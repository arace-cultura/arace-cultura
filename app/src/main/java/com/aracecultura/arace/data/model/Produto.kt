package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentId

@Immutable
data class Produto(
    @DocumentId
    val id: String = "",
    val imagens: List<String> = emptyList(),
    val nome: String = "",
    val categorias: List<String> = emptyList(),
    val descricao: String = "",
    val produtorId: String = "",
    val avaliacao: Double = 0.0,
    val preco: Double = 0.0
)
