package com.aracecultura.arace.ui.components.explorar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.components.SearchBar
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.CategoriasProduto
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun ExplorarProduto(
    viewmodel: ExplorarProdutoViewmodel = viewModel(),
    uid: String,
    onNavigateToProduto: (String) -> Unit = {}
) {
    ConteudoExplorar(
        viewmodel = viewmodel,
        uid = uid,
        onNavigateToProduto = onNavigateToProduto,
        mostrarFiltroCategorias = true,
        header = {
            Text(
                "Descubra",
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgDefault)
                    .padding(vertical = 10.dp)
            )
        }
    )
}

/**
 * Núcleo compartilhado entre Explorar e TelaCategoria: fundo, lista de
 * produtos com o botão de filtros sobreposto, e o painel de filtros
 * deslizante. O [header] e a presença da seção de Categorias no painel são
 * o que distingue as duas telas.
 */
@Composable
fun ConteudoExplorar(
    viewmodel: ExplorarProdutoViewmodel,
    uid: String,
    onNavigateToProduto: (String) -> Unit,
    mostrarFiltroCategorias: Boolean,
    header: @Composable () -> Unit,
    textoBotaoFiltros: String = "Filtros"
) {
    val ordenacaoOpcoes = remember {
        listOf(
            "Nome" to "nome",
            "Menor preço" to "preco_asc",
            "Maior preço" to "preco_desc",
            "Avaliação" to "avaliacao"
        )
    }

    var textPesquisarMu by remember { mutableStateOf("") }
    var mostrarPainel by remember { mutableStateOf(false) }

    val produtos by viewmodel.produtosFiltrados.collectAsState()
    val categoriasSelecionadas by viewmodel.categoriasSelecionadas.collectAsState()
    val ordenacaoAtual by viewmodel.ordenacao.collectAsState()
    val isLoading by viewmodel.isLoading.collectAsState()

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
        Column(modifier = Modifier.fillMaxSize()) {
            header()

            Box(Modifier.fillMaxSize()) {
                LazyColumn {
                    item { Spacer(modifier = Modifier.height(56.dp)) }
                    when {
                        isLoading -> items(4) {
                            ProdutoNavegarSkeleton()
                        }
                        produtos.isEmpty() -> item {
                            Text(
                                text = "Nenhum produto encontrado.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp)
                            )
                        }
                        else -> items(
                            items = produtos,
                            key = { produto -> produto.id }
                        ) { produto ->
                            ProdutoNavegar(
                                produto = produto,
                                onProdutoClick = { onNavigateToProduto(produto.id) },
                                onAddToCartClick = { viewmodel.adicionarAoCarrinho(produto, uid) }
                            )
                        }
                    }
                }

                AppButton(
                    modifier = Modifier
                        // -1dp elimina o fio transparente de antialiasing
                        // na junção com o header acima
                        .offset(y = (-1).dp)
                        .wrapContentWidth()
                        .height(40.dp),
                    text = textoBotaoFiltros,
                    textColor = bgDefault,
                    containerColor = btColor,
                    borderColor = btColor,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 30.dp,
                        bottomStart = 0.dp
                    ),
                    onClick = { mostrarPainel = !mostrarPainel }
                )
            }
        }

        PainelFiltros(
            visible = mostrarPainel,
            mostrarCategorias = mostrarFiltroCategorias,
            ordenacaoOpcoes = ordenacaoOpcoes,
            ordenacaoAtual = ordenacaoAtual,
            categoriasSelecionadas = categoriasSelecionadas,
            textMunicipio = textPesquisarMu,
            onTextMunicipioChange = { textPesquisarMu = it },
            onOrdenacao = viewmodel::setOrdenacao,
            onToggleCategoria = viewmodel::toggleCategoria,
            onFechar = { mostrarPainel = false }
        )
    }
}

@Composable
private fun BoxScope.PainelFiltros(
    visible: Boolean,
    mostrarCategorias: Boolean,
    ordenacaoOpcoes: List<Pair<String, String>>,
    ordenacaoAtual: String,
    categoriasSelecionadas: Set<String>,
    textMunicipio: String,
    onTextMunicipioChange: (String) -> Unit,
    onOrdenacao: (String) -> Unit,
    onToggleCategoria: (String) -> Unit,
    onFechar: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onFechar() }
        )
    }

    AnimatedVisibility(
        visible = visible,
        // slideIn anima só a translação (GPU), sem re-medir o painel a cada frame
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300)),
        modifier = Modifier.align(Alignment.TopCenter)
    ) {
        Column(
            modifier = Modifier
                .background(bgDefault)
                // Consome cliques dentro do painel para não fechá-lo via scrim
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(bottom = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ordenar por",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .width(130.dp)
                        .padding(start = 8.dp, end = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp).width(3.dp).background(btColor))
                Box(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                    var ordenacaoExpandida by remember { mutableStateOf(false) }
                    val labelAtual = ordenacaoOpcoes
                        .firstOrNull { it.second == ordenacaoAtual }?.first
                        ?: "Selecione uma ordem..."

                    Text(
                        text = labelAtual,
                        fontSize = 18.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.clickable { ordenacaoExpandida = true }
                    )
                    DropdownMenu(
                        expanded = ordenacaoExpandida,
                        onDismissRequest = { ordenacaoExpandida = false },
                        modifier = Modifier
                            .background(bgDefault)
                            .width(180.dp)
                    ) {
                        ordenacaoOpcoes.forEach { (label, valor) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontSize = 18.sp,
                                        color = if (ordenacaoAtual == valor) btColor else Color.Black
                                    )
                                },
                                onClick = {
                                    ordenacaoExpandida = false
                                    onOrdenacao(valor)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Município",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .width(130.dp)
                        .padding(start = 8.dp, end = 8.dp)
                        .background(bgDefault)
                )
                Spacer(modifier = Modifier.height(20.dp).width(3.dp).background(btColor))
                SearchBar(
                    modifier = Modifier.weight(1f),
                    text = textMunicipio,
                    textColor = Color.Black,
                    onTextChange = onTextMunicipioChange,
                    containerColor = bgDefault,
                    placeholder = "Pesquise um município"
                )
            }

            // Seção de Categorias: ausente na tela de categoria (filtro fixo)
            if (mostrarCategorias) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Categorias:",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Categorias(categorias = CategoriasProduto.TODAS) { categoria ->
                    val selecionada = categoria in categoriasSelecionadas
                    AppButton(
                        text = categoria,
                        textColor = if (selecionada) bgDefault else btColor,
                        containerColor = if (selecionada) btColor else bgDefault,
                        borderColor = btColor,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(40.dp),
                        onClick = { onToggleCategoria(categoria) }
                    )
                }
            }
        }
    }
}
