package com.aracecultura.arace.ui.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.R
import com.aracecultura.arace.data.CampoCadastro
import com.aracecultura.arace.data.CepRepository
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.ResultadoCep
import com.aracecultura.arace.data.Validacoes
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.supabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed interface ResultadoCadastro {
    data object Idle : ResultadoCadastro
    data object Salvando : ResultadoCadastro
    data object Sucesso : ResultadoCadastro
    data class Erro(val mensagem: String) : ResultadoCadastro
    /** Validação reprovada: campos a corrigir, exibidos no pop-up ao final. */
    data class DadosInvalidos(val campos: List<CampoCadastro>) : ResultadoCadastro
}

/** Passos internos do fluxo de cadastro de produtor (uma única rota, três etapas). */
enum class PassoCadastro { DADOS_LOJA, DADOS_FISCAIS, DETALHES }

class CadastroProdutorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _resultado = MutableStateFlow<ResultadoCadastro>(ResultadoCadastro.Idle)
    val resultado: StateFlow<ResultadoCadastro> = _resultado.asStateFlow()

    // Rascunho compartilhado pelas telas do cadastro. Como o ViewModel é
    // escopado ao nav graph do fluxo (navGraphViewModels), voltar entre as
    // telas não perde nada: cada tela lê o rascunho ao abrir e grava ao sair.
    private val _draft = MutableStateFlow(Produtor())
    val draft: StateFlow<Produtor> = _draft.asStateFlow()

    private val _senhaLoja = MutableStateFlow("")
    val senhaLoja: StateFlow<String> = _senhaLoja.asStateFlow()

    // Imagens escolhidas: guardadas no ViewModel (e não no fragment) para
    // sobreviverem à navegação de volta entre as telas.
    var bannerUri: Uri? = null
    var fotoLojaUri: Uri? = null
    var fotosHistoriaUris: List<Uri> = emptyList()

    fun atualizarDraft(transform: (Produtor) -> Produtor) {
        _draft.value = transform(_draft.value)
    }

    fun atualizarSenha(senha: String) {
        _senhaLoja.value = senha
    }

    /**
     * Marca o resultado como consumido. Como [resultado] é um StateFlow que retém
     * o último valor, sem isto um evento pontual (pop-up de dados inválidos, erro)
     * seria reemitido ao reentrar na tela — reabrindo um pop-up já fechado.
     */
    fun consumirResultado() {
        _resultado.value = ResultadoCadastro.Idle
    }

    /**
     * Valida todos os dados do cadastro e, se tudo estiver correto, persiste a
     * loja. Os campos verificáveis localmente (nome, documento, telefone, senha
     * forte, etc.) são checados na hora; o CEP é confirmado via API (ViaCEP).
     * Reprovações viram [ResultadoCadastro.DadosInvalidos] para o pop-up final.
     */
    fun validarECadastrar(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _resultado.value = ResultadoCadastro.Erro("Usuário não autenticado")
            return
        }
        val produtor = _draft.value
        val senhaLoja = _senhaLoja.value
        val pessoaJuridica = produtor.tipoPessoa == "PJ"

        viewModelScope.launch {
            _resultado.value = ResultadoCadastro.Salvando

            val invalidos = mutableListOf<CampoCadastro>()
            if (!Validacoes.nomeCompletoValido(produtor.nomeCompleto)) invalidos += CampoCadastro.NOME
            if (!Validacoes.preenchido(produtor.nomeLoja)) invalidos += CampoCadastro.NOME_LOJA
            if (pessoaJuridica) {
                if (!Validacoes.preenchido(produtor.razaoSocial)) invalidos += CampoCadastro.RAZAO_SOCIAL
                if (!Validacoes.cnpjValido(produtor.cnpj)) invalidos += CampoCadastro.CNPJ
            } else {
                if (!Validacoes.cpfValido(produtor.cnpj)) invalidos += CampoCadastro.CPF
            }
            if (!Validacoes.telefoneValido(produtor.telefone)) invalidos += CampoCadastro.TELEFONE
            if (!Validacoes.preenchido(produtor.endereco)) invalidos += CampoCadastro.ENDERECO
            if (!Validacoes.preenchido(produtor.tipoArtesanato)) invalidos += CampoCadastro.TIPO_ARTESANATO
            if (!Validacoes.preenchido(produtor.categoriaProduto)) invalidos += CampoCadastro.CATEGORIA
            if (!Validacoes.preenchido(produtor.chavePix)) invalidos += CampoCadastro.CHAVE_PIX
            if (!Validacoes.senhaForte(senhaLoja)) invalidos += CampoCadastro.SENHA

            // CEP: formato local primeiro; só consulta a API se o formato passar.
            if (!Validacoes.cepFormatoValido(produtor.cep)) {
                invalidos += CampoCadastro.CEP
            } else {
                when (CepRepository.consultar(produtor.cep)) {
                    is ResultadoCep.NaoEncontrado -> invalidos += CampoCadastro.CEP
                    ResultadoCep.ErroRede -> {
                        // Sem rede não dá para confirmar o CEP: pede para tentar de novo
                        // em vez de reprovar um CEP possivelmente válido.
                        _resultado.value =
                            ResultadoCadastro.Erro(context.getString(R.string.erro_cep_rede))
                        return@launch
                    }
                    is ResultadoCep.Valido -> Unit
                }
            }

            if (invalidos.isNotEmpty()) {
                _resultado.value = ResultadoCadastro.DadosInvalidos(invalidos)
                return@launch
            }

            persistirProdutor(context, uid, produtor, senhaLoja)
        }
    }

    private suspend fun persistirProdutor(
        context: Context,
        uid: String,
        produtor: Produtor,
        senhaLoja: String,
    ) {
        try {
            val bannerUrl = bannerUri?.let {
                uploadImagemProdutor(context, uid, it, "banner")
            }.orEmpty()

            val fotoLojaUrl = fotoLojaUri?.let {
                uploadImagemProdutor(context, uid, it, "foto-loja")
            }.orEmpty()

            val fotosHistoriaUrls = fotosHistoriaUris.mapIndexed { index, uri ->
                uploadImagemProdutor(context, uid, uri, "historia-$index")
            }

            // Cria a loja com id próprio (compartilhável entre contas),
            // grava o hash da senha e vincula esta conta a ela
            LojaRepository.criarLoja(
                uid = uid,
                produtor = produtor.copy(
                    banner = bannerUrl,
                    fotoLoja = fotoLojaUrl,
                    fotosHistoria = fotosHistoriaUrls
                ),
                senha = senhaLoja
            )

            _resultado.value = ResultadoCadastro.Sucesso
        } catch (e: Exception) {
            _resultado.value = ResultadoCadastro.Erro(e.message ?: "Erro ao salvar produtor")
        }
    }

    private suspend fun uploadImagemProdutor(
        context: Context,
        uid: String,
        imageUri: Uri,
        prefixo: String
    ): String {
        val imageBytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
            ?: throw Exception("Não foi possível processar a imagem.")

        val bucket = supabase.storage.from("imagens")
        val caminhoSeguro = "$uid/produtor/$prefixo-${UUID.randomUUID()}.jpg"

        bucket.upload(path = caminhoSeguro, data = imageBytes) {
            upsert = true
        }

        return bucket.publicUrl(caminhoSeguro)
    }

    private val _passo = MutableStateFlow(PassoCadastro.DADOS_LOJA)
    val passo: StateFlow<PassoCadastro> = _passo.asStateFlow()

    fun proximoPasso() {
        _passo.value = when (_passo.value) {
            PassoCadastro.DADOS_LOJA    -> PassoCadastro.DADOS_FISCAIS
            PassoCadastro.DADOS_FISCAIS -> PassoCadastro.DETALHES
            PassoCadastro.DETALHES      -> PassoCadastro.DETALHES
        }
    }

    /** Retorna false se já está no primeiro passo (aí o caller sai do fluxo). */
    fun passoAnterior(): Boolean = when (_passo.value) {
        PassoCadastro.DADOS_LOJA    -> false
        PassoCadastro.DADOS_FISCAIS -> { _passo.value = PassoCadastro.DADOS_LOJA;    true }
        PassoCadastro.DETALHES      -> { _passo.value = PassoCadastro.DADOS_FISCAIS; true }
    }

}
