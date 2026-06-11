package com.aracecultura.arace.ui.components.produto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.sombraInferior
import com.aracecultura.arace.ui.theme.bgDefault

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(id = com.aracecultura.arace.R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )
        val produto by viewModel.produto.collectAsState() // by = desempacota o State automaticamente
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
                    val pagerState = rememberPagerState(pageCount = { listaDeImagens.size.coerceAtLeast(1) })

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (listaDeImagens.isEmpty()) {
                            Image(
                                painter = painterResource(id = com.aracecultura.arace.R.drawable.img_placeholder),
                                contentDescription = "Produto sem imagem",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            HorizontalPager(
                                state = pagerState,
                                key = { index -> index },
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
                                    listaDeImagens.forEachIndexed { index, _ ->
                                        StatusBolinhaGeral(pagerState.currentPage, index)
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        Modifier
                            .zIndex(1f)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .sombraInferior(alturaSombra = 16.dp)
                            .background(bgDefault)
                            .padding(horizontal = 20.dp, vertical = 15.dp)
                    ) {
                        Row(Modifier.background(bgDefault)) {
                            Column {
                                Text(
                                    text = produtoAtual.nome,
                                    fontSize = 26.sp,
                                    //fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            Column(Modifier.padding(top = 10.dp)) {
                                Avaliacao(produtoAtual.avaliacao)
                            }
                        }

                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "R$${produtoAtual.preco}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "${produtoAtual.avaliacao}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    Column(Modifier.padding(top = 20.dp).fillMaxWidth().weight(1f).padding(10.dp, 5.dp)){
                        /*Row(Modifier.fillMaxWidth().background(bgDefault).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically){
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(40.dp))
                                    .background(btColor)
                            )

                            Text(
                                "Paneleiras capixabas",
                                modifier = Modifier.padding(start = 15.dp),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal

                            )
                        }*/
                        Box(Modifier.fillMaxWidth().padding(top = 20.dp).background(bgDefault)) {
                            Text(
                                produtoAtual.descricao,
                                modifier = Modifier.padding(horizontal = 15.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                }
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(2f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Voltar",
                tint = androidx.compose.ui.graphics.Color.Black
            )
        }
    }
}
