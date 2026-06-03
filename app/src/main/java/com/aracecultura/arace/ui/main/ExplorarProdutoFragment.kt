package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.explorar.ExplorarProduto
import com.google.firebase.auth.FirebaseAuth

class ExplorarProdutoFragment : Fragment() {

    private lateinit var composeView: ComposeView
    // Instanciamos o viewmodel aqui para poder passar a ação do carrinho

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

        // Pega o ID do usuário atual
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        composeView.setContent {
            ExplorarProduto(
                uid = uid,
                onNavigateToProduto = { produtoId ->
                    // 1. Navega para a tela do produto passando o ID no Bundle
                    val bundle = Bundle().apply {
                        putString("produtoId", produtoId)
                    }
                    findNavController().navigate(R.id.action_explorar_to_produto, bundle)
                }
            )
        }
    }
}
