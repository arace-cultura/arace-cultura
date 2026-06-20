package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NewCarrinhoViewModel : ViewModel() {

    private val db: FirebaseFirestore = Firebase.firestore
    private var uidObservado: String? = null

    // null = ainda carregando (distingue de carrinho vazio)
    private val _itens = MutableStateFlow<List<ItemCarrinho>?>(null)

    // Ordenação local do carrinho (Nome / Preço)
    private val _ordenacao = MutableStateFlow("nome")

    // Estado da UI: combina os itens (tempo-real) com a ordenação escolhida.
    val estado: StateFlow<EstadoCarrinho> = combine(_itens, _ordenacao) { itens, ordem ->
        if (itens == null) EstadoCarrinho.Carregando
        else EstadoCarrinho.Pronto(ordenar(itens, ordem))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoCarrinho.Carregando)

    fun carregarCarrinho(uid: String) {
        if (uid.isBlank() || uid == uidObservado) return
        uidObservado = uid
        viewModelScope.launch {
            registrarDocumentoCarrinho(uid)
            itensFlow(uid)
                .catch { _itens.value = emptyList() }
                .collect { _itens.value = it }
        }
    }

    private suspend fun registrarDocumentoCarrinho(uid: String) {
        try {
            db.collection("Carrinho").document(uid)
                .set(
                    mapOf(
                        "usuarioId" to uid,
                        "atualizadoEm" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                )
                .await()
        } catch (e: Exception) {
            Log.e("Carrinho", "Erro ao registrar documento do carrinho", e)
        }
    }

    // Tempo real: um snapshot listener no carrinho. Alterar quantidade ou
    // remover item reflete na hora (a persistência offline do Firestore dispara
    // o listener já na escrita local), então não há mais update otimista manual.
    private fun itensFlow(uid: String): Flow<List<ItemCarrinho>> = callbackFlow {
        val registro = db.collection("Carrinho").document(uid)
            .collection("Produtos")
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                val itens = snapshot?.documents?.mapNotNull { doc ->
                    val produto = doc.toObject(Produto::class.java) ?: return@mapNotNull null
                    ItemCarrinho(
                        id = doc.id,
                        produto = produto,
                        quantidade = (doc.getLong("quantidade") ?: 1L).toInt()
                    )
                } ?: emptyList()
                trySend(itens)
            }
        awaitClose { registro.remove() }
    }

    private fun ordenar(itens: List<ItemCarrinho>, ordem: String): List<ItemCarrinho> =
        when (ordem) {
            "preco_asc" -> itens.sortedBy { it.produto.preco }
            "preco_desc" -> itens.sortedByDescending { it.produto.preco }
            else -> itens.sortedBy { it.produto.nome.lowercase() }
        }

    fun setOrdenacao(novaOrdem: String) {
        _ordenacao.value = novaOrdem
    }

    fun removerItem(item: ItemCarrinho, uid: String) {
        viewModelScope.launch {
            try {
                val carrinhoRef = db.collection("Carrinho").document(uid)
                val produtoRef = carrinhoRef.collection("Produtos").document(item.id)
                db.runBatch { batch ->
                    batch.delete(produtoRef)
                    batch.set(
                        carrinhoRef,
                        mapOf(
                            "usuarioId" to uid,
                            "atualizadoEm" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                }.await()
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao remover item ${item.id}", e)
            }
        }
    }

    fun alterarQuantidade(item: ItemCarrinho, uid: String, novaQuantidade: Int) {
        if (novaQuantidade <= 0) {
            removerItem(item, uid)
            return
        }
        viewModelScope.launch {
            try {
                val carrinhoRef = db.collection("Carrinho").document(uid)
                val produtoRef = carrinhoRef.collection("Produtos").document(item.id)
                db.runBatch { batch ->
                    batch.update(produtoRef, "quantidade", novaQuantidade)
                    batch.set(
                        carrinhoRef,
                        mapOf(
                            "usuarioId" to uid,
                            "atualizadoEm" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                }.await()
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao alterar quantidade do item ${item.id}", e)
            }
        }
    }
}
