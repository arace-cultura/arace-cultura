package com.aracecultura.arace.ui.components.produto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.COLECAO_CONTADOR_CARRINHOS
import com.aracecultura.arace.data.estaEmDestaque
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class ErroAvaliacao {
    USUARIO_NAO_AUTENTICADO,
    SALVAR
}

class TelaDoProdutoViewmodel : ViewModel() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var produtoListener: ListenerRegistration? = null
    private var produtorListener: ListenerRegistration? = null
    private var avaliacaoListener: ListenerRegistration? = null
    private var destaqueListener: ListenerRegistration? = null
    private var produtoIdAtual: String? = null
    private var produtorIdAtual: String? = null

    private val _produto = MutableStateFlow<Produto?>(null)
    val produto: StateFlow<Produto?> = _produto

    private val _produtor = MutableStateFlow<Produtor?>(null)
    val produtor: StateFlow<Produtor?> = _produtor

    private val _avaliacaoUsuario = MutableStateFlow<Int?>(null)
    val avaliacaoUsuario: StateFlow<Int?> = _avaliacaoUsuario

    private val _salvandoAvaliacao = MutableStateFlow(false)
    val salvandoAvaliacao: StateFlow<Boolean> = _salvandoAvaliacao

    private val _erroAvaliacao = MutableStateFlow<ErroAvaliacao?>(null)
    val erroAvaliacao: StateFlow<ErroAvaliacao?> = _erroAvaliacao

    // Destaque vem da coleção externa CarrinhosContador, não do produto.
    private val _emDestaque = MutableStateFlow(false)
    val emDestaque: StateFlow<Boolean> = _emDestaque

    fun carregarProduto(produtoId: String) {
        if (produtoIdAtual == produtoId) return
        produtoIdAtual = produtoId
        produtoListener?.remove()

        val produtoRef = db.collection("Produtos").document(produtoId)
        produtoListener = produtoRef.addSnapshotListener { documento, erro ->
            if (erro != null || documento == null || !documento.exists()) {
                return@addSnapshotListener
            }

            val produtoCarregado = documento.toObject(Produto::class.java)
            _produto.value = produtoCarregado
            carregarProdutorSeNecessario(produtoCarregado?.produtorId)
        }

        destaqueListener?.remove()
        destaqueListener = db.collection(COLECAO_CONTADOR_CARRINHOS).document(produtoId)
            .addSnapshotListener { documento, erro ->
                if (erro != null) return@addSnapshotListener
                _emDestaque.value = documento?.estaEmDestaque() ?: false
            }

        carregarAvaliacaoUsuario(produtoId)
    }

    private fun carregarProdutorSeNecessario(produtorId: String?) {
        if (produtorId.isNullOrBlank() || produtorIdAtual == produtorId) return
        produtorIdAtual = produtorId
        produtorListener?.remove()
        // Tempo real: o erro é tratado no próprio callback (não derruba o app).
        produtorListener = db.collection("Produtores").document(produtorId)
            .addSnapshotListener { documento, erro ->
                if (erro != null || documento == null) return@addSnapshotListener
                _produtor.value = documento.toObject(Produtor::class.java)
            }
    }

    private fun carregarAvaliacaoUsuario(produtoId: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        avaliacaoListener?.remove()
        // Tempo real: a nota do usuário reflete na hora; erro tratado no callback.
        avaliacaoListener = db.collection("Produtos").document(produtoId)
            .collection("Avaliacoes").document(uid)
            .addSnapshotListener { documento, erro ->
                if (erro != null) return@addSnapshotListener
                _avaliacaoUsuario.value = documento?.getLong("nota")?.toInt()
            }
    }

    fun avaliarProduto(nota: Int, onSucesso: () -> Unit) {
        val produtoId = produtoIdAtual ?: return
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            _erroAvaliacao.value = ErroAvaliacao.USUARIO_NAO_AUTENTICADO
            return
        }
        if (nota !in 1..5 || _salvandoAvaliacao.value) return

        viewModelScope.launch {
            _salvandoAvaliacao.value = true
            _erroAvaliacao.value = null
            try {
                withContext(Dispatchers.IO) {
                    val produtoRef = db.collection("Produtos").document(produtoId)
                    val avaliacaoRef = produtoRef.collection("Avaliacoes").document(uid)

                    db.runTransaction { transaction ->
                        val produtoDoc = transaction.get(produtoRef)
                        val avaliacaoDoc = transaction.get(avaliacaoRef)
                        val agregado = calcularAgregadoAvaliacao(
                            somaAtual = produtoDoc.getDouble("somaAvaliacoes") ?: 0.0,
                            quantidadeAtual = (
                                produtoDoc.getLong("quantidadeAvaliacoes") ?: 0L
                                ).toInt(),
                            notaAnterior = avaliacaoDoc.getLong("nota")?.toInt(),
                            novaNota = nota
                        )

                        transaction.set(
                            avaliacaoRef,
                            mapOf(
                                "usuarioId" to uid,
                                "nota" to nota,
                                "atualizadoEm" to FieldValue.serverTimestamp()
                            ),
                            SetOptions.merge()
                        )
                        transaction.update(
                            produtoRef,
                            mapOf(
                                "somaAvaliacoes" to agregado.soma,
                                "quantidadeAvaliacoes" to agregado.quantidade,
                                "avaliacao" to agregado.media
                            )
                        )
                    }.await()
                }
                _avaliacaoUsuario.value = nota
                onSucesso()
            } catch (e: Exception) {
                _erroAvaliacao.value = ErroAvaliacao.SALVAR
            } finally {
                _salvandoAvaliacao.value = false
            }
        }
    }

    fun limparErroAvaliacao() {
        _erroAvaliacao.value = null
    }

    override fun onCleared() {
        produtoListener?.remove()
        produtorListener?.remove()
        avaliacaoListener?.remove()
        destaqueListener?.remove()
        super.onCleared()
    }
}
