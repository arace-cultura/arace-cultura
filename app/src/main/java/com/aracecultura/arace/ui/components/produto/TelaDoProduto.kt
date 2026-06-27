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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.util.Locale

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel,
    onBackClick: () -> Unit = {},
    onProdutorClick: (String) -> Unit = {}
) {
    val produto by viewModel.produto.collectAsState()
    val produtor by viewModel.produtor.collectAsState()
    val avaliacaoUsuario by viewModel.avaliacaoUsuario.collectAsState()
    val salvandoAvaliacao by viewModel.salvandoAvaliacao.collectAsState()
    val erroAvaliacao by viewModel.erroAvaliacao.collectAsState()
    val emDestaque by viewModel.emDestaque.collectAsState()
    var mostrarDialogoAvaliacao by rememberSaveable { mutableStateOf(false) }

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
                .alpha(0.18f)
        )

        val produtoAtual = produto
        if (produtoAtual == null) {
            CarregamentoContainer(Modifier.fillMaxSize())
        } else {
            ConteudoProduto(
                produto = produtoAtual,
                produtor = produtor,
                emDestaque = emDestaque,
                onAvaliarClick = {
                    viewModel.limparErroAvaliacao()
                    mostrarDialogoAvaliacao = true
                },
                onProdutorClick = onProdutorClick,
            )
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
                .zIndex(2f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.voltar),
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }

        IconButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 18.dp, top = 34.dp)
                .zIndex(2f)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_menu),
                contentDescription = stringResource(R.string.configuracoes),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
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

@Composable
private fun ConteudoProduto(
    produto: Produto,
    produtor: Produtor?,
    emDestaque: Boolean,
    onAvaliarClick: () -> Unit,
    onProdutorClick: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = scrollState,
                overscrollEffect = null,
            )
    ) {
        CarrosselProduto(produto = produto)
        BlocoResumoProduto(produto = produto)
        BlocoSocialProduto(produto = produto, emDestaque = emDestaque, onAvaliarClick = onAvaliarClick)
        LinhaProdutor(produto = produto, produtor = produtor, onProdutorClick = onProdutorClick)
        BlocoEspecificacoes(produto = produto)
        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun CarrosselProduto(produto: Produto) {
    val imagens = produto.imagens
    val pagerState = rememberPagerState(pageCount = { imagens.size.coerceAtLeast(1) })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .background(Color(0xFFD9D9D9))
    ) {
        HorizontalPager(
            state = pagerState,
            key = { index -> imagens.getOrNull(index) ?: "placeholder" },
            modifier = Modifier.fillMaxSize()
        ) { index ->
            val imagem = imagens.getOrNull(index)
            if (imagem.isNullOrBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.img_placeholder),
                    contentDescription = stringResource(R.string.cd_produto_sem_imagem),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = imagem,
                    contentDescription = stringResource(R.string.cd_imagem_carrossel),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imagens.size.coerceAtLeast(3)) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) Color.White
                            else Color.White.copy(alpha = 0.55f)
                        )
                )
            }
        }
    }
}

@Composable
private fun BlocoResumoProduto(produto: Produto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgDefault)
            .padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        Text(
            text = produto.nome,
            fontSize = 38.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 42.sp
        )
        Text(
            text = formatarReais(produto.preco),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )
        Text(
            text = "Ou 4x${formatarReais(produto.preco / 4).removePrefix("R$")}",
            fontSize = 17.sp,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(34.dp))
        Text(
            text = produto.descricao,
            fontSize = 22.sp,
            lineHeight = 28.sp
        )
    }
}

@Composable
private fun BlocoSocialProduto(
    produto: Produto,
    emDestaque: Boolean,
    onAvaliarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (emDestaque) {
            DestaquePill(
                modifier = Modifier
                    .weight(1.1f)
                    .height(90.dp)
            )
        }

        AvaliacaoPill(
            avaliacao = produto.avaliacao,
            modifier = Modifier
                .weight(1f)
                .height(88.dp)
        )

        AvaliarPill(
            quantidadeAvaliacoes = produto.quantidadeAvaliacoes,
            onClick = onAvaliarClick,
            modifier = Modifier
                .weight(0.9f)
                .height(112.dp)
        )
    }
}

@Composable
private fun DestaquePill(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(44.dp))
            .background(btColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Em destaque",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AvaliacaoPill(
    avaliacao: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(44.dp))
            .border(2.dp, btColor, RoundedCornerShape(44.dp))
            .background(bgDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = avaliacao.toInt().coerceIn(0, 5).toString(),
            color = btColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Row(horizontalArrangement = Arrangement.Center) {
            repeat(5) { index ->
                Icon(
                    painter = painterResource(
                        if (index < avaliacao.toInt()) R.drawable.ic_estrela1 else R.drawable.ic_estrela0
                    ),
                    contentDescription = null,
                    tint = btColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun AvaliarPill(
    quantidadeAvaliacoes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(34.dp))
            .border(2.dp, btColor, RoundedCornerShape(34.dp))
            .background(btColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp))
                .background(bgDefault),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = quantidadeAvaliacoes.toString(),
                color = btColor,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 31.sp
            )
            Text(
                text = "avaliações",
                color = btColor,
                fontSize = 15.sp,
                lineHeight = 18.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.avaliar),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LinhaProdutor(
    produto: Produto,
    produtor: Produtor?,
    onProdutorClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgDefault)
            .clickable(enabled = produto.produtorId.isNotBlank()) {
                onProdutorClick(produto.produtorId)
            }
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
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
        Spacer(Modifier.width(20.dp))
        Text(
            text = produtor?.let { it.nomeLoja.ifBlank { it.nomeCompleto } }
                .orEmpty()
                .ifBlank { "Produtor" },
            fontSize = 26.sp,
            lineHeight = 30.sp
        )
    }
}

@Composable
private fun BlocoEspecificacoes(produto: Produto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 22.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgDefault.copy(alpha = 0.88f))
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Especificações",
            color = Color(0xFF606060),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = buildString {
                if (produto.categorias.isNotEmpty()) {
                    append("Categoria: ")
                    append(produto.categorias.joinToString(", "))
                    append('\n')
                }
                append("Descrição: ")
                append(produto.descricao.ifBlank { "Não informado." })
            },
            color = Color(0xFF303030),
            fontSize = 18.sp,
            lineHeight = 23.sp
        )
    }
}

private fun formatarReais(valor: Double): String =
    "R$" + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor)
