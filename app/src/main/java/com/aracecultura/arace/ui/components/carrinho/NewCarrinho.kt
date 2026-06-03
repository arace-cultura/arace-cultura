package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault

@Composable
fun NewCarrinho(
    viewModel: NewCarrinhoViewModel = viewModel(),
    uid: String,
    onDeleteClick: (Produto) -> Unit = {}
) {
    val produtos: State<List<Produto>> = viewModel.produtos.collectAsState()

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.getCartProducts(uid)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgDefault)) {

        Image(
            painter = painterResource(id = R.drawable.bg_explorar),
            contentDescription = "Background Topográfico",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.3f
        )

        Scaffold(
            containerColor = Color.Transparent,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Text(
                    text = "Carrinho",
                    fontFamily = GoogleSans,
                    fontSize = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgDefault)
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )

                Ordenar()

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = produtos.value,
                        key = { it.id }
                    ) { produto ->
                        ProdutoCardItem(
                            produto = produto,
                            onDeleteClick = {
                                viewModel.removerProduto(produto, uid)
                                onDeleteClick(produto)
                            }
                        )
                    }
                }
            }
        }
    }
}