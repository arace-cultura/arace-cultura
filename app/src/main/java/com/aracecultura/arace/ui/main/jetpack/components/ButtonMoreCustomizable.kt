package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ButtonMoreCustomizable(
    text: String,
    textColor: Color,
    containerColor: Color,
    borderColor: Color,
    widthButton: Double, // Tentei passar dp mas não deu certo
    heightButton: Double,
    shape: Shape = RoundedCornerShape(50),
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Button(
        onClick = onClick,
        modifier = modifier.then(Modifier.size(widthButton.dp, heightButton.dp)),
        shape = shape,
        border = BorderStroke(2.dp, borderColor),
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

