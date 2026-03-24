package com.aracecultura.arace.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aracecultura.arace.LoginActivity
import com.aracecultura.arace.R
import com.google.firebase.auth.FirebaseAuth

class Favoritos : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Desloga do Firebase
        FirebaseAuth.getInstance().signOut()

        // 2. Redireciona para a tela de entrada (LoginActivity ou similar)
        // Usamos requireActivity() para pegar o contexto da MainActivity
        val intent = Intent(requireActivity(), LoginActivity::class.java)

        // Limpa a pilha de atividades para o usuário não conseguir voltar
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        requireActivity().finish()

        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }
}