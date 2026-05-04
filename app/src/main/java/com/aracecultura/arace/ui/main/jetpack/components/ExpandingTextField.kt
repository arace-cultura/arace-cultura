package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandingTextField(
    text: String,
    textColor: Color,
    onTextChange: (String) -> Unit,
    placeholder: String,
    containerColor: Color,
    borderColor: Color
) {
    val placeholderTextColor = textColor.copy(alpha = 0.7f)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val expandingTextFieldWidth = screenWidth * 0.83f
    val expandingTextFieldHeight = expandingTextFieldWidth * (49f/348f) // ≈ 0.14
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            color = textColor,
            fontSize = 18.sp,
        ),
        minLines = 1,
        maxLines = Int.MAX_VALUE,
        modifier = Modifier
            .width(expandingTextFieldWidth)
            .heightIn(expandingTextFieldHeight)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor) // Usa a cor passada
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 18.sp,
                        color = placeholderTextColor
                    )
                }
                innerTextField()
            }
        }
    )
}