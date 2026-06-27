package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.registrarProdutoEmCarrinho
import com.aracecultura.arace.data.removerProdutoDeCarrinho
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
    private var produtosComMarcadorSincronizado = emptySet<String>()

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
        produtosComMarcadorSincronizado = emptySet()
        viewModelScope.launch {
            registrarDocumentoCarrinho(uid)
            itensFlow(uid)
                .catch { _itens.value = emptyList() }
                .collect { itens ->
                    _itens.value = itens
                    sincronizarMarcadoresDoCarrinho(uid, itens)
                }
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

    private suspend fun sincronizarMarcadoresDoCarrinho(uid: String, itens: List<ItemCarrinho>) {
        val idsParaSincronizar = itens.map { it.id }
            .filter { it !in produtosComMarcadorSincronizado }

        idsParaSincronizar.forEach { produtoId ->
            try {
                registrarProdutoEmCarrinho(db, produtoId, uid)
                produtosComMarcadorSincronizado = produtosComMarcadorSincronizado + produtoId
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao sincronizar destaque do produto $produtoId", e)
            }
        }
    }

    // Tempo real: um snapshot listener no carrinho. Alterar quantidade ou
    // remover item já é refletido na hora por update otimista (ver
    // alterarQuantidade/removerItem, que escrevem em _itens antes do Firestore
    // responder). Assim as três emissões — otimista, snapshot local e snapshot
    // remoto — carregam a mesma lista/altura, sem flicker nem salto de scroll.
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
            "preco_asc" -> itens.sortedWith(
                compareBy<ItemCarrinho> { it.produto.preco }
                    .thenBy { it.produto.nome.lowercase() }
                    .thenBy { it.id }
            )
            "preco_desc" -> itens.sortedWith(
                compareByDescending<ItemCarrinho> { it.produto.preco }
                    .thenBy { it.produto.nome.lowercase() }
                    .thenBy { it.id }
            )
            else -> itens.sortedWith(
                compareBy<ItemCarrinho> { it.produto.nome.lowercase() }
                    .thenBy { it.id }
            )
        }

    fun setOrdenacao(novaOrdem: String) {
        _ordenacao.value = novaOrdem
    }

    fun removerItem(item: ItemCarrinho, uid: String) {
        val estadoAnterior = _itens.value
        if (estadoAnterior != null) {
            _itens.value = estadoAnterior.filterNot { it.id == item.id }
        }

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
                _itens.value = estadoAnterior
                Log.e("Carrinho", "Erro ao remover item ${item.id}", e)
                return@launch
            }
            // Efeito colateral best-effort: a contagem de destaque NUNCA pode
            // desfazer a remoção do carrinho. Se falhar (ex.: regras), só loga.
            runCatching { removerProdutoDeCarrinho(db, item.id, uid) }
                .onFailure { Log.e("Carrinho", "Falha ao baixar destaque de ${item.id}", it) }
        }
    }

    fun alterarQuantidade(item: ItemCarrinho, uid: String, novaQuantidade: Int) {
        if (novaQuantidade <= 0) {
            removerItem(item, uid)
            return
        }

        val estadoAnterior = _itens.value
        if (estadoAnterior != null) {
            _itens.value = estadoAnterior.map { atual ->
                if (atual.id == item.id) atual.copy(quantidade = novaQuantidade) else atual
            }
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
                _itens.value = estadoAnterior
                Log.e("Carrinho", "Erro ao alterar quantidade do item ${item.id}", e)
            }
        }
    }
}
