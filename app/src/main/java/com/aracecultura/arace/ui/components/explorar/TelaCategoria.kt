package com.aracecultura.arace.ui.components.explorar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.verdePrincipal

/**
 * Tela de uma categoria: reusa toda a infraestrutura de Explorar
 * ([ConteudoExplorar]) com a categoria já fixada no viewModel e sem a seção
 * de Categorias no painel de filtros. Cada categoria da HomePage abre esta
 * mesma tela passando o próprio nome.
 */
@Composable
fun TelaCategoria(
    categoria: String,
    uid: String,
    onBack: () -> Unit = {},
    onNavigateToProduto: (String) -> Unit = {},
    viewmodel: ExplorarProdutoViewmodel = viewModel()
) {
    LaunchedEffect(categoria) {
        viewmodel.fixarCategoria(categoria)
    }

    ConteudoExplorar(
        viewmodel = viewmodel,
        uid = uid,
        onNavigateToProduto = onNavigateToProduto,
        mostrarFiltroCategorias = false,
        textoBotaoFiltros = "Ordenar por",
        header = {
            BannerCategoria(categoria = categoria, onBack = onBack)
        }
    )
}

@Composable
private fun BannerCategoria(
    categoria: String,
    onBack: () -> Unit
) {
    // TODO(cores): banner verde provisório — trocar quando a paleta final
    // de categorias for definida
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(verdePrincipal)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Voltar",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(28.dp)
                .clickable { onBack() }
        )

        Text(
            text = categoria,
            fontFamily = GoogleSans,
            fontWeight = FontWeight.ExtraLight,
            fontSize = 40.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
