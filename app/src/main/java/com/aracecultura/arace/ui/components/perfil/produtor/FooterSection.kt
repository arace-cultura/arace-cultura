package com.aracecultura.arace.ui.components.perfil.produtor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import java.util.Locale

@Composable
fun FooterSection(
    brandColor: Color,
    produtor: Produtor,
    avaliacaoMedia: Double?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(brandColor)
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.informacoes),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.width(100.dp).padding(top = 4.dp, bottom = 16.dp)
            )
        }

        FooterRow(label = stringResource(R.string.endereco), value = produtor.endereco.ifBlank { produtor.cep })
        Spacer(modifier = Modifier.height(12.dp))
        FooterRow(
            label = stringResource(R.string.telefone),
            value = produtor.telefone.ifBlank { stringResource(R.string.nao_informado) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        FooterRow(
            label = stringResource(R.string.avaliacao_media),
            value = avaliacaoMedia?.let { String.format(Locale.getDefault(), "%.1f", it) }
                ?: stringResource(R.string.sem_avaliacoes)
        )
    }
}
