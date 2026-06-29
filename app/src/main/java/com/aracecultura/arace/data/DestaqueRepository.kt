package com.aracecultura.arace.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Destaque manual: o produtor escolhe, na tela de destaques, quais dos seus
 * produtos ficam em destaque. Vive numa coleção própria ("Destaques"), com
 * doc id = produtoId, independente do destaque automático (CarrinhosContador).
 */
const val COLECAO_DESTAQUES = "Destaques"
const val CAMPO_PRODUTOR_ID = "produtorId"

/** Marca [produtoId] como destaque da loja [produtorId]. */
suspend fun destacarProduto(
    db: FirebaseFirestore,
    produtoId: String,
    produtorId: String,
) {
    if (produtoId.isBlank() || produtorId.isBlank()) return
    db.collection(COLECAO_DESTAQUES).document(produtoId)
        .set(mapOf(CAMPO_PRODUTOR_ID to produtorId))
        .await()
}

/** Remove [produtoId] dos destaques. */
suspend fun removerDestaque(
    db: FirebaseFirestore,
    produtoId: String,
) {
    if (produtoId.isBlank()) return
    db.collection(COLECAO_DESTAQUES).document(produtoId).delete().await()
}
