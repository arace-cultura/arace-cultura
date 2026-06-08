package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault

sealed interface EstadoCarrinho {
    data object Carregando : EstadoCarrinho
    data class Pronto(val itens: List<ItemCarrinho>) : EstadoCarrinho
}

@Composable
fun NewCarrinho(
    viewModel: NewCarrinhoViewModel = viewModel(),
    uid: String,
    onFinalizarClick: () -> Unit = {},
    onDeleteClick: (ItemCarrinho) -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarCarrinho(uid)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.3f
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                val itens = (estado as? EstadoCarrinho.Pronto)?.itens ?: emptyList()
                SecaoFinalizarCompra(
                    produtos = itens,
                    onFinalizarClick = onFinalizarClick
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TituloCarrinho()
                Ordenar()
                Spacer(modifier = Modifier.height(24.dp))
                ListaItens(
                    estado = estado,
                    modifier = Modifier.weight(1f),
                    onAumentarQuantidade = { item ->
                        viewModel.alterarQuantidade(item, uid, item.quantidade + 1)
                    },
                    onDiminuirQuantidade = { item ->
                        viewModel.alterarQuantidade(item, uid, item.quantidade - 1)
                    },
                    onRemoverItem = { item ->
                        viewModel.removerItem(item, uid)
                        onDeleteClick(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun TituloCarrinho() {
    Text(
        text = "Carrinho",
        fontFamily = GoogleSans,
        fontSize = 36.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgDefault)
            .padding(vertical = 10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ListaItens(
    estado: EstadoCarrinho,
    modifier: Modifier = Modifier,
    onAumentarQuantidade: (ItemCarrinho) -> Unit,
    onDiminuirQuantidade: (ItemCarrinho) -> Unit,
    onRemoverItem: (ItemCarrinho) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (estado) {
            is EstadoCarrinho.Carregando -> items(3) {
                ProdutoCardItem(produto = null)
            }
            is EstadoCarrinho.Pronto -> items(
                items = estado.itens,
                key = { it.id }
            ) { item ->
                ProdutoCardItem(
                    produto = item.produto,
                    quantidade = item.quantidade,
                    onIncreaseClick = { onAumentarQuantidade(item) },
                    onDecreaseClick = { onDiminuirQuantidade(item) },
                    onDeleteClick = { onRemoverItem(item) }
                )
            }
        }
    }
}
