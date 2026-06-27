package com.aracecultura.arace.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.ui.theme.GoogleSans
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/** Compose da antiga `EntrarLojaFragment` (fragment_entrar_loja.xml). Lógica 1:1. */
@Composable
fun TelaEntrarLoja(
    onSucesso: () -> Unit,
    onVoltar: () -> Unit,
) {
    val escopo = rememberCoroutineScope()
    var nomeLoja by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var carregando by remember { mutableStateOf(false) }

    val msgNaoAutenticado = stringResource(R.string.erro_usuario_nao_autenticado)
    val msgErroGenerico = stringResource(R.string.erro_entrar_loja)

    fun confirmar() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            erro = msgNaoAutenticado
            return
        }
        erro = null
        carregando = true
        escopo.launch {
            try {
                LojaRepository.entrarEmLoja(uid = uid, nomeLoja = nomeLoja, senha = senha)
                onSucesso()
            } catch (e: Exception) {
                erro = e.message ?: msgErroGenerico
                carregando = false
            }
        }
    }

    FundoAuth(comImagem = false, arranjo = Arrangement.Center) {
        Text(
            text = stringResource(R.string.entrar_loja_existente),
            fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 24.sp,
            color = CorTexto, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        )

        CampoArace(
            valor = nomeLoja, aoMudar = { nomeLoja = it },
            rotulo = stringResource(R.string.entrar_loja_nome),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = senha, aoMudar = { senha = it },
            rotulo = stringResource(R.string.senha_loja), senha = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )

        erro?.let {
            Text(
                text = it,
                fontFamily = GoogleSans, fontSize = 14.sp, color = CorLaranja,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }

        BotaoArace(
            texto = stringResource(R.string.entrar_na_loja),
            onClick = { confirmar() },
            habilitado = !carregando,
            larguraTotal = true,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
        )
        LinkVoltar(onVoltar = onVoltar, modifier = Modifier.padding(top = 0.dp))
    }
}
