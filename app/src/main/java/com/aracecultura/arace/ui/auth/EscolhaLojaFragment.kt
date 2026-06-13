package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R

/**
 * Porta de entrada do modo produtor para contas sem loja vinculada:
 * cadastrar uma loja nova ou entrar em uma existente com nome + senha.
 */
class EscolhaLojaFragment : Fragment(R.layout.fragment_escolha_loja) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnCadastrarLoja).setOnClickListener {
            findNavController().navigate(R.id.action_escolha_to_tela1)
        }

        view.findViewById<Button>(R.id.btnEntrarLoja).setOnClickListener {
            findNavController().navigate(R.id.action_escolha_to_entrar)
        }

        view.findViewById<TextView>(R.id.btnVoltarEscolha).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
