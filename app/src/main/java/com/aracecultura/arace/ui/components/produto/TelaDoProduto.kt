package com.aracecultura.arace.ui.components.produto

import android.health.connect.datatypes.ExercisePerformanceGoal
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.explorar.googleFont
import com.aracecultura.arace.ui.components.home.StatusBolinha
import com.aracecultura.arace.ui.theme.bgDefault
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(id = com.aracecultura.arace.R.drawable.bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )
        val produto by viewModel.produto.collectAsState() // by = desempacota o State automaticamente
        val scope = rememberCoroutineScope()
        val produtoAtual = produto
        val scrollState = rememberScrollState()

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)) {
            Column(Modifier
                .fillMaxWidth()
                .weight(1f)) {

                if (produtoAtual == null) {
                    CarregamentoContainer(Modifier.fillMaxWidth())
                } else {
                    val listaDeImagens =
                        produtoAtual.imagens // sem ?. pois o smart cast já garantiu não-nulo
                    val pagerState = rememberPagerState(pageCount = { listaDeImagens.size })

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            key = { index -> listaDeImagens[index] },
                            pageSpacing = 5.dp,
                            modifier = Modifier
                                .fillMaxSize()

                        ) { index ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = listaDeImagens[index],
                                    contentDescription = "Imagem do carrossel",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listaDeImagens.forEachIndexed { index, imagem ->
                                    StatusBolinhaGeral(pagerState.currentPage, index)
                                }
                            }
                        }
                    }
                    Column(Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(bgDefault)) {
                        Row(Modifier
                            .background(bgDefault)
                            .padding(10.dp, 10.dp)) {
                            Text(
                                produtoAtual.nome,
                                fontFamily = googleFont,
                                fontSize = 30.sp,
                                //fontWeight = Weight.Bold
                            )
                            Spacer(Modifier.weight(1f))
                            Box(Modifier.padding(0.dp, 5.dp)){
                                Avaliacao(produtoAtual.avaliacao)
                            }

                        }
                    }
                }
            }
        }
    }
}