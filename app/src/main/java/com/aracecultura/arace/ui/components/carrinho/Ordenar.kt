package com.aracecultura.arace.ui.components.carrinho

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

// Ordenação do carrinho (sem filtro de categorias). As chaves batem com as do
// Explorar: "nome", "preco_asc", "preco_desc".
@Composable
fun Ordenar(onSelecionar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AppButton(
            modifier = Modifier
                // -1dp sobrepõe a junção com o elemento acima e elimina o
                // fio transparente causado por antialiasing na fronteira
                .offset(y = (-1).dp)
                .width(140.dp)
                .height(40.dp),
            text = stringResource(R.string.ordenar_por),
            textColor = bgDefault,
            containerColor = btColor,
            borderColor = btColor,
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomEnd = 30.dp,
                bottomStart = 0.dp
            ),
            onClick = { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(bgDefault)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.ordenar_nome)) },
                onClick = { onSelecionar("nome"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.ordenar_preco_menor)) },
                onClick = { onSelecionar("preco_asc"); expanded = false }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.ordenar_preco_maior)) },
                onClick = { onSelecionar("preco_desc"); expanded = false }
            )
        }
    }
}
