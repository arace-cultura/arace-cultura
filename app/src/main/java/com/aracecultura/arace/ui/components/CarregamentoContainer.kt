package com.aracecultura.arace.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
    modifier: Modifier
){
    val transition = rememberInfiniteTransition()
    val shimmerX by transition.animateFloat(
        initialValue = -800f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF9F9F9F),
            Color(0xFFB6B6B6),
            Color(0xFF9F9F9F),
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 800f, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(brush)
    )
}