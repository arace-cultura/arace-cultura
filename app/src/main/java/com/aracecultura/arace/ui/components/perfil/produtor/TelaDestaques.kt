package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.sombraFormato
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.util.Locale

// Bordas internas (imagem) e externas (card) de 30.dp.
private val RaioBorda = 30.dp
private val CorCard = Color(0xFFFCFBFA)
private val CorRemover = Color(0xFFF4F2F0)

@Composable
fun TelaDestaques(
    uid: String,
    onBack: () -> Unit = {},
    viewModel: TelaDestaquesViewModel = viewModel(),
) {
    val itens by viewModel.itens.collectAsState()
    val carregando by viewModel.carregando.collectAsState()

    LaunchedEffect(uid) { viewModel.carregar(uid) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        Image(
            painter = painterResource(R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.18f)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(bgDefault),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.voltar),
                        tint = Color.Black
                    )
                }
                Text(
                    text = stringResource(R.string.destaques_titulo),
                    fontFamily = GoogleSans,
                    fontSize = 36.sp
                )
            }

            when {
                carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.carregando), color = Color.Gray)
                }
                itens.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.editar_produtos_vazio),
                        color = Color.Gray,
                        modifier = Modifier.padding(24.dp)
                    )
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = itens, key = { it.produto.id }) { item ->
                        CardDestaque(
                            item = item,
                            onAlternar = { viewModel.alternarDestaque(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardDestaque(item: ItemDestaque, onAlternar: () -> Unit) {
    val formaCard = RoundedCornerShape(RaioBorda)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sombraFormato(
                shape = formaCard,
                cor = Color.Black.copy(alpha = 0.12f),
                raioBlur = 10.dp,
                deslocamentoY = 4.dp
            )
            .clip(formaCard)
            .background(CorCard)
    ) {
        Row(
            modifier = Modifier.height(150.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = RaioBorda,
                            bottomStart = RaioBorda,
                            topEnd = RaioBorda,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(Color(0xFFD9D9D9))
            ) {
                item.produto.imagens.firstOrNull()?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = item.produto.nome,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = item.produto.nome,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F1B18),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.destaques_valor,
                        formatarReais(item.produto.preco)
                    ),
                    fontSize = 18.sp,
                    color = Color(0xFF7A7168)
                )
                Spacer(Modifier.height(12.dp))
                BotaoDestaque(emDestaque = item.emDestaque, onClick = onAlternar)
            }
        }
    }
}

@Composable
private fun BotaoDestaque(emDestaque: Boolean, onClick: () -> Unit) {
    val cor = if (emDestaque) CorRemover else btColor
    val texto = stringResource(
        if (emDestaque) R.string.destaques_remover else R.string.destaques_destacar
    )
    val corTexto = if (emDestaque) Color(0xFF3B4045) else Color.White

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(RaioBorda))
            .background(cor)
            .clickable(onClick = onClick)
            .padding(horizontal = 40.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = corTexto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatarReais(valor: Double): String =
    "R$" + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor)
