package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ButtonCustomized(
    text: String,
    textColor: Color,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val buttonWidth = screenWidth * 0.472f
    val buttonHeight = buttonWidth * (90f/348f)
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(buttonWidth)
            .heightIn(min = buttonHeight),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, borderColor),
        enabled = true,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor,
    )
    ){
        Text(
            text = text,
            fontSize = 18.sp,
            color = textColor
        )
    }
}

fun onClick() {
    TODO("Not yet implemented")
}
