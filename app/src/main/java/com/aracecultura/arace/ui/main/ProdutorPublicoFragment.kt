package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.ui.components.perfil.produtor.PerfilProdutor
import com.aracecultura.arace.ui.theme.AraceTheme

/**
 * Perfil de uma loja na visão do cliente (somente leitura): sem o botão de
 * editar produtos nem a troca de modo. Aberto ao tocar no produtor responsável
 * na TelaDoProduto. Recebe o lojaId (== produtorId) por argumento.
 */
class ProdutorPublicoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lojaId = arguments?.getString("lojaId").orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                AraceTheme {
                    PerfilProdutor(
                        lojaId = lojaId,
                        somenteLeitura = true,
                        onBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}
