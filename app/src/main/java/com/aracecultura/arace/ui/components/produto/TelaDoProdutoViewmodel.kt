package com.aracecultura.arace.ui.components.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
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

    // Produtor (loja) responsável pelo produto, resolvido via produtorId
    private val _produtor = MutableStateFlow<Produtor?>(null)
    val produtor: StateFlow<Produtor?> = _produtor


    fun carregarProduto(produtoId: String) {
        viewModelScope.launch {
            val documento = withContext(Dispatchers.IO) {
                db.collection("Produtos").document(produtoId).get().await()
            }
            val produtoCarregado = documento.toObject(Produto::class.java)
            _produto.value = produtoCarregado

            // produtorId é o id da loja em Produtores/{id}
            val produtorId = produtoCarregado?.produtorId
            if (!produtorId.isNullOrBlank()) {
                val produtorDoc = withContext(Dispatchers.IO) {
                    db.collection("Produtores").document(produtorId).get().await()
                }
                _produtor.value = produtorDoc.toObject(Produtor::class.java)
            }
        }
    }
}