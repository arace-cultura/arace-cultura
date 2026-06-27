package com.aracecultura.arace.ui.components.carrinho

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.data.removerProdutoDeCarrinho
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/** Uma linha da tabela do cartão de checkout (um produto da loja). */
data class LinhaCheckout(
    val nome: String,
    val quantidade: Int,
    val totalLinha: Double
)

/** Dados de um item para gerar o Envio correspondente ao finalizar a compra. */
data class ProdutoEnvio(
    val produtoId: String,
    val nome: String,
    val descricao: String,
    val imagem: String,
    val preco: Double,
    val quantidade: Int
)

/** Um cartão de pagamento = uma loja presente no carrinho. */
data class LojaCheckout(
    val produtorId: String,
    val nomeLoja: String,
    val chavePix: String,
    val itens: List<LinhaCheckout>,
    val total: Double,
    // Ids dos docs no carrinho desta loja, para removê-los ao finalizar.
    val cartItemIds: List<String>,
    // Itens desta loja, para criar um Envio por item ao finalizar.
    val produtosParaEnvio: List<ProdutoEnvio>
)

sealed interface CheckoutUiState {
    data object Carregando : CheckoutUiState
    data object Vazio : CheckoutUiState
    data class Pagamento(val lojas: List<LojaCheckout>) : CheckoutUiState
    data object Confirmacao : CheckoutUiState
}

/**
 * Monta o checkout agrupado por loja: a compra é finalizada por produtor, então
 * cada loja vira um cartão com seus itens, total e chave Pix (buscada do
 * produtor via produtorId). Finalizar uma loja remove só os itens dela.
 */
class FinalizarCompraViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var uidCarregado: String? = null

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Carregando)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun carregar(uid: String) {
        if (uidCarregado == uid) return
        uidCarregado = uid

        if (uid.isBlank()) {
            _uiState.value = CheckoutUiState.Vazio
            return
        }
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Carregando
            val lojas = withContext(Dispatchers.IO) { montarLojas(uid) }
            _uiState.value = if (lojas.isEmpty()) {
                CheckoutUiState.Vazio
            } else {
                CheckoutUiState.Pagamento(lojas)
            }
        }
    }

    private data class ItemCru(val cartId: String, val produto: Produto, val quantidade: Int)

    private suspend fun montarLojas(uid: String): List<LojaCheckout> {
        val itensSnap = db.collection("Carrinho").document(uid)
            .collection("Produtos").get().await()

        val itens = itensSnap.documents.mapNotNull { doc ->
            val p = doc.toObject(Produto::class.java) ?: return@mapNotNull null
            ItemCru(doc.id, p, (doc.getLong("quantidade") ?: 1L).toInt())
        }

        return itens.groupBy { it.produto.produtorId }.map { (produtorId, doGrupo) ->
            var nomeLoja = ""
            var chavePix = ""
            if (produtorId.isNotBlank()) {
                val loja = db.collection("Produtores").document(produtorId)
                    .get().await().toObject(Produtor::class.java)
                nomeLoja = loja?.nomeLoja?.ifBlank { loja.nomeCompleto }.orEmpty()
                chavePix = loja?.chavePix.orEmpty()
            }
            LojaCheckout(
                produtorId = produtorId,
                nomeLoja = nomeLoja.ifBlank { "Produtor" },
                chavePix = chavePix,
                itens = doGrupo.map {
                    LinhaCheckout(it.produto.nome, it.quantidade, it.produto.preco * it.quantidade)
                },
                total = doGrupo.sumOf { it.produto.preco * it.quantidade },
                cartItemIds = doGrupo.map { it.cartId },
                produtosParaEnvio = doGrupo.map {
                    ProdutoEnvio(
                        produtoId = it.cartId,
                        nome = it.produto.nome,
                        descricao = it.produto.descricao,
                        imagem = it.produto.imagens.firstOrNull().orEmpty(),
                        preco = it.produto.preco,
                        quantidade = it.quantidade
                    )
                }
            )
        }
    }

    /**
     * Finaliza o pagamento de uma loja: remove os itens dela do carrinho e o
     * cartão da tela. A remoção da UI é otimista (não espera o servidor).
     */
    fun finalizarLoja(uid: String, loja: LojaCheckout) {
        val pagamento = _uiState.value as? CheckoutUiState.Pagamento ?: return
        if (pagamento.lojas.none { it.produtorId == loja.produtorId }) return

        val lojasRestantes = pagamento.lojas.filter { it.produtorId != loja.produtorId }
        _uiState.value = if (lojasRestantes.isEmpty()) {
            CheckoutUiState.Confirmacao
        } else {
            CheckoutUiState.Pagamento(lojasRestantes)
        }

        if (uid.isBlank()) return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val carrinhoRef = db.collection("Carrinho").document(uid)
                    val produtosRef = carrinhoRef.collection("Produtos")
                    val enviosRef = db.collection("Envios")
                    db.runBatch { batch ->
                        loja.cartItemIds.forEach { id ->
                            batch.delete(produtosRef.document(id))
                        }
                        batch.set(
                            carrinhoRef,
                            mapOf(
                                "usuarioId" to uid,
                                "atualizadoEm" to FieldValue.serverTimestamp()
                            ),
                            SetOptions.merge()
                        )
                        // Cada item vendido vira um Envio (status inicial PAGAMENTO),
                        // que a loja acompanha na Tela de Vendas.
                        loja.produtosParaEnvio.forEach { p ->
                            batch.set(
                                enviosRef.document(),
                                mapOf(
                                    "produtoId" to p.produtoId,
                                    "produtorId" to loja.produtorId,
                                    "compradorId" to uid,
                                    "nome" to p.nome,
                                    "descricao" to p.descricao,
                                    "imagem" to p.imagem,
                                    "preco" to p.preco,
                                    "quantidade" to p.quantidade,
                                    "status" to "PAGAMENTO",
                                    "criadoEm" to FieldValue.serverTimestamp()
                                )
                            )
                        }
                    }.await()
                    loja.cartItemIds.forEach { produtoId ->
                        removerProdutoDeCarrinho(db, produtoId, uid)
                    }
                }
            } catch (_: Exception) {
                // Persistência offline reenvia quando voltar a conexão.
            }
        }
    }
}
