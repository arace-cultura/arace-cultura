package com.aracecultura.arace.ui.components.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun TelaHome(
    viewmodel: TelaHomeViewmodel = viewModel(),
    onProdutoClick: (String) -> Unit = {}
) {
    val produtos by viewmodel.produtos.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            TituloSecao("Categorias")
            Spacer(modifier = Modifier.height(15.dp))
            SecaoCategorias()
            Spacer(modifier = Modifier.height(15.dp))

            TituloSecao("Produtos em destaque")
            Spacer(modifier = Modifier.height(10.dp))

            if (produtos.isNotEmpty()) {
                CarrosselProdutos(
                    produtos = produtos,
                    onProdutoClick = onProdutoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
            } else {
                CarregamentoContainer(Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
private fun TituloSecao(titulo: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgDefault)
    ) {
        Text(
            text = titulo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CarrosselProdutos(
    produtos: List<Produto>,
    onProdutoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { produtos.size })
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.interactionSource) {
        val dragAtivos = mutableListOf<DragInteraction.Start>()
        pagerState.interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start  -> dragAtivos.add(interaction)
                is DragInteraction.Stop   -> dragAtivos.remove(interaction.start)
                is DragInteraction.Cancel -> dragAtivos.remove(interaction.start)
            }
            isDragging = dragAtivos.isNotEmpty()
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(3000L)
            if (!isDragging) {
                val proxima = (pagerState.currentPage + 1) % produtos.size
                runCatching {
                    pagerState.animateScrollToPage(proxima, animationSpec = tween(800))
                }
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        key = { index -> produtos[index].id },
        pageSpacing = 16.dp,
        modifier = modifier
    ) { index ->
        CartaoProduto(
            produto = produtos[index],
            onClick = { onProdutoClick(produtos[index].id) }
        )
    }
}

@Composable
private fun CartaoProduto(
    produto: Produto,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        SubcomposeAsyncImage(
            model = produto.imagens.firstOrNull(),
            contentDescription = "Imagem de ${produto.nome}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
            loading = { CarregamentoContainer(Modifier.fillMaxSize()) },
            error   = { CarregamentoContainer(Modifier.fillMaxSize()) }
        )
        RodapeCartaoProduto(produto = produto)
    }
}

@Composable
private fun RodapeCartaoProduto(produto: Produto) {
    val descricaoResumida = remember(produto.descricao) {
        if (produto.descricao.length > 40) produto.descricao.take(37) + "…"
        else produto.descricao
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
            .background(btColor)
    ) {
        Row(modifier = Modifier.padding(start = 20.dp, top = 4.dp, end = 20.dp)) {
            Text(
                text = produto.nome,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "R$${produto.preco}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Row(modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)) {
            Text(
                text = descricaoResumida,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}