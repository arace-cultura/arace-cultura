package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import com.aracecultura.arace.ui.theme.GoogleSans
import java.util.Locale

// --- COMPONENTE EXTRAÍDO ---
@Composable
fun BotaoControle(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Color(0xFFE46D39))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ProdutoCardItem(
    produto: Produto?, // Null = Estado de carregamento
    quantidade: Int,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            // 1. CARREGAMENTO DA IMAGEM
            SubcomposeAsyncImage(
                model = produto?.imagens?.firstOrNull(),
                contentDescription = produto?.nome,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.35f)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
                val state = painter.state
                when (state) {
                    is AsyncImagePainter.State.Loading -> CarregamentoContainer(modifier = Modifier.fillMaxSize())
                    is AsyncImagePainter.State.Error -> CarregamentoContainer(modifier = Modifier.fillMaxSize())
                    else -> SubcomposeAsyncImageContent()
                }
            }

            // 2. DETALHES DO PRODUTO (Textos e Controles)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Se for null, já indica que está carregando os dados
                if (produto == null) {
                    Column {
                        CarregamentoContainer(modifier = Modifier.height(24.dp).fillMaxWidth(0.8f))
                        Spacer(modifier = Modifier.height(8.dp))
                        CarregamentoContainer(modifier = Modifier.height(20.dp).fillMaxWidth(0.5f))
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                        CarregamentoContainer(modifier = Modifier.size(24.dp))
                    }
                } else {
                    Column {
                        Text(
                            text = produto.nome.ifEmpty { "Produto" },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            fontFamily = GoogleSans,
                            color = Color.Black,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Valor: R$ ${String.format(Locale("pt", "BR"), "%.2f", produto.preco)}",
                            fontSize = 15.sp,
                            fontFamily = GoogleSans,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Quantidade:",
                            fontSize = 13.sp,
                            fontFamily = GoogleSans,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Uso do Botão Extraído (+)
                        BotaoControle(onClick = onIncreaseClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar quantidade",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = quantidade.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = GoogleSans,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Uso do Botão Extraído (-)
                        BotaoControle(onClick = onDecreaseClick) {
                            Icon(
                                painter = painterResource(R.drawable.ic_deletar),
                                contentDescription = "Diminuir quantidade",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(id = R.drawable.ic_deletar),
                            contentDescription = "Remover do Carrinho",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDeleteClick() },
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}