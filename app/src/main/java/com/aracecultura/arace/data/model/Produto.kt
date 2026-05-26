package com.aracecultura.arace.data.model

import com.google.firebase.firestore.DocumentId

data class Produto(
    @DocumentId
    val id: String = "",
    val imagem: String = "",
    val nome: String = "",
    val descricao: String = "",
    val avaliacao: Double = 0.0,
    val preco: Double = 0.0
)
