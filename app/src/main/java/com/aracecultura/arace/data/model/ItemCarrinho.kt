package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentId

@Immutable
data class ItemCarrinho(
    @DocumentId
    val id: String = "",
    val produto: Produto = Produto(),
    val quantidade: Int = 1
)