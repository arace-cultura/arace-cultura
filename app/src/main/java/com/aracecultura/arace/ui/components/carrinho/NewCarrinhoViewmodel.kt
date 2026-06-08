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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewCarrinhoViewModel : ViewModel() {

    private val db: FirebaseFirestore = Firebase.firestore

    private val _estado = MutableStateFlow<EstadoCarrinho>(EstadoCarrinho.Carregando)
    val estado: StateFlow<EstadoCarrinho> = _estado

    fun carregarCarrinho(uid: String) {
        viewModelScope.launch {
            _estado.value = EstadoCarrinho.Carregando

            val itens: List<ItemCarrinho> = withContext(Dispatchers.IO) {
                getAllCartProducts(uid)
            }

            _estado.value = EstadoCarrinho.Pronto(itens)
        }
    }

    private suspend fun getAllCartProducts(uid: String): List<ItemCarrinho> {
        return try {
            val snapshot = db.collection("Carrinho")
                .document(uid)
                .collection("Produtos")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val idDocumento = doc.id

                // Pega a quantidade (se não achar, assume 1)
                val qtd = doc.getLong("quantidade")?.toInt() ?: 1

                // O pulo do gato: o documento inteiro É o produto
                val produtoMapeado = doc.toObject(Produto::class.java)

                if (produtoMapeado != null) {
                    ItemCarrinho(
                        id = idDocumento,
                        produto = produtoMapeado,
                        quantidade = qtd
                    )
                } else {
                    Log.e("Carrinho", "Documento $idDocumento falhou ao mapear para Produto.")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("Carrinho", "Falha ao buscar dados", e)
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
                    val listaAtualizada = estadoAtual.itens.filter { it.id != item.id }
                    _estado.value = EstadoCarrinho.Pronto(listaAtualizada)
                }

            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao remover item do carrinho", e)
            }
        }
    }
}