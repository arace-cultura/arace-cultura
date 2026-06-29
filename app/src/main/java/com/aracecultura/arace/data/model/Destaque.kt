package com.aracecultura.arace.data.model

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentId

/**
 * Destaque manual administrado pelo produtor na tela de destaques. Vive numa
 * coleção própria ("Destaques"), com doc id = produtoId, independente do
 * destaque automático derivado de carrinhos (CarrinhosContador).
 */
@Immutable
data class Destaque(
    @DocumentId val produtoId: String = "",
    val produtorId: String = "",
)
