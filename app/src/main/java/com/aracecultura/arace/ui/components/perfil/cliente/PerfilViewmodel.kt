package com.aracecultura.arace.ui.components.perfil.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
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
    val isProdutor: Boolean = false
)

class PerfilViewModel : ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore

    private val _usuario = MutableStateFlow(Usuario())
    val usuario: StateFlow<Usuario> = _usuario.asStateFlow()

    // Busca os dados do usuário no Firestore ao abrir o perfil
    fun carregarDadosUsuario(uid: String) {
        viewModelScope.launch {
            try {
                val document = withContext(Dispatchers.IO) {
                    db.collection("Usuarios").document(uid).get().await()
                }

                // Mapeia o documento para o objeto, adicionando também o ID do documento por segurança
                val userData = document.toObject(Usuario::class.java)?.copy(id = document.id)

                if (userData != null) {
                    _usuario.value = userData
                } else {
                    _usuario.value = Usuario(id = uid, nome = "Usuário", email = "usuario@gmail.com")
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
    // Atualiza os dados editados
    fun salvarEdicaoPerfil(novoNome: String, novaFotoUrl: String, uid: String, onSucesso: () -> Unit = {}) {
        val estadoAnterior = _usuario.value

        viewModelScope.launch {
            _usuario.value = _usuario.value.copy(nome = novoNome, fotoUrl = novaFotoUrl)
            // Persiste no banco de dados
            try {
                withContext(Dispatchers.IO) {
                    val updates = mapOf(
                        "nome" to novoNome,
                        "fotoUrl" to novaFotoUrl
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