package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.DirecaoSombra
import com.aracecultura.arace.ui.components.sombraInferior
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun SecaoFinalizarCompra(
    produtos: List<Produto>,
    onFinalizarClick: () -> Unit
) {
    // Calcula o valor total somando a propriedade 'preco' de cada produto.
    val valorTotal = produtos.sumOf { it.preco ?: 0.0 }

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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total",
                    fontFamily = GoogleSans,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    // Formata o valor para o padrão de moeda (ex: R$ 150,00)
                    text = String.format("R$ %.2f", valorTotal),
                    fontFamily = GoogleSans,
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
                    text = "Finalizar",
                    fontFamily = GoogleSans,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}