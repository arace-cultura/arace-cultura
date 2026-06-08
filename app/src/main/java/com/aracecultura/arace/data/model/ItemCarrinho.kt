package com.aracecultura.arace.data.model

import com.google.firebase.firestore.DocumentId

data class ItemCarrinho(
    @DocumentId
    val id: String = "",
    val produto: Produto = Produto(),
    val quantidade: Int = 1
)