package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CadastroProdutorTela3 : Fragment(R.layout.fragment_cadastro_produtor_tela3) {

    private val viewModel: CadastroProdutorViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val produtorRecebido = arguments?.getParcelable<Produtor>("produtorData") ?: Produtor()

        val etCep      = view.findViewById<TextInputEditText>(R.id.etCep)
        val etEndereco = view.findViewById<TextInputEditText>(R.id.etEndereco)
        val etTipoArt  = view.findViewById<TextInputEditText>(R.id.etTipoArtesanato)
        val acCategoria = view.findViewById<AutoCompleteTextView>(R.id.acCategoria)

        acCategoria.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                arrayOf("Artesanato", "Têxteis", "Cosméticos", "Casa", "Cerâmica", "Acessórios")
            )
        )

        view.findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            viewModel.salvarProdutor(
                produtorRecebido.copy(
                    cep             = etCep.text.toString(),
                    endereco        = etEndereco.text.toString(),
                    tipoArtesanato  = etTipoArt.text.toString(),
                    categoriaProduto = acCategoria.text.toString()
                )
            )
        }

        view.findViewById<TextView>(R.id.btnVoltar3).setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultado.collect { resultado ->
                    when (resultado) {
                        is ResultadoCadastro.Sucesso -> {
                            parentFragmentManager.setFragmentResult(
                                "cadastro_produtor_request",
                                bundleOf("sucesso" to true)
                            )
                            findNavController().navigate(R.id.action_cadastro_concluido)
                        }
                        is ResultadoCadastro.Erro -> {
                            // TODO: Snackbar.make(view, resultado.mensagem, Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}