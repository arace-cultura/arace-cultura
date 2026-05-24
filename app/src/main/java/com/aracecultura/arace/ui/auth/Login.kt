package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Importante para o 'by viewModels()'
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentLoginBinding

// Gerado utilizando a IA Gemini.
class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = this._binding!!

    // O Firebase Auth saiu daqui! Agora chamamos o ViewModel:
    private val viewModel: AuthViewModel by viewModels()

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
        this.observeViewModel() // Começa a observar o "cérebro"
    }

    private fun initListeners() {
        binding.loginBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.loginInput.text.toString().trim()
            val senha = binding.loginSenha.text.toString().trim()

            // Apenas delega a função para o ViewModel
            viewModel.realizarLogin(email, senha)
        }
    }

    private fun observeViewModel() {
        // O Fragment observa as mudanças de estado do LiveData
        viewModel.loginState.observe(viewLifecycleOwner) { estado ->
            when (estado) {
                is AuthState.Loading -> {
                    // Aqui você poderia ativar um loading/spinner se quiser
                    binding.loginBtn.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.loginBtn.isEnabled = true
                    findNavController().navigate(R.id.action_auth_to_main)
                }
                is AuthState.Error -> {
                    binding.loginBtn.isEnabled = true
                    Toast.makeText(requireContext(), estado.theMessage, Toast.LENGTH_SHORT).show()
                }
                is AuthState.Idle -> {
                    binding.loginBtn.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}