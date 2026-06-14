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
import com.aracecultura.arace.ui.components.explorar.TelaConfiguracoes
import com.aracecultura.arace.ui.components.perfil.cliente.EditarPerfilUsuario
import com.aracecultura.arace.ui.components.perfil.cliente.MeusDados
import com.aracecultura.arace.ui.components.perfil.cliente.PerfilCliente
import com.aracecultura.arace.ui.theme.AraceTheme
import com.google.firebase.auth.FirebaseAuth

private enum class TelaPerfil {
    PERFIL,
    EDITAR,
    CONFIGURACOES,
    MEUS_DADOS
}

class PerfilClienteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AraceTheme {
                //Controla o estado de qual tela exibir
                var telaAtual by remember { mutableStateOf(TelaPerfil.PERFIL) }

                //Pega o UID real do usuário logado no Firebase
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                when (telaAtual) {
                    TelaPerfil.EDITAR -> {
                    // Mostra a tela de Edição
                    EditarPerfilUsuario(
                        uid = uid,
                        onVoltarClick = { telaAtual = TelaPerfil.PERFIL }
                    )
                    }
                    TelaPerfil.CONFIGURACOES -> {
                        TelaConfiguracoes(
                            onBackClick = { telaAtual = TelaPerfil.PERFIL },
                            onMeusDadosClick = { telaAtual = TelaPerfil.MEUS_DADOS }
                        )
                    }
                    TelaPerfil.MEUS_DADOS -> {
                        MeusDados(
                            uid = uid,
                            onVoltarClick = { telaAtual = TelaPerfil.CONFIGURACOES },
                            onEditarClick = { telaAtual = TelaPerfil.EDITAR }
                        )
                    }
                    TelaPerfil.PERFIL -> {
                    // Mostra a tela de Visualização Padrão
                    PerfilCliente(
                        uid = uid,
                        onEditClick = { telaAtual = TelaPerfil.EDITAR },
                        onSettingsClick = { telaAtual = TelaPerfil.CONFIGURACOES },
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
                } // AraceTheme
            }
        }
    }
}
