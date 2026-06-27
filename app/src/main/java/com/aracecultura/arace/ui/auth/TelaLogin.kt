package com.aracecultura.arace.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans
import com.google.firebase.auth.FirebaseAuth

/**
 * Versão Compose da antiga tela de login (Fragment `Login` + fragment_login.xml).
 * Lógica de autenticação portada 1:1; a navegação sai por callbacks.
 */
@Composable
fun TelaLogin(
    onSucesso: () -> Unit,
    onEsqueciSenha: () -> Unit,
    onVoltar: () -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    fun credenciaisValidas(): Boolean {
        val emailOk = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        return emailOk && senha.trim().length >= 6
    }

    fun entrar() {
        if (!credenciaisValidas()) {
            Toast.makeText(context, R.string.erro_dados_login, Toast.LENGTH_SHORT).show()
            return
        }
        carregando = true
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.trim(), senha.trim())
            .addOnSuccessListener { carregando = false; onSucesso() }
            .addOnFailureListener {
                carregando = false
                Toast.makeText(context, R.string.erro_falha_login, Toast.LENGTH_SHORT).show()
            }
    }

    FundoAuth {
        Text(
            text = stringResource(R.string.login_titulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 20.sp,
            color = CorTexto, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.login_subtitulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 24.sp,
            color = CorTexto, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        )
        Image(
            painter = painterResource(R.drawable.panela_barro),
            contentDescription = null,
            modifier = Modifier.size(150.dp).padding(bottom = 30.dp),
        )
        Text(
            text = stringResource(R.string.login_entrar_titulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 18.sp,
            color = CorTexto, modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))

        CampoArace(
            valor = email, aoMudar = { email = it },
            rotulo = stringResource(R.string.email),
            teclado = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = senha, aoMudar = { senha = it },
            rotulo = stringResource(R.string.senha), senha = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )

        Text(
            text = stringResource(R.string.login_esqueci_senha),
            fontFamily = GoogleSans, fontSize = 15.sp, color = CorAzul, textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().clickable { onEsqueciSenha() }.padding(vertical = 8.dp),
        )
        Spacer(Modifier.height(4.dp))

        BotaoArace(
            texto = stringResource(R.string.login_botao_entrar),
            onClick = { entrar() },
            habilitado = !carregando,
        )
        LinkVoltar(onVoltar = onVoltar)
    }
}
