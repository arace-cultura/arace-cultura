package com.aracecultura.arace.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
fun StatusBolinha(selectedIndex: Int, index: Int, icone: Int = R.drawable.flower) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (index == selectedIndex) {
            Image(
                painter = painterResource(icone),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
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