package com.aracecultura.arace.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface ResultadoCadastro {
    data object Idle : ResultadoCadastro
    data object Salvando : ResultadoCadastro
    data object Sucesso : ResultadoCadastro
    data class Erro(val mensagem: String) : ResultadoCadastro
}

class CadastroProdutorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _resultado = MutableStateFlow<ResultadoCadastro>(ResultadoCadastro.Idle)
    val resultado: StateFlow<ResultadoCadastro> = _resultado.asStateFlow()

    fun salvarProdutor(produtor: Produtor) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _resultado.value = ResultadoCadastro.Erro("Usuário não autenticado")
            return
        }

        viewModelScope.launch {
            _resultado.value = ResultadoCadastro.Salvando
            try {
                db.collection("Produtores")
                    .document(uid)
                    .set(produtor.copy(uid = uid))
                    .await()
                db.collection("Usuarios")
                    .document(uid)
                    .set(mapOf("isProdutor" to true), SetOptions.merge())
                    .await()
                _resultado.value = ResultadoCadastro.Sucesso
            } catch (e: Exception) {
                _resultado.value = ResultadoCadastro.Erro(e.message ?: "Erro ao salvar produtor")
            }
        }
    }

    // Chame esta função em qualquer ViewModel que precise checar se o usuário é produtor.
    // Substitui o sharedPref.getBoolean("STATUS_PRODUTOR_$uid", false) anterior.
    suspend fun isProdutor(uid: String): Boolean {
        return try {
            db.collection("Produtores")
                .document(uid)
                .get()
                .await()
                .exists()
        } catch (e: Exception) {
            false
        }
    }
}
