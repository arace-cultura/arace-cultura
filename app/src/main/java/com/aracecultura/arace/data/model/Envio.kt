package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Estados de um envio. Decide o tipo de cartão exibido. PAGAMENTO → ENVIO →
 * ENTREGUE é o fluxo normal; CANCELADO é estado terminal: o produtor cancelou,
 * o estoque já voltou e o cliente vê o pedido como cancelado (cartão cinza) em
 * Meus Pedidos até dispensá-lo.
 */
enum class StatusEnvio { PAGAMENTO, ENVIO, ENTREGUE, CANCELADO }

/**
 * Um item vendido a ser enviado por uma loja. Coleção própria ("Envios"), à
 * parte do Produto: cada compra finalizada gera um Envio por item, e o produtor
 * acompanha/transiciona o status (PAGAMENTO → ENVIO → ENTREGUE).
 */
@Immutable
data class Envio(
    @DocumentId val id: String = "",
    val produtoId: String = "",
    val produtorId: String = "",
    val compradorId: String = "",
    val telefoneComprador: String = "",
    val nomeComprador: String = "",
    val nome: String = "",
    val descricao: String = "",
    val imagem: String = "",
    val preco: Double = 0.0,
    val quantidade: Int = 1,
    val status: String = StatusEnvio.PAGAMENTO.name,
    val criadoEm: Timestamp? = null,
)

/** Status como enum, tolerante a valores desconhecidos. */
val Envio.statusEnum: StatusEnvio
    get() = runCatching { StatusEnvio.valueOf(status) }.getOrDefault(StatusEnvio.PAGAMENTO)
