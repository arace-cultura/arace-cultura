package com.aracecultura.arace.ui.main.jetpack.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.ui.main.jetpack.theme.BgDefault
import com.aracecultura.arace.ui.main.jetpack.theme.BtColor

object ButtonFilter {
    var statusCategoriasAbertas by mutableStateOf(false)
}

@Composable
fun ButtonFilter(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonMoreCustomizable(
        text = text,
        textColor = BgDefault,
        containerColor = BtColor,
        borderColor = BtColor,
        widthButton = 150.0,
        heightButton = 40.0,
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = 30.dp,
            bottomStart = 0.dp
        ),
        modifier = modifier
    ) {
        ButtonFilter.statusCategoriasAbertas = !ButtonFilter.statusCategoriasAbertas

    }
}
