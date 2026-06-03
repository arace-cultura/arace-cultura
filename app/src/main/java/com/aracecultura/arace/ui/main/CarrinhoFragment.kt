package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.aracecultura.arace.ui.components.carrinho.NewCarrinho
import com.google.firebase.auth.FirebaseAuth // Importe o FirebaseAuth

class CarrinhoFragment : Fragment() {
    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtém o UID do usuário atualmente logado.
        // Caso não haja usuário logado (o que não deve acontecer nesta tela), passa uma string vazia.
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        composeView.setContent {
            NewCarrinho(uid = uid)
        }
    }
}