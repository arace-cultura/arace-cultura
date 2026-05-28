package com.aracecultura.arace.ui.components.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.explorar.googleFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun TelaHome(
    viewmodel: TelaHomeViewmodel = viewModel(),
    onProdutoClick: (String) -> Unit = {}
) {
    val produtos: State<List<Produto>> = viewmodel.produtos.collectAsState()
    val listaDeProdutos = produtos.value
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(20.dp))
        SecaoCategorias()
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Produtos em destaque",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            fontSize = 26.sp,
            fontFamily = googleFont,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(20.dp))


        if (listaDeProdutos.isNotEmpty()) {
            val ecoScrollDelay = 3000L
            val pagerState = rememberPagerState(pageCount = { listaDeProdutos.size })
            val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

            LaunchedEffect(pagerState.settledPage, isDragged) {
                if (!isDragged) {
                    delay(ecoScrollDelay)
                    val nextPage = if (pagerState.settledPage < listaDeProdutos.size - 1) {
                        pagerState.settledPage + 1
                    } else {
                        0
                    }
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = 800)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    key = { index -> listaDeProdutos[index].id },
                    pageSpacing = 16.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) { index ->
                    val produtoAtual = listaDeProdutos[index]
                    Column(Modifier.clickable {onProdutoClick(produtoAtual.id)}){
                        AsyncImage(
                            model = produtoAtual.imagens[0],
                            contentDescription = "Imagem do carrossel",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(15.dp, 15.dp)),
                        )
                        Column(Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
                            .background(btColor)
                        ) {
                            Row(Modifier.padding(20.dp, 4.dp, 20.dp)) {
                                Text(
                                    produtoAtual.nome,
                                    fontFamily = googleFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "R$${produtoAtual.preco}",
                                    fontFamily = googleFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(0.dp, 2.dp, 0.dp,0.dp)
                                )
                            }
                            Row(Modifier.padding(20.dp, 0.dp, 0.dp, 8.dp)) {
                                Text(
                                    text = if (produtoAtual.descricao.length > 40)
                                        produtoAtual.descricao.take(37) + "..." else produtoAtual.descricao,
                                    fontFamily = googleFont,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1,
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }

                        },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(Color(0xFFFF5733), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listaDeProdutos.forEachIndexed { index, imagem ->
                            StatusBolinha(pagerState.currentPage, index)
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                val nextPage = minOf(
                                    listaDeProdutos.size - 1,
                                    pagerState.currentPage + 1
                                )
                                pagerState.animateScrollToPage(
                                    page = nextPage,
                                    animationSpec = tween(durationMillis = 500),
                                )
                            }

                        },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(Color(0xFFFF5733), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            CarregamentoContainer(Modifier.padding(10.dp))
        }
    }
}