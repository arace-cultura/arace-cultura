package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import java.text.NumberFormat
import java.util.Locale

object CheckoutRoutes {
    const val PAYMENT      = "checkout_payment"
    const val CONFIRMATION = "checkout_confirmation"
}

@Composable
fun CheckoutPaymentScreen(
    navController: NavController,
    uid: String,
    viewModel: NewCarrinhoViewModel = viewModel(),
    backgroundRes: Int = R.drawable.img_bg_explorar
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val formatoMoeda = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) viewModel.carregarCarrinho(uid)
    }

    val itens = (estado as? EstadoCarrinho.Pronto)?.itens.orEmpty()

    // Um Pix por produtor: agrupa os itens por produtor e soma o subtotal que
    // cada um deve receber (chave estática → o comprador digita o valor).
    val gruposPix = remember(itens) {
        itens.groupBy { it.produto.produtorId }
            .map { (_, itensDoProdutor) ->
                GrupoPix(
                    chavePix = itensDoProdutor
                        .firstOrNull { it.produto.chavePix.isNotBlank() }
                        ?.produto?.chavePix.orEmpty(),
                    subtotal = itensDoProdutor.sumOf { it.produto.preco * it.quantidade }
                )
            }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color(0xFFFAF7F2),
            title = {
                Text(
                    "Cancelar pedido?",
                    color = Color(0xFF2E2B27),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Tem certeza que deseja cancelar esta compra?",
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
                    Text("Sim, cancelar", color = Color(0xFFCE5A14), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Voltar", color = Color(0xFF7A7168))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(bgDefault)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CheckoutTopBar(onBack = { navController.popBackStack() })

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Finalizar Compra",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2B27)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Aguardando pagamento",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E2B27)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Copie a chave Pix de cada produtor e pague o\nvalor indicado em qualquer app habilitado",
                fontSize = 12.sp,
                color = Color(0xFF7A7168),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(24.dp))

            if (gruposPix.isEmpty()) {
                Text(
                    text = "Carrinho vazio.",
                    fontSize = 13.sp,
                    color = Color(0xFF7A7168),
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                gruposPix.forEach { grupo ->
                    PixProdutorCard(
                        chavePix = grupo.chavePix,
                        valorFormatado = formatoMoeda.format(grupo.subtotal),
                        onCopiar = { clipboard.setText(AnnotatedString(grupo.chavePix)) }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Espaço fixo: Spacer(weight) é incompatível com verticalScroll
            Spacer(Modifier.height(32.dp))

            AppButton(
                text = "Finalizar",
                onClick = { navController.popBackStack() },
                containerColor = btColor,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(12.dp))

            AppButton(
                text = "Cancelar",
                onClick = { showCancelDialog = true },
                containerColor = btColor,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// Um produtor presente no carrinho e quanto deve receber via Pix.
private data class GrupoPix(
    val chavePix: String,
    val subtotal: Double
)

@Composable
private fun PixProdutorCard(
    chavePix: String,
    valorFormatado: String,
    onCopiar: () -> Unit
) {
    var copied by remember(chavePix) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Text(
            text = "Pagar $valorFormatado",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E2B27)
        )
        Spacer(Modifier.height(6.dp))

        if (chavePix.isBlank()) {
            Text(
                text = "Produtor sem chave Pix cadastrada",
                fontSize = 12.sp,
                color = Color(0xFF7A7168)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFFCE5A14),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFAF7F2))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chavePix,
                    fontSize = 13.sp,
                    color = Color(0xFF2E2B27),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "Copiar",
                    tint = Color(0xFFCE5A14),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onCopiar(); copied = true }
                )
            }
            if (copied) {
                Spacer(Modifier.height(4.dp))
                Text("Código copiado!", fontSize = 12.sp, color = Color(0xFFCE5A14))
            }
        }
    }
}

@Composable
fun CheckoutConfirmationScreen(
    navController: NavController,
    backgroundRes: Int = R.drawable.img_bg_explorar
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CheckoutTopBar(onBack = { navController.popBackStack() })

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Confirmação",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2B27)
            )

            // Espaços fixos: Spacer(weight) é incompatível com verticalScroll
            Spacer(Modifier.height(120.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_bag_check),
                contentDescription = "Compra confirmada",
                tint = Color(0xFF2E2B27),
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Obrigado pela aquisição!",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E2B27)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Veja seu pedido em \"Meus pedidos\"!",
                fontSize = 14.sp,
                color = Color(0xFF7A7168)
            )

            Spacer(Modifier.height(120.dp))

            AppButton(
                text = "Continue comprando",
                onClick = {
                    navController.popBackStack(
                        route = CheckoutRoutes.PAYMENT,
                        inclusive = true
                    )
                },
                containerColor = Color(0xFFCE5A14),
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Voltar",
            tint = Color(0xFF2E2B27),
            modifier = Modifier
                .size(24.dp)
                .clickable { onBack() }
        )
    }
}

