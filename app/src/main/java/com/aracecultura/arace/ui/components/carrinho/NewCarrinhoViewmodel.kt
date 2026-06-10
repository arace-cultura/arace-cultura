package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewCarrinhoViewModel : ViewModel() {

    init {
        Log.d("CarrinhoDebug", "ViewModel CRIADO: ${this.hashCode()}")
    }

    private val db: FirebaseFirestore = Firebase.firestore

    private val _estado = MutableStateFlow<EstadoCarrinho>(EstadoCarrinho.Carregando)
    val estado: StateFlow<EstadoCarrinho> = _estado.asStateFlow()

    fun carregarCarrinho(uid: String) {
        Log.d("CarrinhoDebug", "carregarCarrinho() chamado (vm=${this.hashCode()})")
        viewModelScope.launch {
            // Só mostra skeleton se ainda não há conteúdo: recargas com a tela
            // já populada atualizam silenciosamente, sem resetar o scroll
            if (_estado.value !is EstadoCarrinho.Pronto) {
                _estado.value = EstadoCarrinho.Carregando
            }
            val itens = withContext(Dispatchers.IO) { buscarItensDoCarrinho(uid) }
            _estado.value = EstadoCarrinho.Pronto(itens)
        }
    }

    private suspend fun buscarItensDoCarrinho(uid: String): List<ItemCarrinho> {
        return try {
            val snapshot = db.collection("Carrinho")
                .document(uid)
                .collection("Produtos")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val produto = doc.toObject(Produto::class.java) ?: run {
                    Log.e("Carrinho", "Documento ${doc.id} não pôde ser mapeado como Produto")
                    return@mapNotNull null
                }
                ItemCarrinho(
                    id = doc.id,
                    produto = produto,
                    quantidade = doc.getLong("quantidade")?.toInt() ?: 1
                )
            }
        } catch (e: Exception) {
            Log.e("Carrinho", "Falha ao buscar carrinho", e)
            emptyList()
        }
    }

    fun removerItem(item: ItemCarrinho, uid: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.collection("Carrinho")
                        .document(uid)
                        .collection("Produtos")
                        .document(item.id)
                        .delete()
                        .await()
                }
                val estadoAtual = _estado.value
                if (estadoAtual is EstadoCarrinho.Pronto) {
                    _estado.value = EstadoCarrinho.Pronto(
                        estadoAtual.itens.filter { it.id != item.id }
                    )
                }
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao remover item ${item.id}", e)
            }
        }
    }

    fun alterarQuantidade(item: ItemCarrinho, uid: String, novaQuantidade: Int) {
        Log.d("CarrinhoDebug", "alterarQuantidade: item=${item.id.takeLast(5)} nova=$novaQuantidade")
        if (novaQuantidade <= 0) {
            removerItem(item, uid)
            return
        }

        val estadoAnterior = _estado.value
        atualizarQuantidadeLocal(item.id, novaQuantidade)

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.collection("Carrinho")
                        .document(uid)
                        .collection("Produtos")
                        .document(item.id)
                        .update("quantidade", novaQuantidade)
                        .await()
                }
                Log.d("CarrinhoDebug", "update Firestore OK: item=${item.id.takeLast(5)}")
            } catch (e: Exception) {
                _estado.value = estadoAnterior
                Log.e("CarrinhoDebug", "update Firestore FALHOU, estado restaurado", e)
            }
        }
    }

    private fun atualizarQuantidadeLocal(itemId: String, novaQuantidade: Int) {
        val estadoAtual = _estado.value
        if (estadoAtual is EstadoCarrinho.Pronto) {
            _estado.value = EstadoCarrinho.Pronto(
                estadoAtual.itens.map { item ->
                    if (item.id == itemId) item.copy(quantidade = novaQuantidade) else item
                }
            )
        }
    }
}
