package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentCadastroBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class Cadastro : Fragment() {
    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = requireActivity().window
        val view = binding.root
        val controller = WindowInsetsControllerCompat(window, view)

        // muda as cores das barras para se adequar a cor da tela
        requireActivity().window.decorView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.black))

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        initListeners()
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
        binding.cadastroBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cadastroBtn.setOnClickListener {
           this.cadastrar()
        }
    }

    private fun cadastrar() {
        val senha = this.binding.cadastroSenha.text.toString().trim()
        val email = this.binding.cadastroInput.text.toString().trim()

        if(!validarCredenciais(email, senha)){
            Toast
                .makeText(
                    requireContext(),
                    "Há erro nos dados.",
                    Toast.LENGTH_SHORT
                ).show()
            return
        }

        this.auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->

            if (cadastro.isSuccessful) {
                findNavController().navigate(R.id.action_global_to_main)
            }
        }.addOnFailureListener { exception ->
            val mensagemErro = when(exception) {
                // senha de menos 6 caracteres
                is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
                is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
                is FirebaseAuthUserCollisionException -> "Conta já cadastrada. Faça login"
                // para checar conexao com a internet, precisamos usar internet com o app, veja manifest
                is FirebaseNetworkException -> "Verifique sua conexão com a internet e tente novamente!"
                else -> "Erro ao cadastrar usuário"
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
        _binding = null
    }
}