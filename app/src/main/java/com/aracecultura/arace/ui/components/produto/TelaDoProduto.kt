package com.aracecultura.arace.ui.components.produto

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.DirecaoSombra
import com.aracecultura.arace.ui.components.sombraFormato
import com.aracecultura.arace.ui.components.sombraInferior
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.util.Locale

@Composable
fun TelaDoProduto(
    viewModel: TelaDoProdutoViewmodel,
    onBackClick: () -> Unit = {},
    onProdutorClick: (String) -> Unit = {},
    onAdicionarAoCarrinhoClick: (Produto) -> Unit = {},
    onComprarClick: (Produto) -> Unit = {},
) {
    val produto by viewModel.produto.collectAsState()
    val produtor by viewModel.produtor.collectAsState()
    val avaliacaoUsuario by viewModel.avaliacaoUsuario.collectAsState()
    val salvandoAvaliacao by viewModel.salvandoAvaliacao.collectAsState()
    val erroAvaliacao by viewModel.erroAvaliacao.collectAsState()
    val emDestaque by viewModel.emDestaque.collectAsState()
    var mostrarDialogoAvaliacao by rememberSaveable { mutableStateOf(false) }
    var footerVisivel by remember { mutableStateOf(true) }
    var alturaFooterPx by remember { mutableStateOf(0) }
    val alturaFooterDp = with(LocalDensity.current) { alturaFooterPx.toDp() }
    val deslocamentoFooter by animateIntAsState(
        targetValue = if (footerVisivel) 0 else alturaFooterPx,
        label = "produtoFooterOffset"
    )
    val conexaoScroll = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y < -2f) footerVisivel = false
                else if (consumed.y > 2f || available.y > 2f) footerVisivel = true
                return Offset.Zero
            }
        }
    }
    val produtoAtual = produto

    LaunchedEffect(produtoAtual?.id) {
        footerVisivel = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
            .nestedScroll(conexaoScroll)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.18f)
        )

        if (produtoAtual == null) {
            CarregamentoContainer(Modifier.fillMaxSize())
        } else {
            ConteudoProduto(
                produto = produtoAtual,
                produtor = produtor,
                emDestaque = emDestaque,
                espacoInferior = alturaFooterDp + 24.dp,
                onAvaliarClick = {
                    viewModel.limparErroAvaliacao()
                    mostrarDialogoAvaliacao = true
                },
                onProdutorClick = onProdutorClick,
            )

            FooterProduto(
                produto = produtoAtual,
                onAdicionarAoCarrinhoClick = { onAdicionarAoCarrinhoClick(produtoAtual) },
                onComprarClick = { onComprarClick(produtoAtual) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { alturaFooterPx = it.height }
                    .offset { IntOffset(0, deslocamentoFooter) }
                    .zIndex(1f),
            )
        }

        Box(
            modifier = Modifier
                .padding(12.dp)
                .size(44.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(bgDefault)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.voltar),
                tint = Color.Black
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
    espacoInferior: Dp,
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
        Spacer(Modifier.height(espacoInferior + 36.dp))
    }
}

@Composable
private fun FooterProduto(
    produto: Produto,
    onAdicionarAoCarrinhoClick: () -> Unit,
    onComprarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(139.dp)
    ) {
        val abaPrecoShape = remember { AbaPrecoShape() }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(1f)
                .widthIn(min = 176.dp, max = 320.dp)
                .height(54.dp)
                .sombraFormato(
                    shape = abaPrecoShape,
                    cor = Color.Black.copy(alpha = 0.20f),
                    raioBlur = 7.dp,
                    deslocamentoY = (-3).dp
                )
                .clip(abaPrecoShape)
                .background(bgDefault)
                .padding(start = 34.dp, top = 7.dp, end = 56.dp, bottom = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatarReais(produto.preco),
                color = Color(0xFF1F1B18),
                fontSize = 25.sp,
                lineHeight = 28.sp,
                maxLines = 1,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(85.dp)
                .sombraInferior(
                    DirecaoSombra.CIMA,
                    alturaSombra = 15.dp,
                    corSombra = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 15.dp))
                .background(bgDefault)
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(btColor, bgDefault)
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val esgotado = produto.quantidade <= 0
                    if (esgotado) {
                        BotaoFooterProduto(
                            texto = stringResource(R.string.esgotado),
                            onClick = {},
                            containerColor = Color.LightGray,
                            textColor = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        BotaoFooterProduto(
                            texto = stringResource(R.string.adicionar_ao_carrinho),
                            onClick = onAdicionarAoCarrinhoClick,
                            modifier = Modifier.weight(1.42f)
                        )
                        BotaoFooterProduto(
                            texto = stringResource(R.string.comprar),
                            onClick = onComprarClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BotaoFooterProduto(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = btColor,
    textColor: Color = Color.White,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = texto,
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private class AbaPrecoShape(
    private val cornerRadius: Dp = 15.dp,
    private val slantWidth: Dp = 38.dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = with(density) { cornerRadius.toPx() }.coerceAtMost(size.height / 2f)
        val slant = with(density) { slantWidth.toPx() }.coerceAtMost(size.width / 3f)
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, radius)
            quadraticTo(0f, 0f, radius, 0f)
            lineTo(size.width - slant - radius, 0f)
            quadraticTo(
                size.width - slant * 0.55f,
                0f,
                size.width - slant * 0.35f,
                radius
            )
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun CarrosselProduto(produto: Produto) {
    val imagens = produto.imagens.filter { it.isNotBlank() }

    if (imagens.isEmpty()) {
        CarregamentoContainer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.92f)
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { imagens.size })

    Column(modifier = Modifier.fillMaxWidth().background(bgDefault).padding(top = 64.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.92f)
                .background(Color(0xFFD9D9D9))
        ) {
            HorizontalPager(
                state = pagerState,
                key = { index -> imagens[index] },
                modifier = Modifier.fillMaxSize()
            ) { index ->
                SubcomposeAsyncImage(
                    model = imagens[index],
                    contentDescription = stringResource(R.string.cd_imagem_carrossel),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = { CarregamentoContainer(Modifier.fillMaxSize(), shape = RectangleShape) },
                    error = { CarregamentoContainer(Modifier.fillMaxSize(), shape = RectangleShape) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgDefault)
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imagens.size) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) btColor
                            else Color(0xFFD9D9D9)
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
            text = stringResource(
                R.string.produto_parcelamento,
                formatarReais(produto.preco / 4).removePrefix("R$")
            ),
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
    // Todas as pílulas partem da mesma linha de topo e compartilham a mesma
    // altura de "corpo" (alturaCorpo). A pílula de avaliar repete essa altura
    // no topo e só a aba "Avaliar" estende abaixo da linha de base — assim os
    // números das três pílulas ficam alinhados, como no protótipo. Sem espaço
    // entre as pílulas: os pesos repartem toda a largura disponível.
    val alturaCorpo = 96.dp
    val alturaAbaAvaliar = 40.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Spacer(Modifier.weight(0.025f))

        if (emDestaque) {
            DestaquePill(
                modifier = Modifier
                    .weight(1.1f)
                    .height(alturaCorpo)
            )
        }

        AvaliacaoPill(
            avaliacao = produto.avaliacao,
            modifier = Modifier
                .weight(1.1f)
                .height(alturaCorpo)
        )

        AvaliarPill(
            quantidadeAvaliacoes = produto.quantidadeAvaliacoes,
            onClick = onAvaliarClick,
            alturaTopo = alturaCorpo,
            alturaAba = alturaAbaAvaliar,
            modifier = Modifier.weight(0.87f)
        )

        Spacer(Modifier.weight(0.025f))
    }
}

@Composable
private fun DestaquePill(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(44.dp))
            .background(btColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.produto_em_destaque),
            color = Color.White,
            fontSize = 22.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
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
            .background(bgDefault)
            .padding(horizontal = 2.dp),
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
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun AvaliarPill(
    quantidadeAvaliacoes: Int,
    onClick: () -> Unit,
    alturaTopo: Dp,
    alturaAba: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(alturaTopo + alturaAba)
            .clip(RoundedCornerShape(34.dp))
            .border(2.dp, btColor, RoundedCornerShape(34.dp))
            .background(btColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Topo com a mesma altura das outras pílulas: mantém o número e o
        // "avaliações" na mesma linha de base das pílulas vizinhas.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaTopo)
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
                text = stringResource(R.string.produto_avaliacoes),
                color = btColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaAba),
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
    val produtorGenerico = stringResource(R.string.produtor_generico)

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
                .ifBlank { produtorGenerico },
            fontSize = 26.sp,
            lineHeight = 30.sp
        )
    }
}

@Composable
private fun BlocoEspecificacoes(produto: Produto) {
    val categoriaPrefixo = stringResource(R.string.produto_categoria_prefixo)
    val descricaoPrefixo = stringResource(R.string.produto_descricao_prefixo)
    val textoNaoInformado = stringResource(R.string.nao_informado)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 22.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgDefault.copy(alpha = 0.88f))
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.produto_especificacoes),
            color = Color(0xFF606060),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = buildString {
                if (produto.categorias.isNotEmpty()) {
                    append(categoriaPrefixo)
                    append(' ')
                    append(produto.categorias.joinToString(", "))
                    append('\n')
                }
                append(descricaoPrefixo)
                append(' ')
                append(produto.descricao.ifBlank { textoNaoInformado })
            },
            color = Color(0xFF303030),
            fontSize = 18.sp,
            lineHeight = 23.sp
        )
    }
}

private fun formatarReais(valor: Double): String =
    "R$" + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor)
