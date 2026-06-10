package com.aracecultura.arace.ui.components.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun BotaoVisualizacao(
    modoAtualIsProdutor: Boolean,
    onModoChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AppButton(
            text = "Visualização",
            textColor = Color.Black,
            containerColor = bgDefault,
            borderColor = btColor,
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp,
                bottomStart = 30.dp
            ),
            modifier = Modifier
                .height(40.dp)
                .wrapContentWidth(),
            onClick = { expandido = true }
        )
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },  modifier = Modifier.background(bgDefault)
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = !modoAtualIsProdutor,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = btColor,
                                unselectedColor = btColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cliente", fontSize = 16.sp)
                    }
                },
                onClick = {
                    expandido = false
                    onModoChanged(false)
                },
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = modoAtualIsProdutor,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = btColor,
                                unselectedColor = btColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Produtor", fontSize = 16.sp)
                    }
                },
                onClick = {
                    expandido = false
                    onModoChanged(true)
                }
            )
        }
    }
}
