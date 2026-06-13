package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.aracecultura.arace.R
import com.google.android.material.textfield.TextInputEditText

class CadastroProdutorTela2 : Fragment(R.layout.fragment_cadastro_produtor_tela2) {

    private val viewModel: CadastroProdutorViewModel by navGraphViewModels(R.id.fluxo_cadastro_produtor)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etRazao    = view.findViewById<TextInputEditText>(R.id.etRazaoSocial)
        val etCnpj     = view.findViewById<TextInputEditText>(R.id.etCnpj)
        val etTelefone = view.findViewById<TextInputEditText>(R.id.etTelefone)

        val draft = viewModel.draft.value
        etRazao.setText(draft.razaoSocial)
        etCnpj.setText(draft.cnpj)
        etTelefone.setText(draft.telefone)

        fun salvarCampos() {
            viewModel.atualizarDraft {
                it.copy(
                    razaoSocial = etRazao.text.toString(),
                    cnpj        = etCnpj.text.toString(),
                    telefone    = etTelefone.text.toString()
                )
            }
        }

        view.findViewById<Button>(R.id.btnProximo2).setOnClickListener {
            salvarCampos()
            findNavController().navigate(R.id.action_tela2_to_tela3)
        }

        view.findViewById<TextView>(R.id.btnVoltar2).setOnClickListener {
            salvarCampos()
            findNavController().popBackStack()
        }
    }
}
