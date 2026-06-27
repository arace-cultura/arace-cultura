package com.aracecultura.arace.ui.components.vendas

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aracecultura.arace.data.model.Envio
import com.aracecultura.arace.data.model.StatusEnvio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Lê em tempo real as vendas de uma loja (coleção "Envios" filtrada por
 * produtorId) e expõe as transições de status disparadas pelos botões.
 */
class VendasViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var registro: ListenerRegistration? = null
    private var produtorObservado: String? = null

    private val _vendas = MutableStateFlow<List<Envio>>(emptyList())
    val vendas: StateFlow<List<Envio>> = _vendas

    fun carregar(produtorId: String) {
        if (produtorId.isBlank() || produtorId == produtorObservado) return
        produtorObservado = produtorId
        registro?.remove()
        registro = db.collection("Envios")
            .whereEqualTo("produtorId", produtorId)
            .addSnapshotListener { snap, erro ->
                if (erro != null) {
                    Log.e("Vendas", "Erro ao observar vendas", erro)
                    return@addSnapshotListener
                }
                _vendas.value = snap?.documents
                    ?.mapNotNull { it.toObject(Envio::class.java) }
                    ?.sortedByDescending { it.criadoEm }
                    ?: emptyList()
            }
    }

    /** "confirmar" no cartão de pagamento: pagamento confirmado → vai para envio. */
    fun confirmarPagamento(envio: Envio) = atualizarStatus(envio, StatusEnvio.ENVIO)

    /** "confirmar entrega" no cartão de envio: entregue. */
    fun confirmarEntrega(envio: Envio) = atualizarStatus(envio, StatusEnvio.ENTREGUE)

    /** "cancelar envio": remove o registro. */
    fun cancelarEnvio(envio: Envio) {
        if (envio.id.isBlank()) return
        db.collection("Envios").document(envio.id).delete()
            .addOnFailureListener { Log.e("Vendas", "Erro ao cancelar envio ${envio.id}", it) }
    }

    private fun atualizarStatus(envio: Envio, novo: StatusEnvio) {
        if (envio.id.isBlank()) return
        db.collection("Envios").document(envio.id).update("status", novo.name)
            .addOnFailureListener { Log.e("Vendas", "Erro ao atualizar status ${envio.id}", it) }
    }

    override fun onCleared() {
        registro?.remove()
        super.onCleared()
    }
}
