package com.aracecultura.arace.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.azulCinza
import com.aracecultura.arace.ui.theme.azulPrincipal
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.ui.theme.terracota
import com.aracecultura.arace.ui.theme.verdeAzeitona
import com.aracecultura.arace.ui.theme.verdePrincipal

@Composable
fun SecaoCategorias() {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconCategoria(btColor, R.drawable.ic_artesanato, "Artesanato", modifier = Modifier.weight(1f))
            IconCategoria(azulPrincipal, R.drawable.ic_casa, "Casa", modifier = Modifier.weight(1f))
            IconCategoria(verdePrincipal, R.drawable.ic_texteis, "Têxteis", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconCategoria(terracota, R.drawable.ic_ceramica, "Cerâmica", modifier = Modifier.weight(1f))
            IconCategoria(azulCinza, R.drawable.ic_acessorios, "Acessórios", modifier = Modifier.weight(1f))
            IconCategoria(verdeAzeitona, R.drawable.ic_cosmeticos, "Cosméticos", modifier = Modifier.weight(1f))
        }
    }
}