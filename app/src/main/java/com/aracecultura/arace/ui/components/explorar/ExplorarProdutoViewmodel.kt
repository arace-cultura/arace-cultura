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
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        observarProdutos()
    }

    // (A) Tempo real: em vez de um get() único (uma fotografia congelada na
    // entrada da tela), um snapshot listener empurra a coleção Produtos sempre
    // que ela muda — sua escrita ou a de outro membro da loja. O Explorar
    // reflete na hora, sem refetch manual nem truque de navegação.
    private fun observarProdutos() {
        viewModelScope.launch {
            produtosFlow()
                .catch { _isLoading.value = false }
                .collect { lista ->
                    _produtos.value = lista
                    _isLoading.value = false
                }
        }
    }

    // O listener é registrado num executor de IO (mapeamento fora da main) e
    // removido em awaitClose quando o viewModelScope morre — sem vazamento.
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

    fun toggleCategoria(categoria: String) {
        _categoriasSelecionadas.update { atual ->
            if (categoria in atual) atual - categoria else atual + categoria
        }
    }

    // Tela de categoria: fixa o filtro em uma única categoria. O painel de
    // filtros dessa tela não expõe a seção de Categorias, então o conjunto
    // permanece imutável durante a navegação.
    fun fixarCategoria(categoria: String) {
        _categoriasSelecionadas.value = setOf(categoria)
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
            // Denormalizado para o checkout agrupar o pagamento por loja
            "produtorId" to produto.produtorId,
            "quantidade" to FieldValue.increment(1)
        )

        db.collection("Carrinho")
            .document(uid)
            .collection("Produtos")
            .document(produto.id)
            .set(itemCarrinho, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e("Carrinho", "Erro ao adicionar produto", e)
            }
    }
}
