package com.aracecultura.arace.ui.components.explorar

import android.net.Uri
import androidx.compose.ui.text.font.Font
import com.aracecultura.arace.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.components.produto.Avaliacao
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor


@Composable

fun ProdutoNavegar (
    produto: Produto,
    onProdutoClick: () -> Unit = {},      // Recebe o clique na linha
    onAddToCartClick: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(modifier =  Modifier
        .padding(bottom = 12.dp)
        .background(bgDefault)
        .clickable { onProdutoClick() }
        .padding(12.dp)) {
        AsyncImage(
            model = produto.imagens.firstOrNull()?.let(Uri::decode),
            contentDescription = "Produto",
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            modifier = Modifier
                .height(screenWidth * 0.45f)
                .weight(1f)
                .padding(end = 12.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (produto.nome.length > 20)
                    produto.nome.take(17) + "..." else produto.nome,
                fontSize = 18.sp,
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(bottom = 6.dp)
            )
            Text(
                text = if (produto.descricao.length > 60)
                    produto.descricao.take(57) + "..." else produto.descricao,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Avaliacao(produto.avaliacao)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top){
                Text("R$ ", fontSize = 12.sp)
                Text(
                    text = "${produto.preco}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            AppButton(
                text = "Adicionar ao carrinho",
                fontSize = 16.sp,
                textColor = bgDefault,
                containerColor = btColor,
                borderColor = btColor,
                onClick = { onAddToCartClick() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(screenWidth * 0.5f)
                    .height(30.dp)
            )

        }
    }

}