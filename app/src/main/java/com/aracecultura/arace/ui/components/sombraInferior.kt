import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.sombraInferior(
    alturaSombra: Dp = 20.dp, // Você pode aumentar ou diminuir o tamanho aqui
    corSombra: Color = Color.Black.copy(alpha = 0.15f) // Opacidade da sombra
) = this.drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(corSombra, Color.Transparent),
            startY = size.height,
            endY = size.height + alturaSombra.toPx()
        ),
        topLeft = Offset(x = 0f, y = size.height),
        size = Size(width = size.width, height = alturaSombra.toPx())
    )
}