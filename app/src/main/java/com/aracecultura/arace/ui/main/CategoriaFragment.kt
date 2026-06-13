package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.explorar.TelaCategoria
import com.aracecultura.arace.ui.theme.AraceTheme
import com.google.firebase.auth.FirebaseAuth

class CategoriaFragment : Fragment() {

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

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val categoria = arguments?.getString("categoria").orEmpty()

        composeView.setContent {
            AraceTheme {
                TelaCategoria(
                    categoria = categoria,
                    uid = uid,
                    onBack = { findNavController().popBackStack() },
                    onNavigateToProduto = { produtoId ->
                        val bundle = Bundle().apply { putString("produtoId", produtoId) }
                        findNavController().navigate(R.id.action_categoria_to_produto, bundle)
                    }
                )
            }
        }
    }
}
