package com.aracecultura.arace.ui.components.explorar

import android.net.Uri
import com.aracecultura.arace.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.components.produto.Avaliacao
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor


@Composable

fun ProdutoNavegar (
    produto: Produto,
    onProdutoClick: () -> Unit = {},
    onAddToCartClick: () -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(modifier =  Modifier
        .padding(bottom = 12.dp)
        .background(bgDefault)
        .clickable { onProdutoClick() }
        .padding(12.dp)) {
        SubcomposeAsyncImage(
            model = produto.imagens.firstOrNull()?.let(Uri::decode),
            contentDescription = stringResource(R.string.cd_produto),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(screenWidth * 0.45f)
                .weight(1f)
                .padding(end = 12.dp),
            loading = {
                CarregamentoContainer(modifier = Modifier.fillMaxSize(), shape = RectangleShape)
            },
            error = {
                CarregamentoContainer(modifier = Modifier.fillMaxSize(), shape = RectangleShape)
            }
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (produto.nome.length > 20)
                    produto.nome.take(17) + "..." else produto.nome,
                fontSize = 18.sp,
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
                Text(stringResource(R.string.prefixo_real), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${produto.preco}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            val esgotado = produto.quantidade <= 0
            AppButton(
                text = if (esgotado) stringResource(R.string.esgotado)
                else stringResource(R.string.adicionar_ao_carrinho),
                fontSize = 16.sp,
                textColor = if (esgotado) Color.Black else bgDefault,
                containerColor = if (esgotado) Color.LightGray else btColor,
                borderColor = if (esgotado) Color.LightGray else btColor,
                onClick = { if (!esgotado) onAddToCartClick() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(screenWidth * 0.5f)
                    .height(35.dp)
            )

        }
    }

}

// Skeleton exibido enquanto os produtos não chegam do Firestore.
// Espelha o layout de ProdutoNavegar.
@Composable
fun ProdutoNavegarSkeleton() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .background(bgDefault)
            .padding(12.dp)
    ) {
        CarregamentoContainer(
            modifier = Modifier
                .height(screenWidth * 0.45f)
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            CarregamentoContainer(
                modifier = Modifier
                    .height(22.dp)
                    .width(screenWidth * 0.3f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            CarregamentoContainer(
                modifier = Modifier
                    .height(48.dp)
                    .width(screenWidth * 0.42f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            CarregamentoContainer(
                modifier = Modifier
                    .height(18.dp)
                    .width(screenWidth * 0.25f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            CarregamentoContainer(
                modifier = Modifier
                    .height(30.dp)
                    .width(screenWidth * 0.5f)
            )
        }
    }
}
