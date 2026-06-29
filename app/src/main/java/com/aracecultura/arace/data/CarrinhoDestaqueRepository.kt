package com.aracecultura.arace.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Destaque mantido fora do Produto. Uma coleção própria ("CarrinhosContador")
 * guarda, por produto, os ids dos usuários que o têm no carrinho agora. "Em
 * destaque" é derivado na leitura (≥ 5 carrinhos)
 */
const val LIMIAR_DESTAQUE_CARRINHOS = 5
const val COLECAO_CONTADOR_CARRINHOS = "CarrinhosContador"
const val CAMPO_CARRINHOS_IDS = "carrinhosIds"

/** Marca que [uid] tem [produtoId] no carrinho. */
suspend fun registrarProdutoEmCarrinho(
    db: FirebaseFirestore,
    produtoId: String,
    uid: String,
) {
    if (produtoId.isBlank() || uid.isBlank()) return
    db.collection(COLECAO_CONTADOR_CARRINHOS).document(produtoId)
        .set(mapOf(CAMPO_CARRINHOS_IDS to FieldValue.arrayUnion(uid)), SetOptions.merge())
        .await()
}

/** Remove [uid] do contador de [produtoId] (ao tirar do carrinho ou comprar). */
suspend fun removerProdutoDeCarrinho(
    db: FirebaseFirestore,
    produtoId: String,
    uid: String,
) {
    if (produtoId.isBlank() || uid.isBlank()) return
    db.collection(COLECAO_CONTADOR_CARRINHOS).document(produtoId)
        .set(mapOf(CAMPO_CARRINHOS_IDS to FieldValue.arrayRemove(uid)), SetOptions.merge())
        .await()
}

/** Quantos carrinhos distintos contêm o produto (lido do doc do contador). */
fun DocumentSnapshot.carrinhosCount(): Int =
    (get(CAMPO_CARRINHOS_IDS) as? List<*>)?.size ?: 0 // O asterisco ignora o tipo.

/** Deriva o destaque a partir do doc do contador. */
fun DocumentSnapshot.estaEmDestaque(): Boolean =
    carrinhosCount() >= LIMIAR_DESTAQUE_CARRINHOS
