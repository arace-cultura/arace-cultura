package com.aracecultura.arace.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans

@Composable
fun TelaEscolhaLoja(
    onCriarNova: () -> Unit,
    onEntrarExistente: () -> Unit,
    onVoltar: () -> Unit,
) {
    FundoAuth(comImagem = false, arranjo = Arrangement.Center) {
        Text(
            text = stringResource(R.string.escolha_modo_produtor),
            fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 28.sp,
            color = CorTexto, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        Text(
            text = stringResource(R.string.escolha_sem_loja),
            fontFamily = GoogleSans, fontSize = 16.sp, color = CorTexto, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.escolha_cadastrar_nova_loja),
            onClick = onCriarNova,
            larguraTotal = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        )
        BotaoArace(
            texto = stringResource(R.string.entrar_loja_existente),
            onClick = onEntrarExistente,
            cor = CorVerde,
            larguraTotal = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
        )
        LinkVoltar(onVoltar = onVoltar, modifier = Modifier.padding(top = 0.dp))
    }
}
