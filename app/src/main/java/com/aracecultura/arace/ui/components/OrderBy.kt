package com.aracecultura.arace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

object Ordem {
    var ordem by mutableStateOf("Selecione uma ordem...")
}

@Composable
fun OrderBy (textColor: Color) {
    val placeholderTextColor = textColor.copy(alpha = 0.7f)
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .background(bgDefault)
            .padding(start = 8.dp)
    ) {
        Text(Ordem.ordem,
            fontSize = 18.sp,
            color = placeholderTextColor,
            modifier = Modifier.clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                //.padding(start = 12.dp) // antes dp background é margin
                .background(bgDefault)
                .width(150.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Nome", fontSize = 18.sp) },
                onClick = { expanded = false; Ordem.ordem = "Nome"},
            )
            Spacer(modifier = Modifier.fillMaxWidth(0.9f).height(3.dp).background(btColor))
            DropdownMenuItem(
                text = { Text("Preço", fontSize = 18.sp) },
                onClick = { expanded = false; Ordem.ordem = "Preço" }
            )
        }
    }
}