package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.aracecultura.arace.ui.components.perfil.produtor.PerfilProdutor
import com.aracecultura.arace.ui.components.produto.TelaEditarProdutos
import com.aracecultura.arace.ui.theme.AraceTheme
import com.google.firebase.auth.FirebaseAuth

class PerfilProdutorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AraceTheme {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                    // Alterna entre o perfil e a edição de produtos sem mexer
                    // no nav graph (mesmo padrão do PerfilClienteFragment)
                    var editandoProdutos by remember { mutableStateOf(false) }

                    if (editandoProdutos) {
                        TelaEditarProdutos(
                            uid = uid,
                            onBack = { editandoProdutos = false }
                        )
                    } else {
                        PerfilProdutor(
                            uid = uid,
                            onModoChanged = { isProdutor ->
                                val bundle = Bundle().apply { putBoolean("isProdutor", isProdutor) }
                                requireActivity().supportFragmentManager
                                    .setFragmentResult("mudanca_modo_request", bundle)
                            },
                            onEditarProdutos = { editandoProdutos = true }
                        )
                    }
                }
            }
        }
    }
}
