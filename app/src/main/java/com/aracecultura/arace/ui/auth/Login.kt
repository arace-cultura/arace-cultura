package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = this._binding!!


    private val auth by lazy { FirebaseAuth.getInstance() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initListeners()
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        val view = binding.root
        val controller = WindowInsetsControllerCompat(window, view)

        // muda as cores das barras para se adequar a cor da tela
        requireActivity().window.decorView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.placeholder1))

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
    }

    private fun initListeners() {
        binding.loginBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.loginBtn.setOnClickListener {
            this.login()
        }
    }

    private fun login() {
        val email = binding.loginInput.text.toString().trim()
        val senha = binding.loginSenha.text.toString().trim()

        /**
         * Por enquanto, não avisa quanto a aspectos pontuais,
         * apenas se há erro.
         **/

        if (!validarCredenciais(email, senha)) {
            Toast.makeText(
                requireContext(),
                "Há erro nos dados.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        this.auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    findNavController().navigate(R.id.action_global_to_main)
                }
            }.addOnFailureListener { exception ->
                val mensagemErro = when(exception) {
                    is FirebaseAuthInvalidUserException-> "Este e-mail não está cadastrado ou foi desativado."
                    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
                    is FirebaseNetworkException -> "Sem conexão com a rede. Verifique seu Wi-Fi ou dados móveis."
                    is FirebaseTooManyRequestsException -> "Muitas tentativas inválidas. Tente novamente mais tarde."
                    is FirebaseAuthException -> "Erro de autenticação: ${exception.errorCode}"
                    else -> "Ocorreu um erro inesperado: ${exception.localizedMessage}"
                }

                Toast
                    .makeText(
                        requireContext(),
                        mensagemErro,
                        Toast.LENGTH_SHORT
                    ).show()
            }
    }

    private fun validarCredenciais(email: String, senha: String): Boolean {
        // Patterns para e-mail e senha com mínimo de 8 caracteres para segurança.
        val emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val senhaValida = senha.length >= 6

        return emailValido && senhaValida
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.decorView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.background))
        this._binding = null
    }
}