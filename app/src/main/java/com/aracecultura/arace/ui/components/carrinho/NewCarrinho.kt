package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.navigation.LocalAppFooterHeight
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
    val scrollState = rememberScrollState()
    val itens = (estado as? EstadoCarrinho.Pronto)?.itens.orEmpty()
    val alturaFooterApp = LocalAppFooterHeight.current

    // Mede a altura real do footer (overlay) e reserva exatamente esse espaço
    // no fim da lista. Assim o último item nunca fica ocluso — a reserva não
    // depende de um número fixo que possa dessincronizar do footer. Como o
    // footer tem altura fixa, a reserva é estável e não reintroduz salto.
    val alturaFinalizar = if (itens.isNotEmpty()) AlturaSecaoFinalizarCompra else 0.dp
    val espacoInferiorLista = alturaFinalizar + alturaFooterApp + 16.dp

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

        Column(modifier = Modifier.fillMaxSize()) {
            TituloCarrinho()
            Box(modifier = Modifier.weight(1f)) {
                ListaItens(
                    estado = estado,
                    scrollState = scrollState,
                    espacoInferior = espacoInferiorLista,
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
                Ordenar(onSelecionar = viewModel::setOrdenacao)

                // Footer em OVERLAY (dentro do Box, não irmão do Column com
                // weight): a recomposição do total no +/- não força a re-medição
                // da lista — é isso que elimina o salto. onSizeChanged alimenta a
                // reserva inferior medida lá em cima.
                SecaoFinalizarCompra(
                    produtos = itens,
                    onFinalizarClick = onFinalizarClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = alturaFooterApp)
                )
            }
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
    scrollState: ScrollState,
    espacoInferior: Dp,
    modifier: Modifier = Modifier,
    onAumentarQuantidade: (ItemCarrinho) -> Unit,
    onDiminuirQuantidade: (ItemCarrinho) -> Unit,
    onRemoverItem: (ItemCarrinho) -> Unit
) {
    if (estado is EstadoCarrinho.Carregando) {
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(top = 56.dp, start = 16.dp, end = 16.dp)
        ) {
            repeat(3) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    ProdutoCardItem(produto = null)
                }
            }
        }
        return
    }

    val itens = (estado as EstadoCarrinho.Pronto).itens

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(56.dp))

        itens.forEach { item ->
            // key estabiliza a identidade do card: ao reordenar, a composição
            // (e o estado da AsyncImage) se move junto com o item em vez de ser
            // reconstruída na posição — sem recarregar imagem nem piscar.
            key(item.id) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
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

        Spacer(Modifier.height(espacoInferior))
    }
}
