package com.aracecultura.arace.ui.main.jetpack.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object CalculadoraCategoria {
    fun calcularLargura(
        texto: String,
        multiplicadorLetra: Double = 11.0,
        margemBase: Double = 40.0): Double {
        return (texto.length * multiplicadorLetra) + margemBase
    }
}
@Composable
fun Categorias(
    categorias: List<String>,
    labelCategoria: @Composable (categoria: String) -> Unit,
) {
    AdaptiveLine(
        modifier = Modifier.fillMaxWidth(),
        horizontalSpacing = 8.dp,
        verticalSpacing = 8.dp
    ) {
        categorias.forEach { categoria ->
            labelCategoria(categoria)
        }
    }
}