package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.ItemCarrinho
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
    val listState = rememberLazyListState()

    // ---- Instrumentação temporária do reset de scroll ----
    DisposableEffect(Unit) {
        Log.d("CarrinhoDebug", "Composição CRIADA (listState=${listState.hashCode()})")
        onDispose { Log.d("CarrinhoDebug", "Composição DESCARTADA") }
    }
    LaunchedEffect(listState) {
        snapshotFlow {
            Triple(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset,
                listState.layoutInfo.visibleItemsInfo.firstOrNull()?.key
            )
        }.collect { (index, offset, key) ->
            Log.d(
                "CarrinhoDebug",
                "scroll: index=$index offset=$offset primeiraKey=$key " +
                    "total=${listState.layoutInfo.totalItemsCount} " +
                    "viewport=${listState.layoutInfo.viewportSize} " +
                    "emProgresso=${listState.isScrollInProgress}"
            )
        }
    }
    // ------------------------------------------------------

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

        // Column simples no lugar de Scaffold: o bottomBar do Scaffold é uma
        // subcomposição medida durante o layout e, lendo o mesmo estado que o
        // clique altera, forçava re-subcomposição/remedição da LazyColumn no
        // mesmo frame — reescrevendo a posição de scroll (recuo fixo de 441px)
        Column(modifier = Modifier.fillMaxSize()) {
            TituloCarrinho()
            // Mesmo padrão do Explorar: o botão é overlay e a lista
            // rola por trás dele; o Spacer inicial em ListaItens evita
            // que o primeiro item nasça ocluso.
            Box(modifier = Modifier.weight(1f)) {
                ListaItens(
                    estado = estado,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
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
                Ordenar()
            }
            SecaoFinalizarCompra(
                produtos = (estado as? EstadoCarrinho.Pronto)?.itens ?: emptyList(),
                onFinalizarClick = onFinalizarClick
            )
        }
    }
}

@Composable
private fun TituloCarrinho() {
    Text(
        text = "Carrinho",
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
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onAumentarQuantidade: (ItemCarrinho) -> Unit,
    onDiminuirQuantidade: (ItemCarrinho) -> Unit,
    onRemoverItem: (ItemCarrinho) -> Unit
) {
    if (estado is EstadoCarrinho.Pronto) {
        Log.d(
            "CarrinhoDebug",
            "render(listState=${listState.hashCode()}): " +
                estado.itens.joinToString { "${it.id.takeLast(5)}=${it.quantidade}" }
        )
    }
    // Espaçamento via padding dos itens em vez de Arrangement.spacedBy:
    // a aritmética dos saltos de scroll (alvo sempre 66px antes do item
    // visível = spacing + 24px) implicava o spacedBy na remedição
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item(key = "espaco_topo") { Spacer(modifier = Modifier.height(56.dp)) }
        when (estado) {
            is EstadoCarrinho.Carregando -> items(3, key = { "skeleton_$it" }) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    ProdutoCardItem(produto = null)
                }
            }
            is EstadoCarrinho.Pronto -> items(
                items = estado.itens,
                key = { it.id }
            ) { item ->
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
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
}
