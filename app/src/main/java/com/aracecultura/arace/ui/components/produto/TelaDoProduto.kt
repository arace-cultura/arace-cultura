package com.aracecultura.arace.ui.components.produto

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.core.R
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.home.StatusBolinha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel
) {
    val produto by viewModel.produto.collectAsState() // by = desempacota o State automaticamente
    val scope = rememberCoroutineScope()
    val produtoAtual = produto

    Column(Modifier.fillMaxSize()) {

        if (produtoAtual == null) {
            CarregamentoContainer(Modifier.fillMaxWidth())
        } else {
            val listaDeImagens = produtoAtual.imagens // sem ?. pois o smart cast já garantiu não-nulo
            val pagerState = rememberPagerState(pageCount = { listaDeImagens.size })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    key = { index -> listaDeImagens[index] },
                    pageSpacing = 5.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) { index ->
                    AsyncImage(
                        model = listaDeImagens[index],
                        contentDescription = "Imagem do carrossel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }

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
                    listaDeImagens.forEachIndexed { index, imagem ->
                        StatusBolinha(pagerState.currentPage, index, R.drawable.circle)
                    }
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            val nextPage = minOf(
                                listaDeImagens.size - 1,
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


    }
}