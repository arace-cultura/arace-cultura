package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.util.Locale

@Composable
fun CheckoutPaymentScreen(
    navController: NavController,
    uid: String,
    viewModel: FinalizarCompraViewModel = viewModel(),
    backgroundRes: Int = R.drawable.img_bg_explorar
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uid) { viewModel.carregar(uid) }

    if (uiState is CheckoutUiState.Confirmacao) {
        CheckoutConfirmationScreen(
            onExit = { navController.popBackStack() },
            backgroundRes = backgroundRes
        )
        return
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color(0xFFFAF7F2),
            title = {
                Text(
                    stringResource(R.string.checkout_cancelar_titulo),
                    color = Color(0xFF2E2B27),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    stringResource(R.string.checkout_cancelar_msg),
                    color = Color(0xFF7A7168)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.checkout_sim_cancelar), color = Color(0xFFCE5A14), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.voltar), color = Color(0xFF7A7168))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(bgDefault)) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.25f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CheckoutTopBar(onBack = { navController.popBackStack() })

            Spacer(Modifier.height(16.dp))

            Row(Modifier.background(bgDefault).fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(
                    text = stringResource(R.string.checkout_titulo),
                    fontSize = 32.sp,
                    color = Color(0xFF2E2B27)
                )
            }


            Spacer(Modifier.height(24.dp))

            // Um cartão por loja: a compra é finalizada por produtor.
            when (val state = uiState) {
                CheckoutUiState.Carregando -> Text(
                    text = stringResource(R.string.carregando),
                    fontSize = 13.sp,
                    color = Color(0xFF7A7168),
                    modifier = Modifier.padding(32.dp)
                )
                CheckoutUiState.Vazio -> Text(
                    text = stringResource(R.string.checkout_vazio),
                    fontSize = 14.sp,
                    color = Color(0xFF7A7168),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
                is CheckoutUiState.Pagamento -> state.lojas.forEach { loja ->
                    LojaCheckoutCard(
                        loja = loja,
                        onCopiar = { clipboard.setText(AnnotatedString(loja.chavePix)) },
                        onFinalizar = { viewModel.finalizarLoja(uid, loja) }
                    )
                    Spacer(Modifier.height(20.dp))
                }
                CheckoutUiState.Confirmacao -> Unit
            }

            Spacer(Modifier.height(12.dp))

            AppButton(
                text = stringResource(R.string.cancelar),
                onClick = { showCancelDialog = true },
                containerColor = btColor,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(50.dp))
        }
    }
}

// Cartão de pagamento de UMA loja: tabela de itens, total da loja, chave Pix
// copiável (borda tracejada) e o botão que finaliza só o pagamento dela.
@Composable
private fun LojaCheckoutCard(
    loja: LojaCheckout,
    onCopiar: () -> Unit,
    onFinalizar: () -> Unit
) {
    var copied by remember(loja.produtorId) { mutableStateOf(false) }
    val laranja = Color(0xFFCE5A14)
    val escuro = Color(0xFF2E2B27)
    val cinza = Color(0xFF7A7168)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFAF7F2))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loja.nomeLoja.isNotBlank()) {
            Text(loja.nomeLoja, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = cinza)
            Spacer(Modifier.height(4.dp))
        }
        Text(
            text = stringResource(R.string.checkout_apos_pagar),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = escuro,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Cabeçalho da tabela
        Row(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.checkout_col_produto), Modifier.weight(1.3f), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = escuro)
            Text(stringResource(R.string.checkout_col_quantidade), Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = escuro, textAlign = TextAlign.Center)
            Text(stringResource(R.string.checkout_col_preco), Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = escuro, textAlign = TextAlign.End)
        }
        Spacer(Modifier.height(8.dp))
        loja.itens.forEach { linha ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(
                    text = linha.nome,
                    modifier = Modifier.weight(1.3f),
                    fontSize = 16.sp,
                    color = escuro,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(linha.quantidade.toString(), Modifier.weight(1f), fontSize = 16.sp, color = escuro, textAlign = TextAlign.Center)
                Text(formatarValor(linha.totalLinha), Modifier.weight(1f), fontSize = 16.sp, color = escuro, textAlign = TextAlign.End)
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = laranja, thickness = 2.dp)
        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.checkout_total, formatarValor(loja.total)),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = laranja
        )

        Spacer(Modifier.height(16.dp))

        if (loja.chavePix.isBlank()) {
            Text(
                text = stringResource(R.string.checkout_sem_pix),
                fontSize = 12.sp,
                color = cinza,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = stringResource(R.string.checkout_copie_pix),
                fontSize = 12.sp,
                color = cinza,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .dashedBorder(color = laranja, cornerRadius = 8.dp)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loja.chavePix,
                    fontSize = 13.sp,
                    color = escuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = stringResource(R.string.cd_copiar),
                    tint = laranja,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onCopiar(); copied = true }
                )
            }
            if (copied) {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.checkout_codigo_copiado), fontSize = 12.sp, color = laranja)
            }
        }

        Spacer(Modifier.height(16.dp))

        AppButton(
            text = stringResource(R.string.checkout_finalizar_loja),
            onClick = onFinalizar,
            containerColor = btColor,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            fontSize = 15.sp
        )
    }
}

// Borda tracejada arredondada (o desenho do cartão de Pix).
private fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.5.dp,
    tracinho: Dp = 8.dp,
    espaco: Dp = 6.dp
) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(tracinho.toPx(), espaco.toPx()), 0f
            )
        ),
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}

// Valor em pt-BR sem o símbolo R$ (ex.: 1.234,50), como no desenho.
private fun formatarValor(valor: Double): String =
    String.format(Locale("pt", "BR"), "%,.2f", valor)

@Composable
private fun CheckoutConfirmationScreen(
    onExit: () -> Unit,
    backgroundRes: Int = R.drawable.img_bg_explorar
) {
    Box(modifier = Modifier.fillMaxSize().background(bgDefault)) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.25f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CheckoutTopBar(onBack = onExit)

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth().background(bgDefault), horizontalArrangement = Arrangement.Center){
                Text(
                    text = stringResource(R.string.checkout_confirmacao),
                    fontSize = 36.sp,
                    color = Color(0xFF2E2B27)
                )
            }


            Spacer(Modifier.height(120.dp))

            Box(Modifier.clip(CircleShape).background(bgDefault).padding(50.dp)){
                Icon(
                    painter = painterResource(id = R.drawable.ic_bag_check),
                    contentDescription = stringResource(R.string.cd_compra_confirmada),
                    tint = Color(0xFF2E2B27),
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Row(Modifier.fillMaxWidth().background(bgDefault), horizontalArrangement = Arrangement.Center){
                Text(
                    text = stringResource(R.string.checkout_obrigado),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E2B27)
                )
            }

            Spacer(Modifier.height(120.dp))

            AppButton(
                text = stringResource(R.string.checkout_continue_comprando),
                onClick = onExit,
                containerColor = btColor,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CheckoutTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.voltar),
            tint = Color(0xFF2E2B27),
            modifier = Modifier
                .size(24.dp)
                .clickable { onBack() }
        )
    }
}

