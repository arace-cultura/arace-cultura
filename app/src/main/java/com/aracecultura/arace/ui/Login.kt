package com.aracecultura.arace.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.MainActivity
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val dbAuth by lazy { FirebaseAuth.getInstance() }

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
        initListeners()
    }

    private fun initListeners() {
        binding.loginBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.loginBtn.setOnClickListener {

            val email = binding.loginInput.text.toString().trim()
            val senha = binding.loginSenha.text.toString().trim()

            // se vazio
            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(binding.root, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                // autenticar usuario
                dbAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { autenticacao ->
                    if (autenticacao.isSuccessful) {
                        // manda usuario pra tela principal - funcao la em baixo
                        navegarTelaPrincipal()
                    }
                    // listener para quando der errado
                }.addOnFailureListener {
                    /**
                     * TODO: Tratamento de erros utilizando os metodos de exception,
                     * parecido com a tela de cadastro
                     *
                     */
                    val snackbar = Snackbar.make(binding.root, "Erro ao fazer Login do usuário", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }
    }

    private fun navegarTelaPrincipal() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        val snackbar = Snackbar.make(binding.root, "Login Efetuado", Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.GREEN)
        snackbar.show()
        requireActivity().finish()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}