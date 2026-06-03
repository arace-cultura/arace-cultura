package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.perfil.cliente.PerfilCliente
import com.google.firebase.auth.FirebaseAuth

class PerfilClienteFragment : Fragment() {
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

        // Dentro de PerfilClienteFragment.kt (no onViewCreated)
        composeView.setContent {
            PerfilCliente(
                uid = uid,
                onEditClick = { /* sua navegação de edição */ },
                onLogoutClick = {
                    // 1. Desloga do Firebase
                    FirebaseAuth.getInstance().signOut()

                    // 2. Avisa a NavegacaoPrincipal para mudar de tela via FragmentResult
                    requireActivity().supportFragmentManager.setFragmentResult("logout_request", Bundle())
                },
                onModoChanged = { isProdutor ->
                    val bundle = Bundle().apply { putBoolean("isProdutor", isProdutor) }
                    requireActivity().supportFragmentManager.setFragmentResult("mudanca_modo_request", bundle)
                }
            )
        }
    }
}