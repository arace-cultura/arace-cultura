package com.aracecultura.arace.ui.components.produto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.sombraInferior
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel,
    onBackClick: () -> Unit = {},
    onProdutorClick: (String) -> Unit = {}
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
        val produtor by viewModel.produtor.collectAsState()
        val avaliacaoUsuario by viewModel.avaliacaoUsuario.collectAsState()
        val salvandoAvaliacao by viewModel.salvandoAvaliacao.collectAsState()
        val erroAvaliacao by viewModel.erroAvaliacao.collectAsState()
        var mostrarDialogoAvaliacao by rememberSaveable { mutableStateOf(false) }
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
                                contentDescription = stringResource(R.string.cd_produto_sem_imagem),
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
                                        contentDescription = stringResource(R.string.cd_imagem_carrossel),
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
                        // Nome do produto em uma linha própria
                        Text(
                            text = produtoAtual.nome,
                            fontSize = 26.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.size(10.dp))

                        // Botão centralizado na área livre e avaliação à direita.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                            ) {
                                BotaoAvaliarProduto(
                                    onClick = {
                                        viewModel.limparErroAvaliacao()
                                        mostrarDialogoAvaliacao = true
                                    },
                                    modifier = Modifier.width(150.dp)
                                )
                            }
                            Spacer(Modifier.size(12.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Avaliacao(produtoAtual.avaliacao)
                                Spacer(Modifier.size(4.dp))
                                Text(
                                    text = "${produtoAtual.avaliacao}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(Modifier.size(12.dp))

                        Text(
                            text = stringResource(R.string.preco_reais, produtoAtual.preco),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Column(Modifier.padding(top = 20.dp).fillMaxWidth().weight(1f).padding(10.dp, 5.dp)){
                        // Produtor (loja) responsável: foto circular com borda
                        // btColor + nome à direita. Clicável → perfil da loja
                        // na visão do cliente (somente leitura).
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = produtoAtual.produtorId.isNotBlank()) {
                                    onProdutorClick(produtoAtual.produtorId)
                                }
                                .background(bgDefault)
                                .padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .border(2.dp, btColor, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD9D9D9))
                                ) {
                                    val fotoLoja = produtor?.fotoLoja
                                    if (!fotoLoja.isNullOrBlank()) {
                                        AsyncImage(
                                            model = fotoLoja,
                                            contentDescription = stringResource(R.string.cd_foto_produtor),
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }

                            val nomeProdutor = produtor?.let {
                                it.nomeLoja.ifBlank { it.nomeCompleto }
                            }.orEmpty().ifBlank { "Produtor" }
                            Text(
                                text = nomeProdutor,
                                modifier = Modifier.padding(start = 15.dp),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
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
                .clip(CircleShape)
                .background(bgDefault)
                .zIndex(2f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.voltar),
                tint = androidx.compose.ui.graphics.Color.Black
            )
        }
        if (mostrarDialogoAvaliacao) {
            val mensagemErro = when (erroAvaliacao) {
                ErroAvaliacao.USUARIO_NAO_AUTENTICADO ->
                    stringResource(R.string.erro_usuario_nao_autenticado)
                ErroAvaliacao.SALVAR ->
                    stringResource(R.string.erro_salvar_avaliacao)
                null -> null
            }
            DialogoAvaliarProduto(
                notaAtual = avaliacaoUsuario,
                salvando = salvandoAvaliacao,
                erro = mensagemErro,
                onAvaliar = { nota ->
                    viewModel.avaliarProduto(nota) {
                        mostrarDialogoAvaliacao = false
                    }
                },
                onCancelar = {
                    viewModel.limparErroAvaliacao()
                    mostrarDialogoAvaliacao = false
                }
            )
        }
    }
}
