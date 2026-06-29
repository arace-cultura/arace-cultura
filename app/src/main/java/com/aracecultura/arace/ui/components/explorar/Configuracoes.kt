package com.aracecultura.arace.ui.components.explorar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.R

@Composable
fun TelaConfiguracoes(
    onBackClick: () -> Unit = {},
    onMeusDadosClick: () -> Unit = {},
    onMeusPedidosClick: () -> Unit = {},
    onDeletarContaClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val showDeletarContaDialog = remember { mutableStateOf(false) }

    if (showDeletarContaDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeletarContaDialog.value = false },
            containerColor = Color(0xFFFAF7F2),
            title = {
                Text(
                    text = stringResource(R.string.deletar_conta_titulo),
                    color = Color(0xFF2E2B27),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.deletar_conta_msg),
                    color = Color(0xFF7A7168)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletarContaDialog.value = false
                        onDeletarContaClick()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.sim_deletar_conta),
                        color = Color(0xFFCE5A14),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletarContaDialog.value = false }) {
                    Text(stringResource(R.string.voltar), color = Color(0xFF7A7168))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(bgDefault)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.voltar),
                        tint = Color.DarkGray
                    )
                }
            }


            Text(
                text = stringResource(R.string.configuracoes),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        ConfiguracaoItem(
            icon = painterResource(R.drawable.ic_user_data),
            text = stringResource(R.string.meus_dados),
            onClick = onMeusDadosClick
        )

        ConfiguracaoItem(
            icon = painterResource(R.drawable.ic_meus_pedidos),
            text = stringResource(R.string.meus_pedidos),
            onClick = onMeusPedidosClick
        )

        ConfiguracaoItem(
            icon = painterResource(R.drawable.ic_deletar_conta),
            text = stringResource(R.string.deletar_conta),
            onClick = { showDeletarContaDialog.value = true },
            iconTint = Color(0xFFCE5A14),
            textColor = Color(0xFFCE5A14)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ConfiguracaoItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    iconTint: Color = Color(0xFF4A7D59),
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF0F5F2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.width(20.dp))


        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )


        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}
