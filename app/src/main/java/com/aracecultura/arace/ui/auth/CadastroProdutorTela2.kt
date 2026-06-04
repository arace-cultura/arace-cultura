package com.aracecultura.arace.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText

class CadastroProdutorTela2 : Fragment(R.layout.fragment_cadastro_produtor_tela2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera o objeto da tela anterior
        val produtorRecebido = arguments?.getParcelable<Produtor>("produtorData")
            ?: Produtor()

        val etRazao = view.findViewById<TextInputEditText>(R.id.etRazaoSocial)
        val etCnpj = view.findViewById<TextInputEditText>(R.id.etCnpj)
        val etTelefone = view.findViewById<TextInputEditText>(R.id.etTelefone)

        view.findViewById<Button>(R.id.btnProximo2).setOnClickListener {
            val produtorAtualizado = produtorRecebido.copy(
                razaoSocial = etRazao.text.toString(),
                cnpj = etCnpj.text.toString(),
                telefone = etTelefone.text.toString()
            )

            val bundle = Bundle().apply { putParcelable("produtorData", produtorAtualizado) }
            findNavController().navigate(R.id.action_tela2_to_tela3, bundle)
        }

        view.findViewById<TextView>(R.id.btnVoltar2).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}