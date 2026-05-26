package com.aracecultura.arace.ui.components

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.components.Categorias
import com.aracecultura.arace.ui.components.OrderBy
import com.aracecultura.arace.ui.components.ProdutoNavegar
import androidx.compose.foundation.lazy.items
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto

@Composable
fun ExplorarProduto(
    viewmodel: ExplorarProdutoViewmodel = viewModel()
) {
    val libreCaslonDisplay = remember{FontFamily(Font(R.font.librecaslondisplay_regular))}
    val categoriasList = remember { listOf("Têxteis", "Cerâmica", "Entalhe")}

    var textPesquisarMu by remember { mutableStateOf("") }
    var mostrarCategorias by remember{ mutableStateOf(false) }
    val produtos: State<List<Produto>> = viewmodel.produtos.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)


        ) {
            Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                AppButton(
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp),
                    text = "Filtros",
                    textColor = bgDefault,
                    containerColor = btColor,
                    borderColor = btColor,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 30.dp,
                        bottomStart = 0.dp
                    ),
                    onClick = { mostrarCategorias = !mostrarCategorias }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(50.dp)
                            .background(bgDefault)

                    ) {
                        Text(
                            "Descubra",
                            Modifier.width(200.dp),
                            fontFamily = libreCaslonDisplay,
                            fontSize = 40.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    LazyColumn() {
                        items(
                            items = produtos.value,
                            key = { produto -> produto.id }
                        ) { produto ->
                            ProdutoNavegar(produto)

                        }
                    }
                }
            }
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = mostrarCategorias,
            enter =  fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { mostrarCategorias = false }
            )
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = mostrarCategorias,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Column(modifier = Modifier.background(bgDefault).padding(bottom = 16.dp)) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ordenar por",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .width(130.dp)
                            .padding(start = 8.dp, end = 8.dp)
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp).width(3.dp).background(btColor)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        OrderBy(Color.Black)
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
                    Spacer(
                        modifier = Modifier.height(20.dp).width(3.dp).background(btColor)
                    )
                    SearchBar(
                        modifier = Modifier.weight(1f),
                        text = textPesquisarMu,
                        textColor = Color.Black,
                        onTextChange = { textPesquisarMu = it },
                        containerColor = bgDefault,
                        placeholder = "Pesquise um município"
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Categorias:",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Categorias(
                    categorias = categoriasList,
                ) { categoria ->
                    AppButton(
                        text = categoria,
                        textColor = btColor,
                        containerColor = bgDefault,
                        borderColor = btColor,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(40.dp),
                        onClick = { println("Clicou em $categoria") }
                    )
                }
            }
        }
    }
}
