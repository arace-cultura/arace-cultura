package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import com.aracecultura.arace.ui.components.perfil.cliente.EditarPerfilUsuario
import com.aracecultura.arace.ui.components.perfil.cliente.PerfilCliente
import com.google.firebase.auth.FirebaseAuth

class PerfilClienteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                //Controla o estado de qual tela exibir
                var editando by remember { mutableStateOf(false) }

                //Pega o UID real do usuário logado no Firebase
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (editando) {
                    // Mostra a tela de Edição
                    EditarPerfilUsuario(
                        uid = uid,
                        onVoltarClick = { editando = false } // Volta para a tela de visualização
                    )
                } else {
                    // Mostra a tela de Visualização Padrão
                    PerfilCliente(
                        uid = uid,
                        onEditClick = { editando = true }, // Altera o estado para abrir a edição
                        onLogoutClick = {
                            // Desloga do Firebase
                            FirebaseAuth.getInstance().signOut()

                            // Avisa a NavegacaoPrincipal para mudar de tela via FragmentResult
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
    }
}