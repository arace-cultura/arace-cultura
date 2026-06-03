package com.aracecultura.arace.ui.components.carrinho

import android.util.Log
import androidx.compose.ui.graphics.vector.EmptyPath
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var db: FirebaseFirestore = Firebase.firestore


    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos


    fun getCartProducts(uid: String) {
        viewModelScope.launch {
            val result: List<Produto> = withContext(Dispatchers.IO) {
                getAllCartProducts(uid)
            }
            _produtos.value = result
        }
    }

    private suspend fun getAllCartProducts(uid: String): List<Produto> {
        return try {
            // Acessa a coleção "Carrinho", encontra o documento do usuário pelo UID,
            // e lista os itens salvos na subcoleção "Produtos" (ou o nome que você usar no banco).
            db.collection("Carrinho")
                .document(uid)
                .collection("Produtos")
                .get()
                .await()
                .documents
                .mapNotNull { snapshot ->
                    snapshot.toObject(Produto::class.java)
                }
        } catch (e: Exception) {
            Log.e("Carrinho", "Falha ao converter ou buscar dados", e)
            emptyList()
        }
    }

    fun removerProduto(produto: Produto, uid: String) {
        viewModelScope.launch {
            try {
                // 1. Remove do Firestore usando a mesma rota do UID e o ID do documento do produto
                withContext(Dispatchers.IO) {
                    db.collection("Carrinho")
                        .document(uid)
                        .collection("Produtos")
                        .document(produto.id)
                        .delete()
                        .await()
                }

                // 2. Se a exclusão no banco for bem-sucedida, atualiza o fluxo local instantaneamente
                _produtos.value = _produtos.value.filter { it.id != produto.id }

            } catch (e: Exception) {
                // Opcional: Tratar erro (ex: reverter estado, mostrar log/toast)
            }
        }
    }
}