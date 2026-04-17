package com.aracecultura.arace.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.aracecultura.arace.R


class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // private val dbAuth by lazy { FirebaseAuth.getInstance() }

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
            // Utilizado para voltar para a última fragment do backstack
            findNavController().popBackStack()
        }

        binding.loginBtn.setOnClickListener {
            this.tentarRealizarLogin()
        }
    }

    private fun tentarRealizarLogin() {
        // Captura as credenciais dos inputs ao clicar no botão de login
        val email = binding.loginInput.text.toString().trim()
        val senha = binding.loginSenha.text.toString().trim()

        if (validarCredenciais(email, senha)) {
            // autenticar usuario
            /*dbAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { autenticacao ->
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
                    val snackbar = Snackbar.make(
                        binding.root,
                        "Erro ao fazer Login do usuário",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }*/
            /**
             * Assumiremos que a pessoa realizou o login corretamente
             * com o Firebase por enquanto, pois só estou tentando organizar
             * o código da maneira como aprendi durante as aulas.*/
            findNavController().navigate(R.id.action_global_navegacaoPrincipalFragment)
        } else {
            // Caso as credenciais não sejam válidas.
            val snackbar =
                Snackbar.make(binding.root, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
        }
    }

    private fun validarCredenciais(email: String, senha: String): Boolean = !email.isEmpty() && !senha.isEmpty()

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}