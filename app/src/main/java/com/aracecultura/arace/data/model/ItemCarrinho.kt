package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentId

@Immutable
data class ItemCarrinho(
    @DocumentId
    val id: String = "",
    val produto: Produto = Produto(),
    val quantidade: Int = 1,
    // Estoque atual do produto (lido ao vivo da coleção Produtos). O carrinho
    // guarda só um snapshot do produto, então o estoque não vem do doc do
    // carrinho — é resolvido à parte para sinalizar "Esgotado" e limitar o "+".
    // MAX_VALUE = estoque ainda desconhecido (não impõe teto até ser resolvido).
    val estoque: Int = Int.MAX_VALUE,
    val esgotado: Boolean = false
)