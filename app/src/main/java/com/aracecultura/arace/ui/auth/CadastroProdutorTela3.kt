package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.CategoriasProduto
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class CadastroProdutorTela3 : Fragment(R.layout.fragment_cadastro_produtor_tela3) {

    private val viewModel: CadastroProdutorViewModel by navGraphViewModels(R.id.fluxo_cadastro_produtor)

    private val bannerPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.bannerUri = uri
        atualizarTextoSelecao()
    }

    private val fotoLojaPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.fotoLojaUri = uri
        atualizarTextoSelecao()
    }

    private val fotosHistoriaPicker = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        viewModel.fotosHistoriaUris = uris
        atualizarTextoSelecao()
    }

    private var tvBannerSelecionado: TextView? = null
    private var tvFotoLojaSelecionada: TextView? = null
    private var tvFotosHistoriaSelecionadas: TextView? = null
    private var llPreviewHistoria: LinearLayout? = null
    private var ivHistoria1: ImageView? = null
    private var ivHistoria2: ImageView? = null
    private var ivHistoria3: ImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCep      = view.findViewById<TextInputEditText>(R.id.etCep)
        val etEndereco = view.findViewById<TextInputEditText>(R.id.etEndereco)
        val etTipoArt  = view.findViewById<TextInputEditText>(R.id.etTipoArtesanato)
        val acCategoria = view.findViewById<AutoCompleteTextView>(R.id.acCategoria)
        val etHistoria = view.findViewById<TextInputEditText>(R.id.etHistoria)
        val etSenhaLoja = view.findViewById<TextInputEditText>(R.id.etSenhaLoja)

        // Pré-preenche a partir do rascunho compartilhado
        viewModel.draft.value.let { d ->
            etCep.setText(d.cep)
            etEndereco.setText(d.endereco)
            etTipoArt.setText(d.tipoArtesanato)
            acCategoria.setText(d.categoriaProduto, false)
            etHistoria.setText(d.historia)
        }
        etSenhaLoja.setText(viewModel.senhaLoja.value)

        tvBannerSelecionado = view.findViewById(R.id.tvBannerSelecionado)
        tvFotoLojaSelecionada = view.findViewById(R.id.tvFotoLojaSelecionada)
        tvFotosHistoriaSelecionadas = view.findViewById(R.id.tvFotosHistoriaSelecionadas)
        llPreviewHistoria = view.findViewById(R.id.llPreviewHistoria)
        ivHistoria1 = view.findViewById(R.id.ivHistoria1)
        ivHistoria2 = view.findViewById(R.id.ivHistoria2)
        ivHistoria3 = view.findViewById(R.id.ivHistoria3)

        // Restaura os textos/previews de imagem ao reabrir a tela (volta)
        atualizarTextoSelecao()

        acCategoria.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                CategoriasProduto.TODAS
            )
        )

        view.findViewById<Button>(R.id.btnSelecionarBanner).setOnClickListener {
            bannerPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        view.findViewById<Button>(R.id.btnSelecionarFotoLoja).setOnClickListener {
            fotoLojaPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        view.findViewById<Button>(R.id.btnSelecionarFotosHistoria).setOnClickListener {
            fotosHistoriaPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        // Grava os campos desta tela no rascunho (usado ao finalizar e ao voltar)
        fun salvarCampos() {
            viewModel.atualizarDraft {
                it.copy(
                    cep              = etCep.text.toString(),
                    endereco         = etEndereco.text.toString(),
                    tipoArtesanato   = etTipoArt.text.toString(),
                    categoriaProduto = acCategoria.text.toString(),
                    historia         = etHistoria.text.toString()
                )
            }
            viewModel.atualizarSenha(etSenhaLoja.text.toString())
        }

        view.findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            if (etSenhaLoja.text.toString().length < 4) {
                etSenhaLoja.error = "A senha da loja deve ter pelo menos 4 caracteres"
                etSenhaLoja.requestFocus()
                return@setOnClickListener
            }
            salvarCampos()
            viewModel.salvarProdutor(requireContext())
        }

        view.findViewById<TextView>(R.id.btnVoltar3).setOnClickListener {
            salvarCampos()
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultado.collect { resultado ->
                    when (resultado) {
                        is ResultadoCadastro.Sucesso -> {
                            // Barramento único: sinais entre telas sempre no FM da activity
                            requireActivity().supportFragmentManager.setFragmentResult(
                                "cadastro_produtor_request",
                                bundleOf("sucesso" to true)
                            )
                            findNavController().navigate(R.id.action_cadastro_concluido)
                        }
                        is ResultadoCadastro.Erro -> {
                            Log.e("CadastroProdutor", "Falha no cadastro: ${resultado.mensagem}")
                            Toast.makeText(
                                requireContext(),
                                resultado.mensagem,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun atualizarTextoSelecao() {
        tvBannerSelecionado?.text = if (viewModel.bannerUri == null) {
            "Nenhum banner selecionado"
        } else {
            "Banner selecionado"
        }

        tvFotoLojaSelecionada?.text = if (viewModel.fotoLojaUri == null) {
            "Nenhuma foto selecionada"
        } else {
            "Foto da loja selecionada"
        }

        val historia = viewModel.fotosHistoriaUris
        tvFotosHistoriaSelecionadas?.text = when (historia.size) {
            0 -> "Nenhuma foto selecionada"
            1 -> "1 foto selecionada"
            else -> "${historia.size} fotos selecionadas"
        }

        val previews = listOf(ivHistoria1, ivHistoria2, ivHistoria3)
        llPreviewHistoria?.isVisible = historia.isNotEmpty()
        previews.forEachIndexed { index, imageView ->
            imageView?.setImageURI(historia.getOrNull(index))
        }
    }
}
