package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.perfil.BotaoVisualizacao
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.ui.theme.verdePrincipal

@Composable
fun PerfilProdutor(
    uid: String = "",
    lojaId: String? = null,
    somenteLeitura: Boolean = false,
    viewModel: PerfilProdutorViewModel = viewModel(),
    onModoChanged: (Boolean) -> Unit = {},
    onEditarProdutos: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    // lojaId != null → visão do cliente (perfil de outra loja, somente leitura).
    LaunchedEffect(uid, lojaId) {
        if (lojaId != null) viewModel.carregarPerfilPorLoja(lojaId)
        else viewModel.carregarPerfil(uid)
    }

    when {
        uiState.isLoading -> PerfilProdutorLoading()
        uiState.errorMessage != null -> PerfilProdutorErro(uiState.errorMessage.orEmpty())
        uiState.produtor != null -> PerfilProdutorContent(
            produtor = uiState.produtor!!,
            produtos = uiState.produtos,
            somenteLeitura = somenteLeitura,
            onModoChanged = onModoChanged,
            onEditarProdutos = onEditarProdutos,
            onBack = onBack
        )
    }
}

@Composable
private fun PerfilProdutorLoading() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.55f)
                    .background(btColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth * 0.15f)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(bgDefault)
                )

                CarregamentoContainer(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(screenWidth * 0.4f)
                        .clip(CircleShape)
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgDefault)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarregamentoContainer(modifier = Modifier.height(40.dp).fillMaxWidth(0.65f))
                CarregamentoContainer(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .height(22.dp)
                        .fillMaxWidth(0.7f)
                        .align(Alignment.Start)
                )
                CarregamentoContainer(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(240.dp)
                        .fillMaxWidth()
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                CarregamentoContainer(modifier = Modifier.height(22.dp).fillMaxWidth(0.45f))
                CarregamentoContainer(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .height(165.dp)
                        .fillMaxWidth()
                )
                CarregamentoContainer(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(72.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PerfilProdutorErro(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color(0xFF3B4045),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PerfilProdutorContent(
    produtor: Produtor,
    produtos: List<Produto>,
    somenteLeitura: Boolean = false,
    onModoChanged: (Boolean) -> Unit = {},
    onEditarProdutos: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val loja = produtor.nomeLoja.ifBlank { produtor.nomeCompleto }
    val nomeExibicao = loja.ifBlank { "Produtor" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.55f)
            ) {
                if (produtor.banner.isBlank()) {
                    Box(
                        modifier = Modifier
                            .height(screenWidth * 0.55f)
                            .fillMaxWidth()
                            .background(btColor)
                    )
                } else {
                    AsyncImage(
                        model = produtor.banner,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(screenWidth * 0.55f)
                            .fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth * 0.15f)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(bgDefault)
                )

                // Na visão do cliente não há troca de modo nem edição.
                if (!somenteLeitura) {
                    BotaoVisualizacao(
                        modoAtualIsProdutor = true,
                        onModoChanged = onModoChanged,
                        modifier = Modifier.align(Alignment.TopEnd).offset(y = (-2).dp)
                    )
                }

                if (onBack != null) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(44.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(bgDefault)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = stringResource(R.string.voltar),
                            tint = Color.Black
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(screenWidth * 0.4f)
                        .clip(CircleShape)
                        .background(verdePrincipal),
                    contentAlignment = Alignment.Center
                ) {
                    if (produtor.fotoLoja.isBlank()) {
                        Text(
                            text = nomeExibicao.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        AsyncImage(
                            model = produtor.fotoLoja,
                            contentDescription = stringResource(R.string.cd_foto_loja),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgDefault)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nomeExibicao,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                Text(
                    text = stringResource(R.string.nossos_produtos_destaque),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 16.dp),
                    textAlign = TextAlign.Start
                )

                ImageCarousel(imageUrls = produtos.flatMap { it.imagens }.take(5))
            }
        }

        if (!somenteLeitura) {
            item {
                BotaoEditarProdutos(onClick = onEditarProdutos)
            }
        }

        item {
            NossaHistoriaSection(
                brandColor = btColor,
                produtor = produtor
            )
        }

        item {
            TodosProdutosSection(
                brandColor = btColor,
                produtos = produtos
            )
        }

        item {
            FooterSection(
                brandColor = btColor,
                produtor = produtor
            )
        }
    }
}

@Composable
private fun BotaoEditarProdutos(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(btColor)
                .clickable { onClick() }
                .padding(horizontal = 56.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_editar_produto),
                contentDescription = stringResource(R.string.editar_produtos),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.editar_produtos_botao),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
