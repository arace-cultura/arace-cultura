package com.aracecultura.arace.ui.components.explorar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExplorarProdutoViewmodel : ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())

    private val _categoriasSelecionadas = MutableStateFlow<Set<String>>(emptySet())
    val categoriasSelecionadas: StateFlow<Set<String>> = _categoriasSelecionadas

    private val _ordenacao = MutableStateFlow("nome")
    val ordenacao: StateFlow<String> = _ordenacao

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val produtosFiltrados: StateFlow<List<Produto>> = combine(
        _produtos, _categoriasSelecionadas, _ordenacao
    ) { todos, categorias, ordem ->
        val filtrados = if (categorias.isEmpty()) todos
                        else todos.filter { p ->
                            p.categorias.any { cat ->
                                categorias.any { it.equals(cat.trim(), ignoreCase = true) }
                            }
                        }
        when (ordem) {
            "preco_asc" -> filtrados.sortedBy { it.preco }
            "preco_desc" -> filtrados.sortedByDescending { it.preco }
            "avaliacao" -> filtrados.sortedByDescending { it.avaliacao }
            else -> filtrados.sortedBy { it.nome }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val result: List<Produto> = withContext(Dispatchers.IO) {
                getAllProducts()
            }
            _produtos.value = result
            _isLoading.value = false
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

    fun toggleCategoria(categoria: String) {
        _categoriasSelecionadas.update { atual ->
            if (categoria in atual) atual - categoria else atual + categoria
        }
    }

    fun setOrdenacao(novaOrdem: String) {
        _ordenacao.value = novaOrdem
    }

    fun adicionarAoCarrinho(produto: Produto, uid: String) {
        if (uid.isBlank()) {
            Log.w("Carrinho", "Tentativa de adicionar ao carrinho sem usuário logado.")
            return
        }

        val itemCarrinho = hashMapOf(
            "nome" to produto.nome,
            "preco" to produto.preco,
            "imagens" to if (produto.imagens.isNotEmpty()) listOf(produto.imagens[0]) else emptyList<String>(),
            "quantidade" to FieldValue.increment(1)
        )

        db.collection("Carrinho")
            .document(uid)
            .collection("Produtos")
            .document(produto.id)
            .set(itemCarrinho, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Carrinho", "Produto ${produto.nome} adicionado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Carrinho", "Erro ao adicionar produto", e)
            }
    }
}
