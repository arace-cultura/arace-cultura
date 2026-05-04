package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    onTextChange: (String) -> Unit,
    placeholder: String,
    containerColor: Color,
) {
    val placeholderTextColor = textColor.copy(alpha = 0.7f)
    BoxWithConstraints(
        modifier = modifier
    ) {
        val calculatedHeight = maxWidth * (50f / 340f)
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 18.sp,
            ),
            singleLine = true,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = calculatedHeight)
                .background(containerColor)
                .padding(start = 8.dp),
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
}
