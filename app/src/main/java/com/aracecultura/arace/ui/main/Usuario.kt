package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.databinding.FragmentUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.aracecultura.arace.R

class Usuario : Fragment() {

    private lateinit var binding: FragmentUsuarioBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentUsuarioBinding.inflate(
            inflater,
            container,
            false
        )
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.initListeners()
    }

    private fun initListeners() {
        this.binding.sairButton.setOnClickListener {
            this.auth.signOut()
            findNavController().navigate(R.id.action_main_to_auth)
        }
    }
}