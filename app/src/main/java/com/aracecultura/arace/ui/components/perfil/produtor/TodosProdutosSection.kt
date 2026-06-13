package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.data.model.Produto
import java.util.Locale

@Composable
fun TodosProdutosSection(
    brandColor: Color,
    produtos: List<Produto>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Todos produtos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Ordenar por: nome",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }

        if (produtos.isEmpty()) {
            Text(
                text = "Nenhum produto cadastrado ainda.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            return@Column
        }

        val pages = remember(produtos) { produtos.sortedBy { it.nome }.chunked(4) }
        val pagerState = rememberPagerState(pageCount = { pages.size })

        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->
                val pageItems = pages[pageIndex]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    pageItems.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (produto in rowItems) {
                                val precoFormatado = String.format(Locale("pt", "BR"), "%.2f", produto.preco)
                                ProdutoCard(
                                    nome = produto.nome,
                                    preco = "R$ $precoFormatado",
                                    imageUrl = produto.imagens.firstOrNull(),
                                    brandColor = brandColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) brandColor
                                else brandColor.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}
