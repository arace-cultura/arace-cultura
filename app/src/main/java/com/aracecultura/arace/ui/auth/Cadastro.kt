package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentCadastroBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.firestore

class Cadastro : Fragment() {
    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db = Firebase.firestore

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
        initListeners()
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
        val nome = this.binding.nomeInput.text.toString().trim()

        if(!validarCredenciais(nome, email, senha)){
            Toast
                .makeText(
                    requireContext(),
                    "Preencha todos os dados corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
            return
        }

        this.auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
            if (cadastro.isSuccessful) {
                val userUID = cadastro.result.user?.uid

                if(userUID != null) {
                    val novoUsuario = hashMapOf(
                        "nome" to nome,
                        "isProdutor" to false
                    )

                    this.db.collection("Usuarios")
                        .document(userUID)
                        .set(novoUsuario)
                        .addOnSuccessListener {
                            findNavController().navigate(R.id.action_global_to_main)
                        }
                        .addOnFailureListener {
                            Toast
                                .makeText(
                                    requireContext(),
                                    "Houve um erro ao salvar os dados.",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                }
            }
        }.addOnFailureListener { exception ->
            val mensagemErro = when(exception) {
                is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
                is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
                is FirebaseAuthUserCollisionException -> "Conta já cadastrada. Faça login"
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

    private fun validarCredenciais(nome: String, email: String, senha: String): Boolean {
        val nomeValido = nome.isNotEmpty()
        val emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val senhaValida = senha.length >= 6

        return nomeValido && emailValido && senhaValida
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}