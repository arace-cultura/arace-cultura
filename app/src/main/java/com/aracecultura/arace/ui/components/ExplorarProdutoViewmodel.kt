package com.aracecultura.arace.ui.components

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

class ExplorarProdutoViewmodel : ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            val result: List<Produto> = withContext(Dispatchers.IO){
                getAllProducts()
            }
            _produtos.value = result
        }
    }

    private suspend fun getAllProducts():List<Produto>{
        return try{
            db.collection("Produtos")
                .get()
                .await()
                .documents
                .mapNotNull { snapshot ->
                    snapshot.toObject(Produto::class.java)
                }
        }catch (e: Exception){
            emptyList()
        }
    }
}