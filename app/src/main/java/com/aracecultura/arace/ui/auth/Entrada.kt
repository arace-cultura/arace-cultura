package com.aracecultura.arace.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aracecultura.arace.R
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.databinding.FragmentEntradaBinding
import com.google.firebase.auth.FirebaseAuth

class Entrada : Fragment() {

    private var _binding: FragmentEntradaBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.checkAuth()

        _binding = FragmentEntradaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.checkAuth()
        this.initListeners()
    }


    private fun initListeners() {
        binding.goLoginBtn.setOnClickListener {
            // ir para tela de login
            findNavController().navigate(R.id.action_entrada_to_login)
        }

        binding.goCadastroBtn.setOnClickListener {
            // ir para tela de cadastro
            findNavController().navigate(R.id.action_entrada_to_cadastro)
        }
    }

    private fun checkAuth() {
        if (this.auth.currentUser != null) {
            findNavController().navigate(R.id.action_global_to_main)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}