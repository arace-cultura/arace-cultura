package com.aracecultura.arace.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
        listOf(Color.Transparent, corSombra)
    } else {
        listOf(corSombra, Color.Transparent)
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

// Sombra suave que segue o contorno real de uma Shape (inclusive paths customizados),
// ao contrário de sombraInferior que desenha sempre um retângulo.
fun Modifier.sombraFormato(
    shape: Shape,
    cor: Color = Color.Black.copy(alpha = 0.18f),
    raioBlur: Dp = 8.dp,
    deslocamentoX: Dp = 0.dp,
    deslocamentoY: Dp = 0.dp,
) = this.drawBehind {
    if (size.minDimension <= 0f) return@drawBehind

    val caminho = Path().apply {
        when (val outline = shape.createOutline(size, layoutDirection, this@drawBehind)) {
            is Outline.Generic -> addPath(outline.path)
            is Outline.Rounded -> addRoundRect(outline.roundRect)
            is Outline.Rectangle -> addRect(outline.rect)
        }
        translate(Offset(deslocamentoX.toPx(), deslocamentoY.toPx()))
    }

    drawIntoCanvas { canvas ->
        val paint = Paint().apply { color = cor }
        paint.asFrameworkPaint().maskFilter =
            BlurMaskFilter(raioBlur.toPx(), BlurMaskFilter.Blur.NORMAL)
        canvas.drawPath(caminho, paint)
    }
}