package com.aracecultura.arace.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aracecultura.arace.AppViewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.ui.components.carrinho.CheckoutPaymentScreen
import com.aracecultura.arace.ui.components.carrinho.NewCarrinho
import com.aracecultura.arace.ui.components.carrinho.NewCarrinhoViewModel
import com.aracecultura.arace.ui.components.criar.CriarProduto
import com.aracecultura.arace.ui.components.vendas.TelaVendas
import com.aracecultura.arace.ui.components.explorar.ExplorarProduto
import com.aracecultura.arace.ui.components.explorar.TelaCategoria
import com.aracecultura.arace.ui.components.home.TelaHome
import com.aracecultura.arace.ui.components.perfil.produtor.PerfilProdutor
import com.aracecultura.arace.ui.components.produto.TelaDoProduto
import com.aracecultura.arace.ui.components.produto.TelaDoProdutoViewmodel
import com.aracecultura.arace.ui.main.jetpack.Modo
import com.aracecultura.arace.ui.theme.bgDefault
import com.google.firebase.auth.FirebaseAuth

private data class ItemNav(
    val rota: Rota,
    @param:DrawableRes val iconeAtivo: Int,
    @param:DrawableRes val iconeInativo: Int,
    @param:StringRes val titulo: Int,
)

private val itensCliente = listOf(
    ItemNav(Rota.Home, R.drawable.ic_home_filled, R.drawable.ic_home_outline, R.string.nav_inicio),
    ItemNav(Rota.Explorar, R.drawable.ic_explorar_produto_filled, R.drawable.ic_explorar_produto_outline, R.string.nav_explorar),
    ItemNav(Rota.Carrinho, R.drawable.ic_carrinho_filled, R.drawable.ic_carrinho_outline, R.string.nav_carrinho),
    ItemNav(Rota.PerfilCliente, R.drawable.ic_user_filled, R.drawable.ic_user_outline, R.string.nav_eu),
)

private val itensProdutor = listOf(
    ItemNav(Rota.Home, R.drawable.ic_home_filled, R.drawable.ic_home_outline, R.string.nav_inicio),
    ItemNav(Rota.TelaVendas, R.drawable.ic_envios, R.drawable.ic_envios, R.string.nav_vendas),
    ItemNav(Rota.CriarProduto, R.drawable.ic_adicionar, R.drawable.ic_adicionar, R.string.nav_criar),
    ItemNav(Rota.PerfilProdutor, R.drawable.ic_produtor, R.drawable.ic_produtor, R.string.nav_perfil)
)

val LocalAppFooterHeight = staticCompositionLocalOf { 0.dp }

@Composable
fun AppScaffold(rootNav: NavController, appVm: AppViewModel) {
    val innerNav = rememberNavController()
    val modo by appVm.modo.collectAsStateWithLifecycle()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val entry by innerNav.currentBackStackEntryAsState()
    val destino = entry?.destination
    var footerVisivelNoPerfilCliente by remember { mutableStateOf(true) }

    // Footer some de vez na tela de produto e no checkout (telas focadas)
    val mostrarFooter = destino?.let {
        !it.hasRoute(Rota.Produto::class) &&
                !it.hasRoute(Rota.FinalizarCompra::class) &&
                (!it.hasRoute(Rota.PerfilCliente::class) || footerVisivelNoPerfilCliente)
    } ?: true

    // Footer recolhível no scroll: desliza para baixo ao rolar para baixo e
    // reaparece ao rolar para cima. Reseta para visível ao trocar de tela.
    var footerVisivel by remember { mutableStateOf(true) }
    var alturaFooterPx by remember { mutableStateOf(0) }
    LaunchedEffect(destino?.route) { footerVisivel = true }
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

    val itens = if (modo == Modo.PRODUTOR) itensProdutor else itensCliente
    val alturaFooterDp = with(LocalDensity.current) { alturaFooterPx.toDp() }
    val espacoFooterOverlay = if (mostrarFooter) alturaFooterDp else 0.dp

    // Ao trocar de modo enquanto estou num perfil, vou para o perfil do novo
    // modo (antes era o configurarMenuCliente/Produtor do NavegacaoPrincipal).
    LaunchedEffect(modo) {
        val atual = innerNav.currentBackStackEntry?.destination ?: return@LaunchedEffect
        if (modo == Modo.PRODUTOR && atual.hasRoute(Rota.PerfilCliente::class)) {
            innerNav.navigate(Rota.PerfilProdutor) {
                popUpTo(Rota.PerfilCliente) { inclusive = true }
            }
        } else if (modo == Modo.CLIENTE && atual.hasRoute(Rota.PerfilProdutor::class)) {
            innerNav.navigate(Rota.PerfilCliente) {
                popUpTo(Rota.PerfilProdutor) { inclusive = true }
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clipToBounds()
                .nestedScroll(conexaoScroll),
        ) {
            val deslocamentoY by animateIntAsState(
                targetValue = if (mostrarFooter && footerVisivel) 0 else alturaFooterPx,
                label = "footerOffset",
            )

            CompositionLocalProvider(LocalAppFooterHeight provides espacoFooterOverlay) {
                NavHost(
                    innerNav,
                    startDestination = Rota.Home,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<Rota.Home> {
                        TelaHome(
                            onProdutoClick = { innerNav.navigate(Rota.Produto(it)) },
                            onCategoriaClick = { innerNav.navigate(Rota.Categoria(it)) },
                        )
                    }

                    composable<Rota.Explorar> {
                        ExplorarProduto(
                            uid = uid,
                            onNavigateToProduto = { innerNav.navigate(Rota.Produto(it)) },
                        )
                    }

                    composable<Rota.Categoria> { backStack ->
                        val args = backStack.toRoute<Rota.Categoria>()
                        TelaCategoria(
                            categoria = args.categoria,
                            uid = uid,
                            onBack = { innerNav.popBackStack() },
                            onNavigateToProduto = { innerNav.navigate(Rota.Produto(it)) },
                        )
                    }

                    composable<Rota.Produto> { backStack ->
                        val args = backStack.toRoute<Rota.Produto>()
                        val vm: TelaDoProdutoViewmodel = viewModel()
                        LaunchedEffect(args.produtoId) { vm.carregarProduto(args.produtoId) }
                        TelaDoProduto(
                            viewModel = vm,
                            onBackClick = { innerNav.popBackStack() },
                            onProdutorClick = { innerNav.navigate(Rota.ProdutorPublico(it)) },
                        )
                    }

                    composable<Rota.ProdutorPublico> { backStack ->
                        val args = backStack.toRoute<Rota.ProdutorPublico>()
                        PerfilProdutor(
                            lojaId = args.lojaId,
                            somenteLeitura = true,
                            onBack = { innerNav.popBackStack() },
                        )
                    }

                    composable<Rota.Carrinho> {
                        val vm: NewCarrinhoViewModel = viewModel()
                        LaunchedEffect(uid) { vm.carregarCarrinho(uid) }
                        NewCarrinho(
                            viewModel = vm,
                            uid = uid,
                            onFinalizarClick = { innerNav.navigate(Rota.FinalizarCompra) },
                        )
                    }

                    composable<Rota.FinalizarCompra> {
                        CheckoutPaymentScreen(navController = innerNav, uid = uid)
                    }

                    composable<Rota.CriarProduto> {
                        CriarProduto()
                    }

                    composable<Rota.PerfilCliente> {
                        HostPerfilCliente(
                            uid = uid,
                            rootNav = rootNav,
                            appVm = appVm,
                            onFooterVisibleChanged = { footerVisivelNoPerfilCliente = it },
                        )
                    }

                    composable<Rota.PerfilProdutor> {
                        HostPerfilProdutor(uid = uid, appVm = appVm)
                    }

                    composable<Rota.TelaVendas> {
                        // Vendas da loja do produtor logado. O produtorId é o
                        // lojaId (produtos nascem com produtorId = resolverLojaId(uid)),
                        // NÃO o uid — senão a query nunca casa.
                        var lojaId by remember { mutableStateOf<String?>(null) }
                        LaunchedEffect(uid) {
                            lojaId = runCatching { LojaRepository.resolverLojaId(uid) }.getOrNull()
                        }
                        lojaId?.let { id -> TelaVendas(produtorId = id) }
                    }
                }
            }

            if (mostrarFooter) {
                NavigationBar(
                    containerColor = bgDefault,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { alturaFooterPx = it.height }
                        .offset { IntOffset(0, deslocamentoY) },
                ) {
                    itens.forEach { item ->
                        val selecionado =
                            destino?.hierarchy?.any { it.hasRoute(item.rota::class) } == true
                        NavigationBarItem(
                            selected = selecionado,
                            onClick = {
                                innerNav.navigate(item.rota) {
                                    popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        if (selecionado) item.iconeAtivo else item.iconeInativo
                                    ),
                                    contentDescription = stringResource(item.titulo),
                                    tint = Color.Unspecified,
                                )
                            },
                            label = { Text(stringResource(item.titulo)) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            }
        }
    }
}
