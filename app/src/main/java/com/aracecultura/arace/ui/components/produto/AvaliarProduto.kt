package com.aracecultura.arace.ui.components.produto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun BotaoAvaliarProduto(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Só o botão "Avaliar"; a seleção de nota é feita no diálogo. A fileira de
    // estrelas vazias daqui era redundante com as estrelas da média logo acima.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = stringResource(R.string.avaliar),
            onClick = onClick,
            containerColor = btColor,
            textColor = Color.White,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            fontSize = 18.sp
        )
    }
}

@Composable
fun DialogoAvaliarProduto(
    notaAtual: Int?,
    salvando: Boolean,
    erro: String?,
    onAvaliar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    var notaSelecionada by remember(notaAtual) { mutableStateOf(notaAtual) }

    Dialog(
        onDismissRequest = { if (!salvando) onCancelar() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(bgDefault)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.avaliar_produto),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.avaliar_instrucao),
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { nota ->
                    val selecaoAtual = notaSelecionada
                    val selecionada = selecaoAtual != null && nota <= selecaoAtual
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (selecionada) btColor else Color.Transparent)
                            .border(3.dp, btColor, CircleShape)
                            .clickable(enabled = !salvando) {
                                notaSelecionada = nota
                                onAvaliar(nota)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nota.toString(),
                            fontSize = 22.sp,
                            color = if (selecionada) Color.White else btColor
                        )
                    }
                }
            }
            if (erro != null) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = erro,
                    color = Color.Red,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(24.dp))
            if (salvando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = btColor,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.cancelar).lowercase(),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable(onClick = onCancelar)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        }
    }
}
