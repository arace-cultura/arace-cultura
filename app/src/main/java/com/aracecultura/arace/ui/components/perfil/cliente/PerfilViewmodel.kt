package com.aracecultura.arace.ui.components.perfil.cliente

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.ImagemRepository
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.SenhaIncorretaException
import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val fotoUrl: String = "",
    val bannerUrl: String = "",
    val isProdutor: Boolean = false
)

class PerfilViewModel : ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore

    private val _usuario = MutableStateFlow(Usuario())
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()

    // Cadastro de produtor atrelado à conta (null se a conta não é produtora)
    private val _produtor = MutableStateFlow<Produtor?>(null)
    val produtor: StateFlow<Produtor?> = _produtor.asStateFlow()

    // Guards: evitam empilhar listeners ao recompor (cada um observa uma vez).
    private var uidUsuarioObservado: String? = null
    private var uidProdutorObservado: String? = null

    fun carregarDadosProdutor(uid: String) {
        if (uid.isBlank() || uid == uidProdutorObservado) return
        uidProdutorObservado = uid
        viewModelScope.launch {
            val lojaId = try {
                withContext(Dispatchers.IO) { LojaRepository.resolverLojaId(uid) }
            } catch (e: Exception) {
                null
            }
            if (lojaId == null) {
                _produtor.value = null
                return@launch
            }
            // Tempo real: o cadastro da loja reflete edições na hora.
            produtorDocFlow(lojaId)
                .catch { _produtor.value = null }
                .collect { _produtor.value = it }
        }
    }

    // Observa os dados do usuário em tempo real ao abrir o perfil.
    fun carregarDadosUsuario(uid: String) {
        if (uid.isBlank() || uid == uidUsuarioObservado) return
        uidUsuarioObservado = uid
        viewModelScope.launch {
            usuarioDocFlow(uid)
                .catch { it.printStackTrace() }
                .collect { document ->
                    val possuiCadastroProdutor = withContext(Dispatchers.IO) {
                        LojaRepository.resolverLojaId(uid) != null
                    }
                    val emailAutenticado = FirebaseAuth.getInstance().currentUser?.email.orEmpty()

                    val userData = document.toObject(Usuario::class.java)
                        ?.copy(id = document.id, isProdutor = possuiCadastroProdutor)

                    if (userData != null) {
                        _usuario.value = userData.copy(
                            email = userData.email.ifBlank { emailAutenticado }
                        )
                        if (possuiCadastroProdutor && document.getBoolean("isProdutor") != true) {
                            withContext(Dispatchers.IO) {
                                db.collection("Usuarios")
                                    .document(uid)
                                    .set(mapOf("isProdutor" to true), SetOptions.merge())
                                    .await()
                            }
                        }
                    } else {
                        _usuario.value = Usuario(id = uid, nome = "Usuário", email = emailAutenticado, isProdutor = possuiCadastroProdutor)
                    }
                }
        }
    }

    private fun produtorDocFlow(lojaId: String): Flow<Produtor?> = callbackFlow {
        val registro = db.collection("Produtores").document(lojaId)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Produtor::class.java))
            }
        awaitClose { registro.remove() }
    }

    private fun usuarioDocFlow(uid: String): Flow<DocumentSnapshot> = callbackFlow {
        val registro = db.collection("Usuarios").document(uid)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                if (snapshot != null) trySend(snapshot)
            }
        awaitClose { registro.remove() }
    }

    // Altera o modo de visualização entre Cliente e Produtor
    fun alterarModoVisualizacao(isProdutor: Boolean, uid: String) {
        // Guarda o estado anterior caso a requisição falhe
        val estadoAnterior = _usuario.value
        _usuario.value = _usuario.value.copy(isProdutor = isProdutor)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Usuarios").document(uid)
                    .update("isProdutor", isProdutor)
                    .await()
            } catch (e: Exception) {
                _usuario.value = estadoAnterior
            }
        }
    }
    // Troca a senha da loja. Distingue "senha atual incorreta" (onSenhaIncorreta)
    // dos demais erros, para a UI mostrar o texto vermelho no campo certo.
    fun alterarSenhaLoja(
        uid: String,
        senhaAtual: String,
        senhaNova: String,
        onSucesso: () -> Unit,
        onSenhaIncorreta: () -> Unit,
        onErro: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    LojaRepository.alterarSenhaLoja(uid, senhaAtual, senhaNova)
                }
                onSucesso()
            } catch (e: SenhaIncorretaException) {
                onSenhaIncorreta()
            } catch (e: Exception) {
                onErro(e.message ?: "Não foi possível alterar a senha.")
            }
        }
    }

    // Atualiza os dados editados; URIs novas (foto/banner) são enviadas ao
    // storage antes de persistir as URLs
    fun salvarEdicaoPerfil(
        context: Context,
        novoNome: String,
        uid: String,
        novoTelefone: String = _usuario.value.telefone,
        novaFotoUri: Uri? = null,
        novoBannerUri: Uri? = null,
        onSucesso: () -> Unit = {}
    ) {
        val estadoAnterior = _usuario.value

        viewModelScope.launch {
            try {
                val fotoUrl = novaFotoUri?.let {
                    ImagemRepository.upload(context, uid, "perfil", it)
                } ?: estadoAnterior.fotoUrl
                val bannerUrl = novoBannerUri?.let {
                    ImagemRepository.upload(context, uid, "banner", it)
                } ?: estadoAnterior.bannerUrl

                _usuario.value = estadoAnterior.copy(
                    nome = novoNome, telefone = novoTelefone, fotoUrl = fotoUrl, bannerUrl = bannerUrl
                )

                withContext(Dispatchers.IO) {
                    val updates = mapOf(
                        "nome" to novoNome,
                        "telefone" to novoTelefone,
                        "fotoUrl" to fotoUrl,
                        "bannerUrl" to bannerUrl
                    )
                    db.collection("Usuarios").document(uid).update(updates).await()
                }
                // Se salvou com sucesso, executa a ação de voltar de tela
                onSucesso()
            } catch (e: Exception) {
                _usuario.value = estadoAnterior
            }
        }
    }

    fun salvarEdicaoProdutor(
        context: Context,
        uid: String,
        nomeCompleto: String,
        nomeLoja: String,
        tipoPessoa: String,
        razaoSocial: String,
        cnpj: String,
        telefone: String,
        cep: String,
        endereco: String,
        tipoArtesanato: String,
        categoriaProduto: String,
        historia: String,
        chavePix: String,
        novoBannerUri: Uri? = null,
        novaFotoLojaUri: Uri? = null,
        novasFotosHistoriaUris: List<Uri?> = emptyList(),
        onSucesso: () -> Unit = {},
        onErro: (String) -> Unit = {}
    ) {
        val estadoAnterior = _produtor.value ?: run {
            onErro("Cadastro de produtor nao encontrado.")
            return
        }

        viewModelScope.launch {
            try {
                val lojaId = withContext(Dispatchers.IO) {
                    LojaRepository.resolverLojaId(uid)
                } ?: throw IllegalStateException("Conta sem loja vinculada.")

                val bannerUrl = novoBannerUri?.let {
                    ImagemRepository.upload(context, lojaId, "banner-loja", it)
                } ?: estadoAnterior.banner
                val fotoLojaUrl = novaFotoLojaUri?.let {
                    ImagemRepository.upload(context, lojaId, "foto-loja", it)
                } ?: estadoAnterior.fotoLoja
                val fotosHistoriaUrls = List(3) { index ->
                    novasFotosHistoriaUris.getOrNull(index)?.let {
                        ImagemRepository.upload(context, lojaId, "historia-$index", it)
                    } ?: estadoAnterior.fotosHistoria.getOrNull(index).orEmpty()
                }

                val produtorAtualizado = estadoAnterior.copy(
                    nomeCompleto = nomeCompleto,
                    nomeLoja = nomeLoja,
                    tipoPessoa = tipoPessoa,
                    razaoSocial = razaoSocial,
                    cnpj = cnpj,
                    telefone = telefone,
                    cep = cep,
                    endereco = endereco,
                    tipoArtesanato = tipoArtesanato,
                    categoriaProduto = categoriaProduto,
                    banner = bannerUrl,
                    fotoLoja = fotoLojaUrl,
                    fotosHistoria = fotosHistoriaUrls,
                    historia = historia,
                    chavePix = chavePix
                )

                _produtor.value = produtorAtualizado

                withContext(Dispatchers.IO) {
                    val updates = mapOf(
                        "nomeCompleto" to nomeCompleto,
                        "nomeLoja" to nomeLoja,
                        "tipoPessoa" to tipoPessoa,
                        "razaoSocial" to razaoSocial,
                        "cnpj" to cnpj,
                        "telefone" to telefone,
                        "cep" to cep,
                        "endereco" to endereco,
                        "tipoArtesanato" to tipoArtesanato,
                        "categoriaProduto" to categoriaProduto,
                        "banner" to bannerUrl,
                        "fotoLoja" to fotoLojaUrl,
                        "fotosHistoria" to fotosHistoriaUrls,
                        "historia" to historia,
                        "chavePix" to chavePix
                    )
                    db.collection("Produtores")
                        .document(lojaId)
                        .set(updates, SetOptions.merge())
                        .await()
                }
                onSucesso()
            } catch (e: Exception) {
                _produtor.value = estadoAnterior
                onErro(e.message ?: "Nao foi possivel salvar os dados da loja.")
            }
        }
    }
}
