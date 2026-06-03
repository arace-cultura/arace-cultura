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

// Data class para representar os dados do usuário
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
                val userData = document.toObject(Usuario::class.java)
                if (userData != null) {
                    _usuario.value = userData
                } else {
                    // Fallback para visualização caso o documento não exista
                    _usuario.value = Usuario(id = uid, nome = "Usuário", email = "usuario@gmail.com")
                }
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    // Altera o modo de visualização entre Cliente e Produtor
    fun alterarModoVisualizacao(isProdutor: Boolean, uid: String) {
        _usuario.value = _usuario.value.copy(isProdutor = isProdutor)

        // Salva a preferência no Firestore em background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Usuarios").document(uid)
                    .update("isProdutor", isProdutor)
                    .await()
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }

    // Atualiza os dados editados (ex: nome, nova foto)
    fun salvarEdicaoPerfil(novoNome: String, novaFotoUrl: String, uid: String) {
        viewModelScope.launch {
            // 1. Atualiza o estado da UI instantaneamente
            _usuario.value = _usuario.value.copy(nome = novoNome, fotoUrl = novaFotoUrl)

            // 2. Persiste no banco de dados
            try {
                withContext(Dispatchers.IO) {
                    val updates = mapOf(
                        "nome" to novoNome,
                        "fotoUrl" to novaFotoUrl
                    )
                    db.collection("Usuarios").document(uid).update(updates).await()
                }
            } catch (e: Exception) {
                // Tratar erro (ex: reverter estado, mostrar toast)
            }
        }
    }
}