package com.aracecultura.arace.ui.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.LojaRepository
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
}

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

    fun salvarProdutor(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _resultado.value = ResultadoCadastro.Erro("Usuário não autenticado")
            return
        }

        val produtor = _draft.value
        val senhaLoja = _senhaLoja.value

        viewModelScope.launch {
            _resultado.value = ResultadoCadastro.Salvando
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

    suspend fun isProdutor(uid: String): Boolean {
        return try {
            LojaRepository.resolverLojaId(uid) != null
        } catch (e: Exception) {
            false
        }
    }
}
