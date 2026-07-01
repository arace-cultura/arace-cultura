package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.registrarProdutoEmCarrinho
import com.aracecultura.arace.data.removerProdutoDeCarrinho
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
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
    // Teto de estoque por produto, lido do BD UMA vez e reusado. Sem cache, cada
    // +/- gera um snapshot -> nova consulta de estoque, e o round-trip de rede
    // trava a manipulação da quantidade. Só consultamos ids ainda desconhecidos.
    private var estoquePorProduto = emptyMap<String, Int>()

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
        estoquePorProduto = emptyMap()
        viewModelScope.launch {
            registrarDocumentoCarrinho(uid)
            itensFlow(uid)
                .catch { _itens.value = emptyList() }
                .collect { itens ->
                    _itens.value = marcarEsgotados(itens)
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
            } catch (_: Exception) {
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

    /**
     * Marca cada item como esgotado usando o estoque da coleção Produtos. O doc
     * do carrinho só tem um snapshot do produto (e seu campo "quantidade" é a
     * quantidade escolhida pelo comprador, não o estoque), então o estoque é
     * buscado à parte — mas só UMA vez por produto: o resultado fica em
     * [estoquePorProduto] e reusado nas emissões seguintes. Assim, alterar a
     * quantidade não dispara nova leitura de estoque a cada toque. Itens cujo
     * estoque não pôde ser lido ficam sem teto (default não-esgotado), para não
     * bloquear o carrinho por um erro de leitura. O checkout revalida o estoque.
     */
    private suspend fun marcarEsgotados(itens: List<ItemCarrinho>): List<ItemCarrinho> {
        if (itens.isEmpty()) return itens

        val faltando = itens.map { it.id }.filter { it !in estoquePorProduto }
        if (faltando.isNotEmpty()) {
            try {
                val novos = mutableMapOf<String, Int>()
                // whereIn aceita no máximo 10 ids por consulta.
                faltando.chunked(10).forEach { grupo ->
                    db.collection("Produtos")
                        .whereIn(FieldPath.documentId(), grupo)
                        .get().await()
                        .documents.forEach { doc ->
                            novos[doc.id] = (doc.getLong("quantidade") ?: 0L)
                                .coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
                        }
                }
                // Produto ausente (ex.: excluído) conta como esgotado (estoque 0).
                faltando.forEach { id -> novos.putIfAbsent(id, 0) }
                estoquePorProduto = estoquePorProduto + novos
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao verificar estoque dos itens", e)
            }
        }

        return itens.map {
            val estoque = estoquePorProduto[it.id] ?: return@map it
            it.copy(estoque = estoque, esgotado = estoque <= 0)
        }
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

    fun adicionarProduto(produto: Produto, uid: String, onConcluido: () -> Unit = {}) {
        if (uid.isBlank() || produto.id.isBlank()) {
            Log.w("Carrinho", "Tentativa de adicionar ao carrinho sem usuario ou produto.")
            return
        }

        val estadoAnterior = _itens.value
        // Teto de estoque: produto.quantidade é o estoque ao vivo (vem da tela
        // do produto). Como a atualização otimista de _itens é síncrona, dois
        // toques seguidos já enxergam a quantidade nova — o teto é à prova de
        // toque-duplo sem leitura extra. No teto, não adiciona, mas segue o
        // fluxo: o "Comprar" ainda navega com o que já está no carrinho.
        val qtdAtual = estadoAnterior?.firstOrNull { it.id == produto.id }?.quantidade ?: 0
        if (qtdAtual >= produto.quantidade) {
            onConcluido()
            return
        }

        if (estadoAnterior != null) {
            val itemExistente = estadoAnterior.firstOrNull { it.id == produto.id }
            _itens.value = if (itemExistente == null) {
                estadoAnterior + ItemCarrinho(id = produto.id, produto = produto, quantidade = 1)
            } else {
                estadoAnterior.map { item ->
                    if (item.id == produto.id) item.copy(quantidade = item.quantidade + 1) else item
                }
            }
        }

        val itemCarrinho = hashMapOf(
            "nome" to produto.nome,
            "descricao" to produto.descricao,
            "preco" to produto.preco,
            "imagens" to if (produto.imagens.isNotEmpty()) listOf(produto.imagens.first()) else emptyList<String>(),
            "produtoId" to produto.id,
            "produtorId" to produto.produtorId,
            "quantidade" to FieldValue.increment(1)
        )

        viewModelScope.launch {
            try {
                val carrinhoRef = db.collection("Carrinho").document(uid)
                val produtoRef = carrinhoRef.collection("Produtos").document(produto.id)
                db.runBatch { batch ->
                    batch.set(
                        carrinhoRef,
                        mapOf(
                            "usuarioId" to uid,
                            "atualizadoEm" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                    batch.set(produtoRef, itemCarrinho, SetOptions.merge())
                }.await()

                runCatching { registrarProdutoEmCarrinho(db, produto.id, uid) }
                onConcluido()
            } catch (e: Exception) {
                _itens.value = estadoAnterior
                Log.e("Carrinho", "Erro ao adicionar produto ${produto.id}", e)
            }
        }
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
            // A contagem de destaque NUNCA pode desfazer a remoção do carrinho.
            runCatching { removerProdutoDeCarrinho(db, item.id, uid) }
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
