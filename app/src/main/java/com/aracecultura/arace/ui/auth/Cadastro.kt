package com.aracecultura.arace.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.MainActivity
import com.aracecultura.arace.databinding.FragmentCadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class Cadastro : Fragment() {
    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!

    private val dbAuth by lazy { FirebaseAuth.getInstance() }

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
            val senha = binding.cadastroSenha.text.toString().trim()
            val email = binding.cadastroInput.text.toString().trim()

            // logica que evita entradas incorretas
            if (email.isEmpty() || senha.isEmpty()){
                val snackbar = Snackbar.make(binding.root,"Preencha todos os Campos", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()

            } else {
                if (!email.contains("@")){ // se o email do cara tem @
                    val snackbar = Snackbar.make(binding.root,"Informe um e-mail válido", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()

                } else {
                    // dados enviados se nao há erros
                    dbAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
                        // se cadastro bem-sucedido, mostra essa toast
                        if (cadastro.isSuccessful) {
                            Toast.makeText(requireContext(), "Usuário logado", Toast.LENGTH_LONG).show()                            /** SMILI
                             * limpa os campos de texto
                             * agora, precisamos encaminhar o usuario para a tela de login
                             * tambem precisa implementar a logica pra deixar ele entrar direto
                             * quando ja logado no app
                             */
                            binding.cadastroInput.setText("")
                            binding.cadastroSenha.setText("")
                            binding.cadastroConfirmarSenha.setText("")
                            // manda o cara pra tela principal
                            navegarTelaPrincipal()
                        }
                        // para futuro tratamento de erros (proxima aula do curso xd)
                    }.addOnFailureListener { exception ->

                        // tratamento de erro
                        val mensagemErro = when(exception) {
                            // senha de menos 6 caracteres
                            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
                            is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
                            is FirebaseAuthUserCollisionException -> "Conta já cadastrada. Faça login"
                            // para checar conexao com a internet, precisamos usar internet com o app, veja manifest
                            is FirebaseNetworkException -> "Verifique sua conexão com a internet e tente novamente!"
                            else -> "Erro ao cadastrar usuário"
                        }
                        // snackbar que mostra as exceptions do firebase
                        val snackbar = Snackbar.make(binding.root,mensagemErro, Snackbar.LENGTH_LONG)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }
            }
        }
    }

    private fun navegarTelaPrincipal() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        val snackbar = Snackbar.make(binding.root, "Cadastro Efetuado!", Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.GREEN)
        snackbar.show()
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}