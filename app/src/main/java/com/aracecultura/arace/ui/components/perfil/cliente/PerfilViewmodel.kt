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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Data class perfeita para o Firestore
data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
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

    fun carregarDadosProdutor(uid: String) {
        viewModelScope.launch {
            try {
                _produtor.value = withContext(Dispatchers.IO) {
                    val lojaId = LojaRepository.resolverLojaId(uid) ?: return@withContext null
                    db.collection("Produtores").document(lojaId).get().await()
                        .toObject(Produtor::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Busca os dados do usuário no Firestore ao abrir o perfil
    fun carregarDadosUsuario(uid: String) {
        viewModelScope.launch {
            try {
                val document = withContext(Dispatchers.IO) {
                    db.collection("Usuarios").document(uid).get().await()
                }
                val possuiCadastroProdutor = withContext(Dispatchers.IO) {
                    LojaRepository.resolverLojaId(uid) != null
                }
                val emailAutenticado = FirebaseAuth.getInstance().currentUser?.email.orEmpty()

                // Mapeia o documento para o objeto, adicionando também o ID do documento por segurança
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
            } catch (e: Exception) {
                e.printStackTrace() // Ajuda a debugar no Logcat caso dê erro
            }
        }
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
                    nome = novoNome, fotoUrl = fotoUrl, bannerUrl = bannerUrl
                )

                withContext(Dispatchers.IO) {
                    val updates = mapOf(
                        "nome" to novoNome,
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
}
