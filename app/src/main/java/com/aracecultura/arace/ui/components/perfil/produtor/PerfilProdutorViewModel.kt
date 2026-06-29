package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.CAMPO_PRODUTOR_ID
import com.aracecultura.arace.data.COLECAO_DESTAQUES
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PerfilProdutorUiState(
    val produtor: Produtor? = null,
    val produtos: List<Produto> = emptyList(),
    val produtosDestaque: List<Produto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PerfilProdutorViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(PerfilProdutorUiState(isLoading = true))
    val uiState: StateFlow<PerfilProdutorUiState> = _uiState.asStateFlow()

    // Mantém só uma observação ativa por vez (troca de loja cancela a anterior).
    private var observacao: Job? = null

    /** Perfil da loja vinculada à conta logada (visão do próprio produtor). */
    fun carregarPerfil(uid: String) {
        if (uid.isBlank()) {
            _uiState.value = PerfilProdutorUiState(errorMessage = "Usuario nao autenticado")
            return
        }
        observar { LojaRepository.resolverLojaId(uid) }
    }

    /** Perfil de uma loja específica pelo id (visão do cliente, somente leitura). */
    fun carregarPerfilPorLoja(lojaId: String) {
        if (lojaId.isBlank()) {
            _uiState.value = PerfilProdutorUiState(errorMessage = "Loja nao encontrada")
            return
        }
        observar { lojaId }
    }

    // Tempo real: resolve o lojaId uma vez (raramente muda) e então observa, via
    // snapshot listeners, o documento da loja e a query de produtos dela. Edições
    // (do dono ou de outro membro) refletem na hora.
    private fun observar(resolverLojaId: suspend () -> String?) {
        observacao?.cancel()
        observacao = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val lojaId = try {
                withContext(Dispatchers.IO) { resolverLojaId() }
            } catch (e: Exception) {
                null
            }
            if (lojaId == null) {
                _uiState.value = PerfilProdutorUiState(errorMessage = "Cadastro de produtor nao encontrado")
                return@launch
            }

            combine(
                produtorFlow(lojaId),
                produtosFlow(lojaId),
                destaquesFlow(lojaId).catch { emit(emptySet()) }
            ) { produtor, produtos, idsEmDestaque ->
                PerfilProdutorUiState(
                    produtor = produtor,
                    produtos = produtos,
                    produtosDestaque = produtos.filter { it.id in idsEmDestaque },
                    errorMessage = if (produtor == null) "Cadastro de produtor nao encontrado" else null
                )
            }
                .catch { e ->
                    _uiState.value = PerfilProdutorUiState(
                        errorMessage = e.message ?: "Erro ao carregar perfil do produtor"
                    )
                }
                .collect { _uiState.value = it }
        }
    }

    private fun produtorFlow(lojaId: String): Flow<Produtor?> = callbackFlow {
        val registro = db.collection("Produtores").document(lojaId)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Produtor::class.java)?.copy(uid = snapshot.id))
            }
        awaitClose { registro.remove() }
    }

    private fun produtosFlow(lojaId: String): Flow<List<Produto>> = callbackFlow {
        val registro = db.collection("Produtos")
            .whereEqualTo("produtorId", lojaId)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Produto::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { registro.remove() }
    }

    private fun destaquesFlow(lojaId: String): Flow<Set<String>> = callbackFlow {
        val registro = db.collection(COLECAO_DESTAQUES)
            .whereEqualTo(CAMPO_PRODUTOR_ID, lojaId)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.id }?.toSet() ?: emptySet())
            }
        awaitClose { registro.remove() }
    }
}
