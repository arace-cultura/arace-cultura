package com.aracecultura.arace.ui.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// Gerado utilizando a IA Gemini.

// Representa o que está acontecendo na autenticação
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val theMessage: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    // O encapsulamento: Mutable privado para o ViewModel alterar, LiveData público para o Fragment ler
    private val _loginState = MutableLiveData<AuthState>(AuthState.Idle)
    val loginState: LiveData<AuthState> = _loginState

    fun realizarLogin(email: String, senha: String) {
        if (!validarCredenciais(email, senha)) {
            _loginState.value = AuthState.Error("Há erro nos dados.")
            return
        }

        // Altera o estado para "Carregando" (útil para mostrar um ProgressBar)
        _loginState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = AuthState.Success
                }
            }
            .addOnFailureListener {
                _loginState.value = AuthState.Error("Falha no login, tente novamente.")
            }
    }

    private fun validarCredenciais(email: String, senha: String): Boolean {
        val emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val senhaValida = senha.length >= 6
        return emailValido && senhaValida
    }
}