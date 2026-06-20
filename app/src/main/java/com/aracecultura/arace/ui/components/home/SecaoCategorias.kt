package com.aracecultura.arace.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.CategoriasProduto
import com.aracecultura.arace.ui.theme.azulCinza
import com.aracecultura.arace.ui.theme.azulPrincipal
import com.aracecultura.arace.ui.theme.btColor
import com.aracecultura.arace.ui.theme.terracota
import com.aracecultura.arace.ui.theme.verdeAzeitona
import com.aracecultura.arace.ui.theme.verdePrincipal

// Visual (cor + ícone) de cada categoria. Os nomes vêm de CategoriasProduto.TODAS;
// categorias sem entrada aqui usam o fallback abaixo.
private data class VisualCategoria(val cor: Color, val icone: Int)

private val visuais: Map<String, VisualCategoria> = mapOf(
    "Artesanato" to VisualCategoria(btColor, R.drawable.ic_artesanato),
    "Casa" to VisualCategoria(azulPrincipal, R.drawable.ic_casa),
    "Têxteis" to VisualCategoria(verdePrincipal, R.drawable.ic_texteis),
    "Cerâmica" to VisualCategoria(terracota, R.drawable.ic_ceramica),
    "Acessórios" to VisualCategoria(azulCinza, R.drawable.ic_acessorios),
    "Cosméticos" to VisualCategoria(verdeAzeitona, R.drawable.ic_cosmeticos),
)

private val visualFallback = VisualCategoria(btColor, R.drawable.ic_artesanato)

@Composable
fun SecaoCategorias(
    onCategoriaClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoriasProduto.TODAS.chunked(3).forEach { linha ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                linha.forEach { categoria ->
                    val visual = visuais[categoria] ?: visualFallback
                    IconCategoria(
                        visual.cor,
                        visual.icone,
                        categoria,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategoriaClick(categoria) }
                    )
                }
                // Mantém o alinhamento quando a última linha não está completa
                repeat(3 - linha.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
