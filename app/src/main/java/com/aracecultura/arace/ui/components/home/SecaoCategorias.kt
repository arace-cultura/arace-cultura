package com.aracecultura.arace.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    // Agora a Column vai respeitar o wrapContentHeight perfeitamente
    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaçamento vertical entre as linhas
    ) {
        // PRIMEIRA LINHA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Distribui os 3 itens igualmente na largura
        ) {
            Spacer(Modifier.width(10.dp))
            IconCategoria(btColor, R.drawable.ic_artesanato, "Artesanato", modifier = Modifier)
            IconCategoria(azulPrincipal, R.drawable.ic_casa, "Casa",  modifier = Modifier)
            IconCategoria(verdePrincipal, R.drawable.ic_texteis, "Têxteis", modifier = Modifier)
            Spacer(Modifier.width(10.dp))
        }

        // SEGUNDA LINHA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Alinhamento idêntico à primeira linha
        ){
            Spacer(Modifier.width(10.dp))
            IconCategoria(terracota, R.drawable.ic_ceramica, "Cerâmica", modifier = Modifier)
            IconCategoria(azulCinza, R.drawable.ic_acessorios, "Acessórios", modifier = Modifier)
            IconCategoria(verdeAzeitona, R.drawable.ic_cosmeticos, "Cosméticos", modifier = Modifier)
            Spacer(Modifier.width(10.dp))
        }
    }
}