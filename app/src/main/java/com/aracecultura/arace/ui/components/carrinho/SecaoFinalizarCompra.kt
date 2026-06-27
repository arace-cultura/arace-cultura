package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.ItemCarrinho
import com.aracecultura.arace.ui.components.DirecaoSombra
import com.aracecultura.arace.ui.components.sombraInferior
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.text.NumberFormat
import java.util.Locale

internal val AlturaSecaoFinalizarCompra = 100.dp

@Composable
fun SecaoFinalizarCompra(
    produtos: List<ItemCarrinho>,
    onFinalizarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (produtos.isEmpty()) return

    // Multiplica o preço pela quantidade do carrinho
    val valorTotal = produtos.sumOf {
        (it.produto.preco) * it.quantidade
    }

    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Box(
        modifier = modifier
            .zIndex(0.5f)
            .fillMaxWidth()
            .background(bgDefault)
            .sombraInferior(DirecaoSombra.CIMA, alturaSombra = 15.dp, corSombra = Color.Black.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Altura fixa: o total mudando não pode redimensionar a barra,
                // senão a viewport da lista acima oscila e força remedições
                .height(AlturaSecaoFinalizarCompra)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.carrinho_total),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatoMoeda.format(valorTotal),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onFinalizarClick,
                modifier = Modifier
                    .width(112.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btColor
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.finalizar),
                    fontSize = 18.sp,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}
