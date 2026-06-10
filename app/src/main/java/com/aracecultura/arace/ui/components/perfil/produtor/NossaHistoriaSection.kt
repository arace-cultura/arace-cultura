package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.data.model.Produtor
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun NossaHistoriaSection(
    brandColor: Color,
    produtor: Produtor
) {
    val nome = produtor.nomeLoja.ifBlank { produtor.nomeCompleto }.ifBlank { "Este produtor" }
    val historia = produtor.historia.ifBlank { "Historia ainda não cadastrada." }
    var historiaExpandida by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Nossa história",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HistoriaImageBox(
                    imageUrl = produtor.fotosHistoria.getOrNull(2),
                    modifier = Modifier.weight(4f).fillMaxWidth(),
                    fallbackColor = Color.LightGray
                )
                Box(modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF6A9960)))
            }
            Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(0.5f).fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFF1B74F)))
                HistoriaImageBox(
                    imageUrl = produtor.fotosHistoria.getOrNull(0),
                    modifier = Modifier.weight(2f).fillMaxWidth(),
                    fallbackColor = Color.DarkGray
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HistoriaImageBox(
                    imageUrl = produtor.fotosHistoria.getOrNull(1),
                    modifier = Modifier.weight(3f).fillMaxWidth(),
                    fallbackColor = Color.Gray
                )
                Box(modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF3B6897)))
            }
        }

        Text(
            text = historia,
            fontSize = 16.sp,
            maxLines = if (historiaExpandida) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(4.dp, 16.dp)
                .animateContentSize()
        )

        AppButton(
            text = if (historiaExpandida) "Ler menos" else "Ler mais",
            onClick = { historiaExpandida = !historiaExpandida },
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
                .background(btColor)
                .padding(0.dp),
            containerColor = brandColor,
            textColor = Color.White
        )
    }
}

@Composable
private fun HistoriaImageBox(
    imageUrl: String?,
    fallbackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(fallbackColor)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
