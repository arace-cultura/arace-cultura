package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText

class CadastroProdutorTela2 : Fragment(R.layout.fragment_cadastro_produtor_tela2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val produtorRecebido = arguments?.getParcelable<Produtor>("produtorData") ?: Produtor()

        val etRazao    = view.findViewById<TextInputEditText>(R.id.etRazaoSocial)
        val etCnpj     = view.findViewById<TextInputEditText>(R.id.etCnpj)
        val etTelefone = view.findViewById<TextInputEditText>(R.id.etTelefone)

        view.findViewById<Button>(R.id.btnProximo2).setOnClickListener {
            val produtorAtualizado = produtorRecebido.copy(
                razaoSocial = etRazao.text.toString(),
                cnpj        = etCnpj.text.toString(),
                telefone    = etTelefone.text.toString()
            )
            findNavController().navigate(
                R.id.action_tela2_to_tela3,
                Bundle().apply { putParcelable("produtorData", produtorAtualizado) }
            )
        }

        view.findViewById<TextView>(R.id.btnVoltar2).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}