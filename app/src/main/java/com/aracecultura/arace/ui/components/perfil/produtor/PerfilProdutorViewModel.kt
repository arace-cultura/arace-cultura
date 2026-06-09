package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class PerfilProdutorUiState(
    val produtor: Produtor? = null,
    val produtos: List<Produto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PerfilProdutorViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(PerfilProdutorUiState(isLoading = true))
    val uiState: StateFlow<PerfilProdutorUiState> = _uiState.asStateFlow()

    fun carregarPerfil(uid: String) {
        if (uid.isBlank()) {
            _uiState.value = PerfilProdutorUiState(errorMessage = "Usuario nao autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val (produtor, produtos) = withContext(Dispatchers.IO) {
                    val produtorSnapshot = db.collection("Produtores").document(uid).get().await()
                    val produtosSnapshot = db.collection("Produtos")
                        .whereEqualTo("produtorId", uid)
                        .get()
                        .await()

                    val produtorData = produtorSnapshot.toObject(Produtor::class.java)
                        ?.copy(uid = produtorSnapshot.id)
                    val produtosData = produtosSnapshot.documents.mapNotNull { snapshot ->
                        snapshot.toObject(Produto::class.java)?.copy(id = snapshot.id)
                    }

                    produtorData to produtosData
                }

                _uiState.value = PerfilProdutorUiState(
                    produtor = produtor,
                    produtos = produtos,
                    errorMessage = if (produtor == null) "Cadastro de produtor nao encontrado" else null
                )
            } catch (e: Exception) {
                _uiState.value = PerfilProdutorUiState(
                    errorMessage = e.message ?: "Erro ao carregar perfil do produtor"
                )
            }
        }
    }
}
