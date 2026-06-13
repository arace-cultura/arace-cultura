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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.painter.ColorPainter
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import java.util.Locale

private val CorBotaoControle = Color(0xFFE46D39)
private val CorPlaceholderImagem = Color(0xFFE9E5E1)

@Composable
fun ProdutoCardItem(
    produto: Produto?,
    quantidade: Int = 0,
    onIncreaseClick: () -> Unit = {},
    onDecreaseClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val imagemModifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.35f)
        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (produto == null) {
                CarregamentoContainer(modifier = imagemModifier)
            } else {
                // AsyncImage (sem subcomposição) no lugar de SubcomposeAsyncImage:
                // a subcomposição durante a medição da LazyColumn estava
                // envolvida na reescrita da posição de scroll a cada clique
                AsyncImage(
                    model = produto.imagens.firstOrNull(),
                    contentDescription = produto.nome,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(CorPlaceholderImagem),
                    error = ColorPainter(CorPlaceholderImagem),
                    modifier = imagemModifier
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (produto == null) {
                    EsqueletoProdutoCardItem()
                } else {
                    ConteudoProdutoCardItem(
                        produto = produto,
                        quantidade = quantidade,
                        onIncreaseClick = onIncreaseClick,
                        onDecreaseClick = onDecreaseClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EsqueletoProdutoCardItem() {
    Column {
        CarregamentoContainer(modifier = Modifier.height(24.dp).fillMaxWidth(0.8f))
        Spacer(modifier = Modifier.height(8.dp))
        CarregamentoContainer(modifier = Modifier.height(20.dp).fillMaxWidth(0.5f))
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
        CarregamentoContainer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ConteudoProdutoCardItem(
    produto: Produto,
    quantidade: Int,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val precoFormatado = remember(produto.preco) {
        String.format(Locale("pt", "BR"), "%.2f", produto.preco)
    }

    Column {
        Text(
            text = produto.nome.ifEmpty { "Produto" },
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color.Black,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Valor: R$ $precoFormatado",
            fontSize = 15.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Quantidade:",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }

    ControlesQuantidade(
        quantidade = quantidade,
        onIncreaseClick = onIncreaseClick,
        onDecreaseClick = onDecreaseClick,
        onDeleteClick = onDeleteClick
    )
}

@Composable
private fun ControlesQuantidade(
    quantidade: Int,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotaoControle(onClick = onIncreaseClick) {
            Icon(
                painter = painterResource(R.drawable.ic_adicionar),
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
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(16.dp))

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
            contentDescription = "Remover do carrinho",
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onDeleteClick),
            tint = Color.Black
        )
    }
}

@Composable
private fun BotaoControle(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(CorBotaoControle)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}