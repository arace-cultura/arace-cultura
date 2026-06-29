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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.CarregamentoContainer
import java.util.Locale

private val CorBotaoControle = Color(0xFFE46D39)

@Composable
fun ProdutoCardItem(
    produto: Produto?,
    quantidade: Int = 0,
    esgotado: Boolean = false,
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
            val imagem = produto?.imagens?.firstOrNull()?.takeIf { it.isNotBlank() }
            if (produto == null || imagem == null) {
                CarregamentoContainer(modifier = imagemModifier, shape = RectangleShape)
            } else {
                SubcomposeAsyncImage(
                    model = imagem,
                    contentDescription = produto.nome,
                    contentScale = ContentScale.Crop,
                    modifier = imagemModifier,
                    loading = {
                        CarregamentoContainer(modifier = Modifier.fillMaxSize(), shape = RectangleShape)
                    },
                    error = {
                        CarregamentoContainer(modifier = Modifier.fillMaxSize(), shape = RectangleShape)
                    }
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
                        esgotado = esgotado,
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
    esgotado: Boolean,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val precoFormatado = remember(produto.preco) {
        String.format(Locale("pt", "BR"), "%.2f", produto.preco)
    }

    Column {
        Text(
            text = produto.nome.ifEmpty { stringResource(R.string.cd_produto) },
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color.Black,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.carrinho_valor, precoFormatado),
            fontSize = 15.sp,
            color = Color.Black
        )
        if (!esgotado) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.carrinho_quantidade),
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }

    ControlesQuantidade(
        quantidade = quantidade,
        esgotado = esgotado,
        onIncreaseClick = onIncreaseClick,
        onDecreaseClick = onDecreaseClick,
        onDeleteClick = onDeleteClick
    )
}

@Composable
private fun ControlesQuantidade(
    quantidade: Int,
    esgotado: Boolean,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (esgotado) {
            // Estoque zerado: no lugar dos controles de quantidade, só o aviso.
            // A lixeira (à direita) é preservada para o cliente poder remover.
            Text(
                text = stringResource(R.string.esgotado),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        } else {
            BotaoControle(onClick = onIncreaseClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_adicionar),
                    contentDescription = stringResource(R.string.cd_aumentar_qtd),
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
                    painter = painterResource(R.drawable.ic_menos),
                    contentDescription = stringResource(R.string.cd_diminuir_qtd),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_deletar),
            contentDescription = stringResource(R.string.cd_remover_carrinho),
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
