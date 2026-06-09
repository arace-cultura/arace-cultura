package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.theme.GoogleSans

@Composable
fun FooterRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = GoogleSans, color = Color.White, fontSize = 14.sp)
        Text(text = value, fontFamily = GoogleSans, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.End)
    }
}