package com.aracecultura.arace.ui.components.explorar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.registrarProdutoEmCarrinho
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
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.Locale

class ExplorarProdutoViewmodel : ViewModel() {
    private var db: FirebaseFirestore = Firebase.firestore
    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())

    private val _textoBusca = MutableStateFlow("")
    val textoBusca: StateFlow<String> = _textoBusca

    private val _categoriasSelecionadas = MutableStateFlow<Set<String>>(emptySet())
    val categoriasSelecionadas: StateFlow<Set<String>> = _categoriasSelecionadas

    private val _ordenacao = MutableStateFlow("nome")
    val ordenacao: StateFlow<String> = _ordenacao

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val produtosFiltrados: StateFlow<List<Produto>> = combine(
        _produtos, _categoriasSelecionadas, _ordenacao, _textoBusca
    ) { todos, categorias, ordem, textoBusca ->
        val filtradosPorCategoria = if (categorias.isEmpty()) todos
        else todos.filter { produto ->
            produto.categorias.any { categoriaProduto ->
                categorias.any {
                    it.equals(categoriaProduto.trim(), ignoreCase = true)
                }
            }
        }

        val termosBusca = textoBusca.normalizarParaBusca()
            .split(' ')
            .filter(String::isNotBlank)

        val filtrados = if (termosBusca.isEmpty()) filtradosPorCategoria
        else filtradosPorCategoria.filter { produto ->
            val conteudoPesquisavel = buildString {
                append(produto.nome)
                append(' ')
                append(produto.descricao)
                append(' ')
                append(produto.categorias.joinToString(" "))
            }.normalizarParaBusca()

            termosBusca.all { termo ->
                conteudoPesquisavel.contains(termo)
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

    fun setTextoBusca(texto: String) {
        _textoBusca.value = texto
    }

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
            "descricao" to produto.descricao,
            "preco" to produto.preco,
            "imagens" to if (produto.imagens.isNotEmpty()) listOf(produto.imagens[0]) else emptyList<String>(),
            "produtoId" to produto.id,
            // Denormalizado para o checkout agrupar o pagamento por loja
            "produtorId" to produto.produtorId,
            "quantidade" to FieldValue.increment(1)
        )

        val carrinhoRef = db.collection("Carrinho").document(uid)
        val produtoRef = carrinhoRef.collection("Produtos").document(produto.id)

        viewModelScope.launch {
            try {
                db.runBatch { batch ->
                    batch.set(
                        carrinhoRef,
                        mapOf(
                            "usuarioId" to uid,
                            "atualizadoEm" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                    batch.set(produtoRef, itemCarrinho, SetOptions.merge())
                }.await()
                registrarProdutoEmCarrinho(db, produto.id, uid)
            } catch (e: Exception) {
                Log.e("Carrinho", "Erro ao adicionar produto", e)
            }
        }
    }
}

private val marcasDiacriticas = "\\p{Mn}+".toRegex()

private fun String.normalizarParaBusca(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(marcasDiacriticas, "")
        .lowercase(Locale.ROOT)
        .trim()
