package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth


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
                    findNavController().navigate(R.id.action_auth_to_main)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Falha no login, tente novamente.",
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
        this._binding = null
    }
}