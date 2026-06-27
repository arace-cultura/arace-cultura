package com.aracecultura.arace.ui.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.COLECAO_CONTADOR_CARRINHOS
import com.aracecultura.arace.data.estaEmDestaque
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TelaHomeViewmodel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos

    init {
        observarProdutos()
    }

    // A home só exibe produtos em destaque. Como o destaque vive na coleção
    // externa CarrinhosContador (≥ 5 carrinhos), combinamos os produtos com o
    // conjunto de ids em destaque e filtramos no cliente.
    private fun observarProdutos() {
        viewModelScope.launch {
            combine(produtosFlow(), destaquesFlow()) { produtos, idsEmDestaque ->
                produtos.filter { it.id in idsEmDestaque }
            }
                .catch { _produtos.value = emptyList() }
                .collect { _produtos.value = it }
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

    private fun destaquesFlow(): Flow<Set<String>> = callbackFlow {
        val registro = db.collection(COLECAO_CONTADOR_CARRINHOS)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                val ids = snapshot?.documents
                    ?.filter { it.estaEmDestaque() }
                    ?.map { it.id }
                    ?.toSet()
                    ?: emptySet()
                trySend(ids)
            }
        awaitClose { registro.remove() }
    }
}
