package com.aracecultura.arace.ui.components.produto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.R

@Composable
fun StatusBolinhaGeral(selectedIndex: Int, index: Int) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (index == selectedIndex) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(
                        width = 5.dp,
                        color = Color(0xFFE37038),
                        shape = CircleShape
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Gray, shape = CircleShape)
            )
        }
    }
}