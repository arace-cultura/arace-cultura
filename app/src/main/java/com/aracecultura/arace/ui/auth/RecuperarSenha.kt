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
import com.aracecultura.arace.databinding.FragmentRecuperarSenhaBinding
import com.google.firebase.auth.FirebaseAuth

class RecuperarSenha : Fragment() {
    private var _binding: FragmentRecuperarSenhaBinding? = null
    private val binding get() = this._binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperarSenhaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initListeners()
    }

    private fun initListeners() {
        binding.recuperarBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.recuperarBtn.setOnClickListener {
            this.recuperarSenha()
        }
    }

    private fun recuperarSenha() {
        val email = binding.recuperarEmail.text.toString().trim()

        if (!emailValido(email)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.erro_email_invalido),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        this.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { tarefa ->
                if (tarefa.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.recuperar_email_enviado),
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().popBackStack()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_recuperar_senha),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun emailValido(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}
