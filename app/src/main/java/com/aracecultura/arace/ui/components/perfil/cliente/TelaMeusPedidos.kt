package com.aracecultura.arace.ui.components.perfil.cliente

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Envio
import com.aracecultura.arace.data.model.StatusEnvio
import com.aracecultura.arace.data.model.statusEnum
import com.aracecultura.arace.ui.components.sombraFormato
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.azulPrincipal
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.ui.theme.cinzaPrincipal
import com.aracecultura.arace.ui.theme.verdePrincipal

private val TextoEscuro = Color(0xFF050505)
private val TextoSecundario = Color(0xFF8A8A8A)
private val FormaCardPedido = RoundedCornerShape(16.dp)
private val FormaAbaQuantidade = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
private val AlturaCardPedido = 138.dp
private val AlturaCardPagamento = 166.dp
private val LarguraImagemPedido = 120.dp
private val LarguraAbaQuantidade = 52.dp

@Composable
fun TelaMeusPedidos(
    uid: String,
    onVoltar: () -> Unit = {},
    viewModel: MeusPedidosViewModel = viewModel(),
) {
    BackHandler(onBack = onVoltar)
    LaunchedEffect(uid) { viewModel.carregar(uid) }
    val pedidos by viewModel.pedidos.collectAsState()
    val nomesLojas by viewModel.nomesLojas.collectAsState()

    Column(Modifier.fillMaxSize().background(bgDefault)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onVoltar() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = stringResource(R.string.voltar),
                    tint = TextoEscuro,
                    modifier = Modifier.size(28.dp),
                )
            }

            Text(
                text = stringResource(R.string.meus_pedidos_titulo),
                fontFamily = GoogleSans,
                fontSize = 36.sp,
            )
        }

        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.img_bg_explorar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.25f),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(pedidos, key = { it.id }) { pedido ->
                    CartaoPedido(
                        envio = pedido,
                        nomeLoja = nomesLojas[pedido.produtorId].orEmpty(),
                        onDispensar = { viewModel.dispensarEntregue(pedido) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CartaoPedido(
    envio: Envio,
    nomeLoja: String,
    onDispensar: () -> Unit,
) {
    val status = envio.statusEnum
    // Entregue e cancelado são estados terminais: o cliente pode dispensá-los (×).
    val temDispensar = status == StatusEnvio.ENTREGUE || status == StatusEnvio.CANCELADO
    val cor = when (status) {
        StatusEnvio.PAGAMENTO -> btColor
        StatusEnvio.ENVIO -> azulPrincipal
        StatusEnvio.ENTREGUE -> verdePrincipal
        StatusEnvio.CANCELADO -> cinzaPrincipal
    }
    val rotuloEstado = stringResource(
        when (status) {
            StatusEnvio.PAGAMENTO -> R.string.pedido_estado_pagamento
            StatusEnvio.ENVIO -> R.string.pedido_estado_envio
            StatusEnvio.ENTREGUE -> R.string.pedido_estado_entregue
            StatusEnvio.CANCELADO -> R.string.pedido_estado_cancelado
        }
    )
    val alturaCard = when (status) {
        StatusEnvio.PAGAMENTO -> AlturaCardPagamento
        else -> AlturaCardPedido
    }
    val paddingFimConteudo = when (status) {
        StatusEnvio.PAGAMENTO -> 24.dp
        else -> 80.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(alturaCard)
            .sombraFormato(
                shape = FormaCardPedido,
                cor = Color.Black.copy(alpha = 0.14f),
                raioBlur = 10.dp,
                deslocamentoY = 4.dp,
            )
            .clip(FormaCardPedido)
            .background(bgDefault),
    ) {
        Box(Modifier.matchParentSize()) {
            if (envio.imagem.isNotBlank()) {
                AsyncImage(
                    model = envio.imagem,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(LarguraImagemPedido),
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            0.00f to bgDefault,
                            0.62f to bgDefault,
                            0.74f to bgDefault.copy(alpha = 0.93f),
                            0.87f to bgDefault.copy(alpha = 0.60f),
                            1.00f to bgDefault.copy(alpha = 0.04f),
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, top = 19.dp, end = paddingFimConteudo, bottom = 14.dp),
        ) {
            Text(
                text = envio.nome,
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Medium,
                fontSize = 27.sp,
                lineHeight = 31.sp,
                color = TextoEscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            LinhaPedido(
                rotulo = stringResource(R.string.pedido_de_prefixo),
                valor = nomeLoja.ifBlank { stringResource(R.string.pedido_loja_desconhecida) },
            )
            Spacer(Modifier.height(2.dp))
            LinhaPedido(
                rotulo = stringResource(R.string.pedido_estado_prefixo),
                valor = rotuloEstado,
                valorEmNovaLinha = status == StatusEnvio.PAGAMENTO,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp)
                .width(LarguraAbaQuantidade)
                .then(
                    if (temDispensar) {
                        Modifier.fillMaxHeight()
                    } else {
                        Modifier.height(98.dp)
                    }
                )
                .clip(FormaAbaQuantidade)
                .background(cor),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.pedido_qtd),
                    color = Color.White,
                    fontFamily = GoogleSans,
                    fontSize = 22.sp,
                    lineHeight = 24.sp,
                )
                Text(
                    text = envio.quantidade.toString(),
                    color = Color.White,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 31.sp,
                    lineHeight = 35.sp,
                    maxLines = 1,
                )
                if (temDispensar) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onDispensar() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_dispensar_pedido),
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinhaPedido(
    rotulo: String,
    valor: String,
    valorEmNovaLinha: Boolean = false,
) {
    if (valorEmNovaLinha) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = rotulo.trimEnd(),
                color = TextoSecundario,
                fontFamily = GoogleSans,
                fontSize = 20.sp,
                lineHeight = 22.sp,
            )
            Text(
                text = valor,
                color = TextoEscuro,
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = rotulo.trimEnd(),
            color = TextoSecundario,
            fontFamily = GoogleSans,
            fontSize = 20.sp,
            lineHeight = 22.sp,
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = valor,
            color = TextoEscuro,
            fontFamily = GoogleSans,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}
