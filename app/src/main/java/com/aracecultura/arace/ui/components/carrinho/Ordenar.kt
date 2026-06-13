package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun Ordenar() {
    AppButton(
        modifier = Modifier
            // -1dp sobrepõe a junção com o elemento acima e elimina o
            // fio transparente causado por antialiasing na fronteira
            .offset(y = (-1).dp)
            .width(140.dp)
            .height(40.dp),
        text = "Ordenar por",
        textColor = bgDefault,
        containerColor = btColor,
        borderColor = btColor,
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = 30.dp,
            bottomStart = 0.dp
        ),
        onClick = {  }
    )
}