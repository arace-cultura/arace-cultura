package com.aracecultura.arace.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Enum para controlar a direção de onde a sombra será projetada
enum class DirecaoSombra {
    CIMA, BAIXO
}

fun Modifier.sombraInferior(
    direcao: DirecaoSombra = DirecaoSombra.BAIXO,
    alturaSombra: Dp = 20.dp,
    corSombra: Color = Color.Black.copy(alpha = 0.15f)
) = this.drawBehind {
    val alturaPx = alturaSombra.toPx()

    // Inverte a física do desenho baseado na direção escolhida
    val yTopLeft = if (direcao == DirecaoSombra.CIMA) -alturaPx else size.height
    val startY = if (direcao == DirecaoSombra.CIMA) -alturaPx else size.height
    val endY = if (direcao == DirecaoSombra.CIMA) 0f else size.height + alturaPx

    val cores = if (direcao == DirecaoSombra.CIMA) {
        listOf(Color.Transparent, corSombra) // Esmaece conforme sobe
    } else {
        listOf(corSombra, Color.Transparent) // Esmaece conforme desce
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = cores,
            startY = startY,
            endY = endY
        ),
        topLeft = Offset(x = 0f, y = yTopLeft),
        size = Size(width = size.width, height = alturaPx)
    )
}