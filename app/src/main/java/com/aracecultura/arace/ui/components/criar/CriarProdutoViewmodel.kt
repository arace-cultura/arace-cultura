package com.aracecultura.arace.ui.components.criar

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.R
import com.aracecultura.arace.supabase
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.data.model.Produto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

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
        imageUri: Uri,
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
            _uiState.value = ProdutoUiState.Error("Usuário não autenticado.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProdutoUiState.Loading

            try {
                // O produto pertence à LOJA vinculada à conta (compartilhável
                // entre contas), não ao usuário individual
                val lojaId = LojaRepository.resolverLojaId(userUid)
                    ?: throw Exception("Conta sem loja vinculada.")

                // 1. Converter a URI local em ByteArray
                val imageBytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
                    ?: throw Exception("Não foi possível processar a imagem.")

                // 2. Upload para o Supabase
                val bucket = supabase.storage.from("imagens")
                val fileName = "${UUID.randomUUID()}.jpg"
                val caminhoSeguro = "$userUid/$fileName"

                bucket.upload(path = caminhoSeguro, data = imageBytes) {
                    upsert = true
                }
                val imageUrl = bucket.publicUrl(caminhoSeguro)

                // 3. Formatar o preço
                val precoFormatado = precoStr.replace(",", ".").toDoubleOrNull() ?: 0.0

                // 4. Instanciar a Data Class (adaptando os campos únicos para listas)
                val novoProduto = Produto(
                    // id = "" -> Não passamos o ID, o @DocumentId diz pro Firestore gerar um automaticamente no .add()
                    nome = nomeNormalizado,
                    categorias = listOf(categoria), // Envolvido em lista
                    descricao = descricao,
                    preco = precoFormatado,
                    imagens = listOf(imageUrl), // Envolvido em lista
                    produtorId = lojaId,
                    avaliacao = 0.0,
                    somaAvaliacoes = 0.0,
                    quantidadeAvaliacoes = 0
                )

                // 5. Salvar o objeto diretamente no Firestore
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
