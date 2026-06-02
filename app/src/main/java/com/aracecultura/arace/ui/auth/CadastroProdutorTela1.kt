package com.aracecultura.arace.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.databinding.FragmentCadastroProdutorTela1Binding
import com.google.android.material.textfield.TextInputEditText

// ... imports ...
class CadastroProdutorTela1 : Fragment(R.layout.fragment_cadastro_produtor_tela1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnProximo = view.findViewById<Button>(R.id.btnProximo1)
        val etNome = view.findViewById<TextInputEditText>(R.id.etNomeCompleto)
        val etLoja = view.findViewById<TextInputEditText>(R.id.etNomeLoja)
        val rgTipo = view.findViewById<RadioGroup>(R.id.rgTipoPessoa)

        btnProximo.setOnClickListener {
            val tipoSelecionado = if (rgTipo.checkedRadioButtonId == R.id.rbPJ) "PJ" else "PF"

            val produtor = Produtor(
                nomeCompleto = etNome.text.toString(),
                nomeLoja = etLoja.text.toString(),
                tipoPessoa = tipoSelecionado
            )

            val bundle = Bundle().apply { putParcelable("produtorData", produtor) }
            findNavController().navigate(R.id.action_tela1_to_tela2, bundle)
        }
    }
}