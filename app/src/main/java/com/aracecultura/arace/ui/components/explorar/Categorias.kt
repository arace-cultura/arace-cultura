package com.aracecultura.arace.ui.components.explorar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.ui.components.AdaptiveLine

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