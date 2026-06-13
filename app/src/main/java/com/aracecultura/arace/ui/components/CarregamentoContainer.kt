package com.aracecultura.arace.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CarregamentoContainer(
    modifier: Modifier = Modifier
) {

    val shimmerColors = listOf(
        Color(0xFFE9E5E1),
        Color(0xFFF4F2F0),
        Color(0xFFE9E5E1)
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")

    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerAnimation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim, y = translateAnim),
        end = Offset(x = translateAnim + 500f, y = translateAnim + 500f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    )
}