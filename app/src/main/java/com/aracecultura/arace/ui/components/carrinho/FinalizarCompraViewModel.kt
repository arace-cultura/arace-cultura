package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
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
 * Um item cuja quantidade no carrinho passou do estoque atual no momento de
 * finalizar. Alimenta o pop-up que pede para reduzir a quantidade ao disponível.
 */
data class ItemEstoqueInsuficiente(
    val nome: String,
    val solicitado: Int,
    val disponivel: Int
)

/**
 * Monta o checkout agrupado por loja: a compra é finalizada por produtor, então
 * cada loja vira um cartão com seus itens, total e chave Pix (buscada do
 * produtor via produtorId). Finalizar uma loja remove só os itens dela.
 */
class FinalizarCompraViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var uidCarregado: String? = null
    // Telefone do comprador, lido uma vez no carregamento e copiado para cada
    // Envio gerado, para o produtor contatar o cliente na Tela de Vendas.
    private var telefoneComprador: String = ""
    private var nomeComprador: String = ""

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Carregando)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // Itens reprovados na conferência final de estoque. Não-vazio = abre o pop-up
    // pedindo para reduzir a quantidade ao disponível.
    private val _estoqueInsuficiente = MutableStateFlow<List<ItemEstoqueInsuficiente>>(emptyList())
    val estoqueInsuficiente: StateFlow<List<ItemEstoqueInsuficiente>> = _estoqueInsuficiente.asStateFlow()

    fun limparEstoqueInsuficiente() {
        _estoqueInsuficiente.value = emptyList()
    }

    // Lojas com finalização em voo. Sem a remoção otimista, o cartão fica na tela
    // durante o round-trip; isto impede que um toque-duplo dispare a transação
    // duas vezes (toda a mutação ocorre na main thread do viewModelScope).
    private val lojasFinalizando = mutableSetOf<String>()

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
        val comprador = db.collection("Usuarios").document(uid).get().await()
        telefoneComprador = comprador.getString("telefone").orEmpty()
        nomeComprador = comprador.getString("nome").orEmpty()

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
                nomeLoja = nomeLoja,
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

    /** Resultado da conferência final de estoque feita dentro da transação. */
    private sealed interface ResultadoFinalizacao {
        data object Sucesso : ResultadoFinalizacao
        data class EstoqueInsuficiente(val itens: List<ItemEstoqueInsuficiente>) : ResultadoFinalizacao
        data object Erro : ResultadoFinalizacao
    }

    /**
     * Finaliza o pagamento de uma loja. Diferente do resto do app, isto roda numa
     * transação: relê o estoque de cada produto e só efetiva a venda se houver
     * estoque para todos os itens — caso contrário, nada é gravado e a UI abre o
     * pop-up pedindo para reduzir a quantidade ao disponível. Esta conferência
     * exige leitura, então (ao contrário do carrinho) não opera offline.
     */
    fun finalizarLoja(uid: String, loja: LojaCheckout) {
        val pagamento = _uiState.value as? CheckoutUiState.Pagamento ?: return
        if (pagamento.lojas.none { it.produtorId == loja.produtorId }) return
        if (uid.isBlank()) return
        if (!lojasFinalizando.add(loja.produtorId)) return // já em andamento

        viewModelScope.launch {
            val resultado = try {
                withContext(Dispatchers.IO) { efetivarLoja(uid, loja) }
            } finally {
                lojasFinalizando.remove(loja.produtorId)
            }
            when (resultado) {
                is ResultadoFinalizacao.Sucesso -> {
                    // Sucesso confirmado pelo servidor: aí sim some o cartão da loja.
                    val atual = _uiState.value as? CheckoutUiState.Pagamento ?: return@launch
                    val restantes = atual.lojas.filter { it.produtorId != loja.produtorId }
                    _uiState.value = if (restantes.isEmpty()) {
                        CheckoutUiState.Confirmacao
                    } else {
                        CheckoutUiState.Pagamento(restantes)
                    }
                    // Baixa do contador de destaque: best-effort, fora da transação.
                    withContext(Dispatchers.IO) {
                        loja.cartItemIds.forEach { removerProdutoDeCarrinho(db, it, uid) }
                    }
                }
                is ResultadoFinalizacao.EstoqueInsuficiente ->
                    _estoqueInsuficiente.value = resultado.itens
                is ResultadoFinalizacao.Erro -> Unit // rede/etc.: cartão permanece para tentar de novo
            }
        }
    }

    private suspend fun efetivarLoja(uid: String, loja: LojaCheckout): ResultadoFinalizacao {
        val carrinhoRef = db.collection("Carrinho").document(uid)
        val produtosCarrinhoRef = carrinhoRef.collection("Produtos")
        val enviosRef = db.collection("Envios")
        // Refs dos produtos da loja (o id do item no carrinho é o id do produto).
        val produtoRefs = loja.produtosParaEnvio.associateWith {
            db.collection("Produtos").document(it.produtoId)
        }

        return try {
            db.runTransaction { tx ->
                // 1) LEITURAS: estoque atual de cada produto (transação exige todas
                //    as leituras antes das escritas).
                val estoques = produtoRefs.mapValues { (_, ref) ->
                    tx.get(ref).getLong("quantidade") ?: 0L
                }

                // 2) Conferência: algum item passa do estoque? (produto sumido = 0)
                val faltas = loja.produtosParaEnvio.mapNotNull { p ->
                    val disponivel = estoques[p] ?: 0L
                    if (p.quantidade > disponivel) {
                        ItemEstoqueInsuficiente(p.nome, p.quantidade, disponivel.toInt())
                    } else null
                }
                if (faltas.isNotEmpty()) {
                    return@runTransaction ResultadoFinalizacao.EstoqueInsuficiente(faltas)
                }

                // 3) ESCRITAS: baixa o estoque, esvazia o carrinho e gera os Envios.
                loja.produtosParaEnvio.forEach { p ->
                    val ref = produtoRefs.getValue(p)
                    tx.update(ref, "quantidade", (estoques[p] ?: 0L) - p.quantidade)
                }
                loja.cartItemIds.forEach { id -> tx.delete(produtosCarrinhoRef.document(id)) }
                tx.set(
                    carrinhoRef,
                    mapOf("usuarioId" to uid, "atualizadoEm" to FieldValue.serverTimestamp()),
                    SetOptions.merge()
                )
                // Cada item vendido vira um Envio (status inicial PAGAMENTO), que a
                // loja acompanha na Tela de Vendas.
                loja.produtosParaEnvio.forEach { p ->
                    tx.set(
                        enviosRef.document(),
                        mapOf(
                            "produtoId" to p.produtoId,
                            "produtorId" to loja.produtorId,
                            "compradorId" to uid,
                            "telefoneComprador" to telefoneComprador,
                            "nomeComprador" to nomeComprador,
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
                ResultadoFinalizacao.Sucesso
            }.await()
        } catch (e: Exception) {
            Log.e("Checkout", "Erro ao finalizar loja ${loja.produtorId}", e)
            ResultadoFinalizacao.Erro
        }
    }
}
