package com.aracecultura.arace.ui.components.criar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FixedTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    unfocusedContainerColor: Color,
    focusedContainerColor: Color,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    unfocusedTextColor: Color = Color.Black,
    focusedTextColor: Color = Color.Black,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isFocused by remember { mutableStateOf(false) }

    val currentContainerColor = if (isFocused) focusedContainerColor else unfocusedContainerColor
    val currentBorderColor = if (isFocused) focusedBorderColor else unfocusedBorderColor
    val currentTextColor = if (isFocused) focusedTextColor else unfocusedTextColor
    val currentBorderWidth = if (isFocused) 2.dp else 1.dp

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            color = currentTextColor,
            fontSize = 18.sp,
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        maxLines = 1,
        modifier = modifier
            .fillMaxWidth(0.83f)
            .height(56.dp) // <--- Trava a altura exata do Material Design
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(15.dp))
            .background(currentContainerColor)
            .border(currentBorderWidth, currentBorderColor, RoundedCornerShape(15.dp))
            .padding(horizontal = 12.dp), // Ajustado para horizontal apenas, para o Box centralizar melhor a altura
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty() && placeholder != null) {
                    placeholder()
                }
                innerTextField()
            }
        }
    )
}