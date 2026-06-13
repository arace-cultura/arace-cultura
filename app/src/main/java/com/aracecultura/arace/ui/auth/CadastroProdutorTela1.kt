package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.aracecultura.arace.R
import com.google.android.material.textfield.TextInputEditText

class CadastroProdutorTela1 : Fragment(R.layout.fragment_cadastro_produtor_tela1) {

    // ViewModel escopado ao nav graph do fluxo: o rascunho sobrevive à
    // navegação entre as telas (ida e volta).
    private val viewModel: CadastroProdutorViewModel by navGraphViewModels(R.id.fluxo_cadastro_produtor)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNome  = view.findViewById<TextInputEditText>(R.id.etNomeCompleto)
        val etLoja  = view.findViewById<TextInputEditText>(R.id.etNomeLoja)
        val rgTipo  = view.findViewById<RadioGroup>(R.id.rgTipoPessoa)

        // Pré-preenche a partir do rascunho
        val draft = viewModel.draft.value
        etNome.setText(draft.nomeCompleto)
        etLoja.setText(draft.nomeLoja)
        if (draft.tipoPessoa == "PJ") rgTipo.check(R.id.rbPJ) else rgTipo.check(R.id.rbPF)

        fun salvarCampos() {
            viewModel.atualizarDraft {
                it.copy(
                    nomeCompleto = etNome.text.toString(),
                    nomeLoja     = etLoja.text.toString(),
                    tipoPessoa   = if (rgTipo.checkedRadioButtonId == R.id.rbPJ) "PJ" else "PF"
                )
            }
        }

        view.findViewById<Button>(R.id.btnProximo1).setOnClickListener {
            salvarCampos()
            findNavController().navigate(R.id.action_tela1_to_tela2)
        }

        view.findViewById<TextView>(R.id.btnVoltar).setOnClickListener {
            salvarCampos()
            findNavController().popBackStack()
        }
    }
}
