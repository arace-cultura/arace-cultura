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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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

@Composable
fun SecaoFinalizarCompra(
    produtos: List<ItemCarrinho>,
    onFinalizarClick: () -> Unit
) {
    if (produtos.isEmpty()) return

    // Multiplica o preço pela quantidade do carrinho
    val valorTotal = produtos.sumOf {
        (it.produto.preco) * it.quantidade
    }

    val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Box(
        modifier = Modifier
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
                .height(100.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.carrinho_total),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatoMoeda.format(valorTotal),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = onFinalizarClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = btColor
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.finalizar),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
