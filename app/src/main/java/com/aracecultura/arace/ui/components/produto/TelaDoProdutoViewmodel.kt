package com.aracecultura.arace.ui.components.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TelaDoProdutoViewmodel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val _produto = MutableStateFlow<Produto?>(null)
    val produto: StateFlow<Produto?> = _produto


    fun carregarProduto(produtoId: String) {
        viewModelScope.launch {
            val documento = withContext(Dispatchers.IO) {
                db.collection("Produtos").document(produtoId).get().await()
            }
            _produto.value = documento.toObject(Produto::class.java)
        }
    }

    private suspend fun getAllProducts(): List<Produto> {
        return try {
            db.collection("Produtos")
                .get()
                .await()
                .documents
                .mapNotNull { snapshot ->
                    snapshot.toObject(Produto::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}