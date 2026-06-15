package com.aracecultura.arace.ui.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TelaHomeViewmodel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos

    init {
        observarProdutos()
    }

    private fun observarProdutos() {
        viewModelScope.launch {
            produtosFlow()
                .catch { _produtos.value = emptyList() }
                .collect { lista -> _produtos.value = lista }
        }
    }

    private fun produtosFlow(): Flow<List<Produto>> = callbackFlow {
        val registro = db.collection("Produtos")
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Produto::class.java)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { registro.remove() }
    }
}