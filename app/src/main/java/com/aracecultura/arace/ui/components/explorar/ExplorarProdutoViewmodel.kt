package com.aracecultura.arace.ui.components.explorar

import android.util.Log // <-- Import necessário para os testes no Logcat
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
            val result: List<Produto> = withContext(Dispatchers.IO) {
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

    fun adicionarAoCarrinho(produto: Produto, uid: String) {
        // Trava de segurança: se não houver usuário logado, não faz nada
        if (uid.isBlank()) {
            Log.w("Carrinho", "Tentativa de adicionar ao carrinho sem usuário logado.")
            return
        }

        // Formatamos o mapa de dados exatamente com os atributos solicitados
        val itemCarrinho = hashMapOf(
            "nome" to produto.nome,
            "preco" to produto.preco,
            // Pega apenas a primeira imagem da lista (se existir) e coloca dentro de uma nova List
            "imagens" to if (produto.imagens.isNotEmpty()) listOf(produto.imagens[0]) else emptyList<String>()
        )

        // Salva na coleção: Carrinho -> [UID do Usuário] -> Produtos -> [ID do Produto]
        db.collection("Carrinho")
            .document(uid)
            .collection("Produtos")
            .document(produto.id)
            .set(itemCarrinho)
            .addOnSuccessListener {
                Log.d("Carrinho", "Produto ${produto.nome} adicionado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Carrinho", "Erro ao adicionar produto", e)
            }
    }
}