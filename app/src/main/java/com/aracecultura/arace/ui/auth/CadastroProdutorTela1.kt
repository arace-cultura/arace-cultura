package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText

class CadastroProdutorTela1 : Fragment(R.layout.fragment_cadastro_produtor_tela1) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNome  = view.findViewById<TextInputEditText>(R.id.etNomeCompleto)
        val etLoja  = view.findViewById<TextInputEditText>(R.id.etNomeLoja)
        val rgTipo  = view.findViewById<RadioGroup>(R.id.rgTipoPessoa)

        view.findViewById<Button>(R.id.btnProximo1).setOnClickListener {
            val produtor = Produtor(
                nomeCompleto = etNome.text.toString(),
                nomeLoja     = etLoja.text.toString(),
                tipoPessoa   = if (rgTipo.checkedRadioButtonId == R.id.rbPJ) "PJ" else "PF"
            )
            findNavController().navigate(
                R.id.action_tela1_to_tela2,
                Bundle().apply { putParcelable("produtorData", produtor) }
            )

            view.findViewById<TextView>(R.id.btnVoltar).setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}