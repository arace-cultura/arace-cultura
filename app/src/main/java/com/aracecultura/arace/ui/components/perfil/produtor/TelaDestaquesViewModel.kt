package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.CAMPO_PRODUTOR_ID
import com.aracecultura.arace.data.COLECAO_DESTAQUES
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.destacarProduto
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.removerDestaque
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

/** Item da lista de destaques: o produto e se ele está em destaque agora. */
@Immutable
data class ItemDestaque(
    val produto: Produto,
    val emDestaque: Boolean,
)

class TelaDestaquesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _itens = MutableStateFlow<List<ItemDestaque>>(emptyList())
    val itens: StateFlow<List<ItemDestaque>> = _itens.asStateFlow()

    private val _carregando = MutableStateFlow(true)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private var lojaId: String? = null
    private var observacao: Job? = null

    // Lista todos os produtos da loja (cada produto uma vez, ignorando o
    // estoque) combinados, em tempo real, com o conjunto de ids em destaque.
    fun carregar(uid: String) {
        observacao?.cancel()
        observacao = viewModelScope.launch {
            _carregando.value = true
            val id = try {
                withContext(Dispatchers.IO) { LojaRepository.resolverLojaId(uid) }
            } catch (e: Exception) {
                null
            }
            if (id == null) {
                _itens.value = emptyList()
                _carregando.value = false
                return@launch
            }
            lojaId = id

            // A lista de produtos não pode depender da query de destaques: se
            // esta falhar (ex.: regras do Firestore ainda sem a coleção nova),
            // mostramos os produtos mesmo assim, nenhum marcado.
            combine(
                produtosFlow(id),
                destaquesFlow(id).catch { emit(emptySet()) }
            ) { produtos, idsEmDestaque ->
                produtos.map { ItemDestaque(it, it.id in idsEmDestaque) }
            }
                .catch {
                    _itens.value = emptyList()
                    _carregando.value = false
                }
                .collect {
                    _itens.value = it
                    _carregando.value = false
                }
        }
    }

    /** Alterna o destaque do produto; o snapshot listener reflete a mudança. */
    fun alternarDestaque(item: ItemDestaque) {
        val produtorId = lojaId ?: return
        viewModelScope.launch {
            try {
                if (item.emDestaque) removerDestaque(db, item.produto.id)
                else destacarProduto(db, item.produto.id, produtorId)
            } catch (_: Exception) {
            }
        }
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
