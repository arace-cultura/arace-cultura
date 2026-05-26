package com.aracecultura.arace.ui.components

import android.net.Uri
import androidx.compose.ui.text.font.Font
import com.aracecultura.arace.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@OptIn(ExperimentalTextApi::class)
val googleFont = FontFamily(
    Font(
        R.font.google_sans_flex_variable_font,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300))
    ),
    Font(
        R.font.google_sans_flex_variable_font,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.google_sans_flex_variable_font,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        R.font.google_sans_flex_variable_font,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    )
)

@Composable

fun ProdutoNavegar (
    produto: Produto
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Row(modifier =  Modifier
        .padding(top = 12.dp)
        .background(bgDefault)
        .padding(12.dp)) {
        AsyncImage(
            model = Uri.decode(produto.imagem),
            contentDescription = "Produto",
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
                fontFamily = googleFont,
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
            Image(
                painter = painterResource(id = R.drawable.estrela),
                contentDescription = null,
                modifier = Modifier
                    .width(screenWidth * 0.2f)
            )
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
                text ="Adicionar ao carrinho",
                fontSize = 16.sp,
                textColor = bgDefault,
                containerColor = btColor,
                borderColor = btColor,
                onClick = { println("Clicou em $screenWidth") },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(screenWidth * 0.5f)
                    .height(30.dp)
            )

        }
    }

}
