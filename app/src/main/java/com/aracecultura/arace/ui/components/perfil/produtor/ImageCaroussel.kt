package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.absoluteValue

@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum produto em destaque",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val pagerState = rememberPagerState { imageUrls.size }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 40.dp)
        ) { page ->
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .absoluteValue
                .coerceIn(0f, 1f)

            Column {
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = "Imagem do produto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 10f)
                        .graphicsLayer {
                            val scale = 0.85f + (1f - pageOffset) * 0.15f
                            scaleX = scale
                            scaleY = scale
                            alpha = 0.6f + (1f - pageOffset) * 0.4f
                        }
                )
            }
        }
    }
}
