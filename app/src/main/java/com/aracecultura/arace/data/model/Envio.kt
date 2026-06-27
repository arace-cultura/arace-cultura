package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/** Estados de um envio. Decide o tipo de cartão exibido na Tela de Vendas. */
enum class StatusEnvio { PAGAMENTO, ENVIO, ENTREGUE }

/**
 * Um item vendido a ser enviado por uma loja. Coleção própria ("Envios"), à
 * parte do Produto: cada compra finalizada gera um Envio por item, e o produtor
 * acompanha/transiciona o status (PAGAMENTO → ENVIO → ENTREGUE).
 */
@Immutable
data class Envio(
    @DocumentId val id: String = "",
    val produtoId: String = "",
    val produtorId: String = "",   // loja responsável pelo envio (dono desta tela)
    val compradorId: String = "",
    val nome: String = "",
    val descricao: String = "",
    val imagem: String = "",
    val preco: Double = 0.0,
    val quantidade: Int = 1,
    // Guardado como String (nome do enum) para o mapeamento do Firestore.
    val status: String = StatusEnvio.PAGAMENTO.name,
    val criadoEm: Timestamp? = null,
)

/** Status como enum, tolerante a valores desconhecidos. */
val Envio.statusEnum: StatusEnvio
    get() = runCatching { StatusEnvio.valueOf(status) }.getOrDefault(StatusEnvio.PAGAMENTO)
