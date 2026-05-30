package com.aracecultura.arace.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.ui.theme.GoogleSans

@Composable
fun IconCategoria(
    backgroundColor: Color,
    imagem: Int,
    legenda: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .padding(10.dp)
        ){
            Image(
                painter = painterResource(imagem),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = legenda,
            fontSize = 14.sp,
            fontFamily = GoogleSans,
            fontWeight = FontWeight.Medium
        )
    }
}