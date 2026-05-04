package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun AdaptiveLine (
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(looseConstraints) }

        var currentX = 0
        var currentY = 0
        var rowHeight = 0
        val placeablePositions = mutableListOf<Pair<Int, Int>>()

        placeables.forEach { placeable ->

            if (currentX + placeable.width > constraints.maxWidth) {
                currentX = 0
                currentY += rowHeight + verticalSpacing.roundToPx()
                rowHeight = 0
            }

            placeablePositions.add(Pair(currentX, currentY))

            rowHeight = max(rowHeight, placeable.height)

            currentX += placeable.width + horizontalSpacing.roundToPx()
        }

        layout(
            width = constraints.maxWidth,
            height = currentY + rowHeight
        ) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = placeablePositions[index]
                placeable.placeRelative(x = x, y = y)
            }
        }
    }
}