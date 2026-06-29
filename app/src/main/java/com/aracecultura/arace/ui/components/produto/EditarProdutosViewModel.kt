package com.aracecultura.arace.ui.components.produto

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.ImagemRepository
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditarProdutosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _produtos = MutableStateFlow<List<Produto>>(emptyList())
    val produtos: StateFlow<List<Produto>> = _produtos.asStateFlow()

    private val _carregando = MutableStateFlow(true)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    // Mensagem efêmera para Toast (salvo/excluído/erro)
    private val _mensagem = MutableStateFlow<String?>(null)
    val mensagem: StateFlow<String?> = _mensagem.asStateFlow()

    fun carregar() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _carregando.value = true
            try {
                _produtos.value = withContext(Dispatchers.IO) {
                    val lojaId = LojaRepository.resolverLojaId(uid)
                        ?: return@withContext emptyList()
                    db.collection("Produtos")
                        .whereEqualTo("produtorId", lojaId)
                        .get().await()
                        .documents.mapNotNull { snap ->
                            snap.toObject(Produto::class.java)?.copy(id = snap.id)
                        }
                }
            } catch (e: Exception) {
                _mensagem.value = e.message ?: "Erro ao carregar produtos."
            } finally {
                _carregando.value = false
            }
        }
    }

    fun salvar(
        context: Context,
        produtoId: String,
        nome: String,
        descricao: String,
        precoStr: String,
        quantidadeStr: String,
        novaImagemUri: Uri?
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val preco = precoStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                val quantidade = quantidadeStr.toIntOrNull()?.coerceAtLeast(0) ?: 0
                val updates = mutableMapOf<String, Any>(
                    "nome" to nome,
                    "descricao" to descricao,
                    "preco" to preco,
                    "quantidade" to quantidade
                )
                var novaUrl: String? = null
                if (novaImagemUri != null) {
                    novaUrl = withContext(Dispatchers.IO) {
                        ImagemRepository.upload(context, uid, "produto", novaImagemUri)
                    }
                    updates["imagens"] = listOf(novaUrl)
                }
                withContext(Dispatchers.IO) {
                    db.collection("Produtos").document(produtoId).update(updates).await()
                }
                _produtos.value = _produtos.value.map { p ->
                    if (p.id == produtoId) {
                        p.copy(
                            nome = nome,
                            descricao = descricao,
                            preco = preco,
                            quantidade = quantidade,
                            imagens = if (novaUrl != null) listOf(novaUrl) else p.imagens
                        )
                    } else p
                }
                _mensagem.value = "Produto salvo."
            } catch (e: Exception) {
                _mensagem.value = e.message ?: "Erro ao salvar produto."
            }
        }
    }

    fun excluir(produtoId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val produtoRef = db.collection("Produtos").document(produtoId)
                    val avaliacoes = produtoRef.collection("Avaliacoes").get().await()

                    avaliacoes.documents.chunked(450).forEach { grupo ->
                        val lote = db.batch()
                        grupo.forEach { lote.delete(it.reference) }
                        lote.commit().await()
                    }
                    produtoRef.delete().await()
                }
                _produtos.value = _produtos.value.filter { it.id != produtoId }
                _mensagem.value = "Produto excluído."
            } catch (e: Exception) {
                _mensagem.value = e.message ?: "Erro ao excluir produto."
            }
        }
    }

    fun limparMensagem() {
        _mensagem.value = null
    }
}
