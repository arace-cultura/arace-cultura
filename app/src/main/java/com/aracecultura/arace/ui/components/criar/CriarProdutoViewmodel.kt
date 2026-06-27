package com.aracecultura.arace.ui.components.criar

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.supabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.storage.storage
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class ProdutoUiState {
    data object Idle : ProdutoUiState()
    data object Loading : ProdutoUiState()
    data class Success(val message: String) : ProdutoUiState()
    data class Error(val message: String) : ProdutoUiState()
}

class ProdutoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProdutoUiState>(ProdutoUiState.Idle)
    val uiState: StateFlow<ProdutoUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    fun salvarProduto(
        context: Context,
        imageUris: List<Uri>,
        nome: String,
        categoria: String,
        descricao: String,
        precoStr: String
    ) {
        val nomeNormalizado = normalizarNomeProduto(nome)
        if (nomeNormalizado.isEmpty()) {
            _uiState.value = ProdutoUiState.Error(
                context.getString(R.string.criar_nome_obrigatorio)
            )
            return
        }

        val userUid = Firebase.auth.currentUser?.uid
        if (userUid == null) {
            _uiState.value = ProdutoUiState.Error("Usuario nao autenticado.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProdutoUiState.Loading

            try {
                val lojaId = LojaRepository.resolverLojaId(userUid)
                    ?: throw Exception("Conta sem loja vinculada.")

                val bucket = supabase.storage.from("imagens")
                val imageUrls = imageUris.take(3).map { imageUri ->
                    val imageBytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
                        ?: throw Exception("Nao foi possivel processar a imagem.")
                    val caminhoSeguro = "$userUid/${UUID.randomUUID()}.jpg"

                    bucket.upload(path = caminhoSeguro, data = imageBytes) {
                        upsert = true
                    }

                    bucket.publicUrl(caminhoSeguro)
                }

                val precoFormatado = precoStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                val novoProduto = Produto(
                    nome = nomeNormalizado,
                    categorias = listOf(categoria),
                    descricao = descricao,
                    preco = precoFormatado,
                    imagens = imageUrls,
                    produtorId = lojaId,
                    avaliacao = 0.0,
                    somaAvaliacoes = 0.0,
                    quantidadeAvaliacoes = 0
                )

                db.collection("Produtos").add(novoProduto).await()

                _uiState.value = ProdutoUiState.Success("Produto criado com sucesso!")
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProdutoUiState.Error(e.message ?: "Erro ao salvar produto.")
            }
        }
    }

    fun resetState() {
        _uiState.value = ProdutoUiState.Idle
    }
}
