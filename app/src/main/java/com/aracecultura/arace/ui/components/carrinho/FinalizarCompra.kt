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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

object CheckoutRoutes {
    const val PAYMENT      = "checkout_payment"
    const val CONFIRMATION = "checkout_confirmation"
}

@Composable
fun CheckoutPaymentScreen(
    navController: NavController,
    pixCode: String = "00020126330014br.gov.bcb.pix0111015482157835204000053039865802BR5919DARLY SILVA DA CRUZ6005SERRA62070503***63042D8E",
    backgroundRes: Int = R.drawable.img_bg_explorar,
    qrCodeRes: Int = R.drawable.qr_code_placeholder
) {
    var copied by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

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

            CheckoutTopBar(onBack = { navController.popBackStack() })

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Finalizar Compra",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2B27)
            )

            Spacer(Modifier.height(28.dp))

            Image(
                painter = painterResource(id = qrCodeRes),
                contentDescription = "QR Code Pix",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "pedido aguardando pagamento",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E2B27)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Copie o código abaixo para pagar via Pix\nem qualquer aplicativo habilitado",
                fontSize = 12.sp,
                color = Color(0xFF7A7168),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
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
                    text = pixCode,
                    fontSize = 12.sp,
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
                        .clickable { copied = true }
                )
            }

            if (copied) {
                Spacer(Modifier.height(6.dp))
                Text("Código copiado!", fontSize = 12.sp, color = Color(0xFFCE5A14))
            }

            // Espaço fixo: Spacer(weight) é incompatível com verticalScroll
            Spacer(Modifier.height(48.dp))

            AppButton(
                text = "Copiar código",
                onClick = { copied = true },
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
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF2E2B27),
            modifier = Modifier.size(24.dp)
        )
    }
}

