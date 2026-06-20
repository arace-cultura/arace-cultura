package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

private data class AncoraScrollCarrinho(
    val itemId: String?,
    val indice: Int,
    val deslocamento: Int
)

@Composable
fun NewCarrinho(
    viewModel: NewCarrinhoViewModel = viewModel(),
    uid: String,
    onFinalizarClick: () -> Unit = {},
    onDeleteClick: (ItemCarrinho) -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    val listState = rememberLazyListState()
    val itens = (estado as? EstadoCarrinho.Pronto)?.itens
    var ancoraPendente by remember { mutableStateOf<AncoraScrollCarrinho?>(null) }

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarCarrinho(uid)
        }
    }

    LaunchedEffect(itens) {
        val ancora = ancoraPendente ?: return@LaunchedEffect
        if (itens.isNullOrEmpty()) {
            ancoraPendente = null
            return@LaunchedEffect
        }

        val indiceDaAncora = ancora.itemId
            ?.let { id -> itens.indexOfFirst { it.id == id } }
            ?.takeIf { it >= 0 }
            ?: ancora.indice.coerceIn(itens.indices)

        // Aguarda a LazyColumn medir a lista atualizada antes de desfazer
        // a correção automática de âncora aplicada perto do fim do conteúdo.
        withFrameNanos { }
        listState.scrollToItem(indiceDaAncora, ancora.deslocamento)
        ancoraPendente = null
    }

    fun guardarAncoraScroll() {
        ancoraPendente = AncoraScrollCarrinho(
            itemId = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.key as? String,
            indice = listState.firstVisibleItemIndex,
            deslocamento = listState.firstVisibleItemScrollOffset
        )
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
            // rola por trás dele; o contentPadding superior da lista evita
            // que o primeiro item nasça ocluso.
            Box(modifier = Modifier.weight(1f)) {
                ListaItens(
                    estado = estado,
                    listState = listState,
                    modifier = Modifier.fillMaxSize(),
                    onAumentarQuantidade = { item ->
                        guardarAncoraScroll()
                        viewModel.alterarQuantidade(item, uid, item.quantidade + 1)
                    },
                    onDiminuirQuantidade = { item ->
                        guardarAncoraScroll()
                        viewModel.alterarQuantidade(item, uid, item.quantidade - 1)
                    },
                    onRemoverItem = { item ->
                        guardarAncoraScroll()
                        viewModel.removerItem(item, uid)
                        onDeleteClick(item)
                    }
                )
                Ordenar(onSelecionar = viewModel::setOrdenacao)
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
        text = stringResource(R.string.carrinho_titulo),
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
    if (estado is EstadoCarrinho.Carregando) {
        Column(modifier = modifier.padding(top = 56.dp, start = 16.dp, end = 16.dp)) {
            repeat(3) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    ProdutoCardItem(produto = null)
                }
            }
        }
        return
    }

    val itens = (estado as EstadoCarrinho.Pronto).itens

    LazyColumn(
        modifier = modifier,
        state = listState,
        // Mantém os últimos cartões fora da zona em que o LazyColumn corrige
        // a âncora após uma atualização de quantidade e desloca o scroll.
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 56.dp,
            bottom = 176.dp
        )
    ) {
        items(
            items = itens,
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
