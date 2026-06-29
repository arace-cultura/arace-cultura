package com.aracecultura.arace.ui.components.perfil.cliente

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aracecultura.arace.data.model.Envio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Lê em tempo real os pedidos do cliente (coleção "Envios" filtrada por
 * compradorId) — é a contraparte de [com.aracecultura.arace.ui.components.vendas.VendasViewModel],
 * que lê os mesmos documentos pelo lado do produtor. O nome da loja não vive no
 * Envio, então é resolvido sob demanda a partir de Produtores/{produtorId}.
 */
class MeusPedidosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var registro: ListenerRegistration? = null
    private var compradorObservado: String? = null
    private val cacheNomesLojas = mutableMapOf<String, String>()

    private val _pedidos = MutableStateFlow<List<Envio>>(emptyList())
    val pedidos: StateFlow<List<Envio>> = _pedidos

    private val _nomesLojas = MutableStateFlow<Map<String, String>>(emptyMap())
    val nomesLojas: StateFlow<Map<String, String>> = _nomesLojas

    fun carregar(uid: String) {
        if (uid.isBlank() || uid == compradorObservado) return
        compradorObservado = uid
        registro?.remove()
        registro = db.collection("Envios")
            .whereEqualTo("compradorId", uid)
            .addSnapshotListener { snap, erro ->
                if (erro != null) {
                    Log.e("MeusPedidos", "Erro ao observar pedidos", erro)
                    return@addSnapshotListener
                }
                val pedidos = snap?.documents
                    ?.mapNotNull { it.toObject(Envio::class.java) }
                    ?.sortedByDescending { it.criadoEm }
                    ?: emptyList()
                _pedidos.value = pedidos
                buscarNomesLojasFaltantes(pedidos)
            }
    }

    private fun buscarNomesLojasFaltantes(pedidos: List<Envio>) {
        pedidos.asSequence()
            .map { it.produtorId }
            .filter { it.isNotBlank() }
            .distinct()
            .filterNot { cacheNomesLojas.containsKey(it) }
            .forEach { produtorId ->
                db.collection("Produtores").document(produtorId).get()
                    .addOnSuccessListener { doc ->
                        val nome = doc.getString("nomeLoja")?.takeIf { it.isNotBlank() }
                            ?: doc.getString("nomeCompleto").orEmpty()
                        if (nome.isBlank()) return@addOnSuccessListener
                        cacheNomesLojas[produtorId] = nome
                        _nomesLojas.value = cacheNomesLojas.toMap()
                    }
                    .addOnFailureListener {
                        Log.e("MeusPedidos", "Erro ao buscar loja $produtorId", it)
                    }
            }
    }

    /**
     * "x" no cartão entregue: dispensa o pedido já concluído, removendo o registro.
     * Espelha o [com.aracecultura.arace.ui.components.vendas.VendasViewModel.removerEntregue]
     * do produtor — o Envio é compartilhado, então a remoção vale para os dois lados.
     */
    fun dispensarEntregue(envio: Envio) {
        if (envio.id.isBlank()) return
        db.collection("Envios").document(envio.id).delete()
            .addOnFailureListener { Log.e("MeusPedidos", "Erro ao dispensar pedido ${envio.id}", it) }
    }

    override fun onCleared() {
        registro?.remove()
        super.onCleared()
    }
}
