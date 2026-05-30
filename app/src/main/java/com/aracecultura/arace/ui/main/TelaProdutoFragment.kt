package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.aracecultura.arace.ui.components.produto.TelaDoProduto
import com.aracecultura.arace.ui.components.produto.TelaDoProdutoViewmodel

class TelaProdutoFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private val args: TelaProdutoFragmentArgs by navArgs()
    private val viewModel: TelaDoProdutoViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.carregarProduto(args.produtoId)

        composeView.setContent {
            TelaDoProduto(viewModel = viewModel)
        }
    }
}