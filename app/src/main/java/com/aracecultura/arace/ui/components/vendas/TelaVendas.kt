package com.aracecultura.arace.ui.components.vendas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.azulPrincipal
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.ui.theme.verdePrincipal
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Tela de Vendas do produtor: lê em tempo real as vendas da loja (via
 * [VendasViewModel]) e exibe um cartão por status.
 *
 * @param produtorId id da loja dona destas vendas.
 */
@Composable
fun TelaVendas(
    produtorId: String,
    viewModel: VendasViewModel = viewModel(),
) {
    LaunchedEffect(produtorId) { viewModel.carregar(produtorId) }
    val vendas by viewModel.vendas.collectAsState()

    Column(Modifier.fillMaxSize().background(bgDefault)) {
        BarraVendas()

        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.img_bg_explorar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.25f),
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 96.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(vendas, key = { it.id }) { envio ->
                    when (envio.statusEnum) {
                        StatusEnvio.PAGAMENTO -> CartaoPagamento(
                            envio = envio,
                            onConfirmar = { viewModel.confirmarPagamento(envio) },
                        )
                        StatusEnvio.ENVIO -> CartaoEnvio(
                            envio = envio,
                            onCancelar = { viewModel.cancelarEnvio(envio) },
                            onConfirmarEntrega = { viewModel.confirmarEntrega(envio) },
                        )
                        StatusEnvio.ENTREGUE -> CartaoEntregue(
                            envio = envio,
                            onRemover = { viewModel.removerEntregue(envio) },
                        )
                        // Cancelados são filtrados no VM; não aparecem para o produtor.
                        StatusEnvio.CANCELADO -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraVendas() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(bgDefault),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.vendas_titulo),
                fontFamily = GoogleSans,
                fontSize = 36.sp
            )
        }
    }
}

@Composable
private fun CartaoPagamento(envio: Envio, onConfirmar: () -> Unit) {
    CartaoBase(envio = envio, cor = btColor) {
        Text(
            text = stringResource(R.string.vendas_pagamento_pendente),
            fontFamily = GoogleSans,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )
        ContatoCliente(nomeCliente = envio.nomeComprador, telefone = envio.telefoneComprador)
        BotaoVenda(texto = stringResource(R.string.vendas_confirmar), icone = Icons.Filled.Check, cor = btColor, onClick = onConfirmar)
    }
}

@Composable
private fun CartaoEnvio(
    envio: Envio,
    onCancelar: () -> Unit,
    onConfirmarEntrega: () -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }

    CartaoBase(envio = envio, cor = azulPrincipal) {
        Text(
            text = stringResource(R.string.vendas_em_envio),
            fontFamily = GoogleSans,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandido = !expandido }
                .padding(vertical = 8.dp),
        )

        AnimatedVisibility(
            visible = expandido,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            ) + fadeOut(animationSpec = tween(durationMillis = 140)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ContatoCliente(nomeCliente = envio.nomeComprador, telefone = envio.telefoneComprador)
                Spacer(Modifier.height(5.dp))
                BotaoVenda(texto = stringResource(R.string.vendas_cancelar_envio), icone = Icons.Filled.Close, cor = azulPrincipal, onClick = onCancelar)
                Spacer(Modifier.height(15.dp))
                BotaoVenda(texto = stringResource(R.string.vendas_confirmar_entrega), icone = Icons.Filled.LocationOn, cor = azulPrincipal, onClick = onConfirmarEntrega)
                Spacer(Modifier.height(12.dp))
            }
        }

        Icon(
            imageVector = if (expandido) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = stringResource(
                if (expandido) R.string.cd_recolher else R.string.cd_expandir
            ),
            tint = azulPrincipal,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandido = !expandido },
        )
    }
}

@Composable
private fun CartaoEntregue(envio: Envio, onRemover: () -> Unit) {
    CartaoBase(
        envio = envio,
        cor = verdePrincipal,
        acaoTopo = {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onRemover() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.cd_remover_venda),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
    ) {
        Text(
            text = stringResource(R.string.vendas_entregue),
            fontFamily = GoogleSans,
            fontSize = 17.sp,
            color = verdePrincipal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
        }
    }

@Composable
private fun CartaoBase(
    envio: Envio,
    cor: Color,
    acaoTopo: (@Composable BoxScope.() -> Unit)? = null,
    conteudoInferior: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(cor),
        ) {
            if (envio.imagem.isNotBlank()) {
                AsyncImage(
                    model = envio.imagem,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize().alpha(0.15f),
                )
            }
            Column(Modifier.padding(start = 16.dp, top = 16.dp, end = 42.dp, bottom = 16.dp)) {
                Text(
                    text = envio.nome,
                    color = Color.White,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.vendas_produto_id,
                        codigoProdutoVenda(envio.produtoId)
                    ),
                    color = Color.White,
                    fontFamily = GoogleSans,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(16.dp))
                PrecoVenda(envio.preco)
            }
            acaoTopo?.invoke(this)
        }

        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            conteudoInferior()
        }
    }
}

/**
 * Bloco "para contatar Cliente, ligue para <telefone>": identifica de quem é a
 * compra e dá ao produtor o número do cliente (copiado para o Envio no checkout).
 */
@Composable
private fun ContatoCliente(nomeCliente: String, telefone: String) {
    val nomeExibido = nomeCliente.ifBlank { stringResource(R.string.vendas_contatar_cliente) }
    val chamada = buildAnnotatedString {
        append(stringResource(R.string.vendas_contatar_prefixo))
        append(' ')
        withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
            append(nomeExibido)
        }
        append(stringResource(R.string.vendas_contatar_sufixo))
    }
    Column(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = chamada,
            fontFamily = GoogleSans,
            fontSize = 17.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = telefone.ifBlank { stringResource(R.string.vendas_telefone_indisponivel) },
            fontFamily = GoogleSans,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun codigoProdutoVenda(produtoId: String): String {
    val id = produtoId.trim()
    if (id.isBlank()) return "-"
    if (id.startsWith("PRD-", ignoreCase = true) && id.length <= 12) {
        return id.uppercase(Locale.ROOT)
    }

    val sufixo = id.substringAfterLast('-')
    val codigo = when {
        sufixo != id && sufixo.length in 6..12 && sufixo.all(Char::isLetterOrDigit) -> sufixo
        else -> id.filter(Char::isLetterOrDigit).take(8)
    }

    return codigo.ifBlank { id.take(8) }.uppercase(Locale.ROOT)
}

@Composable
private fun PrecoVenda(preco: Double) {
    val reais = preco.toInt()
    val centavos = ((preco - reais) * 100).roundToInt().toString().padStart(2, '0')
    Row(verticalAlignment = Alignment.Top) {
        Text(stringResource(R.string.vendas_moeda_real), color = Color.White, fontFamily = GoogleSans, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.size(4.dp))
        Text("$reais", color = Color.White, fontFamily = GoogleSans, fontSize = 26.sp, fontWeight = FontWeight.Medium)
        Text(centavos, color = Color.White, fontFamily = GoogleSans, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BotaoVenda(
    texto: String,
    icone: ImageVector,
    cor: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(cor)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(36.dp).border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icone, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Text(texto, color = Color.White, fontFamily = GoogleSans, fontSize = 17.sp)
    }
}
