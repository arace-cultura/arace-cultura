package com.aracecultura.arace.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
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

/** Compose da antiga `RecuperarSenha` (fragment_recuperar_senha.xml). Lógica 1:1. */
@Composable
fun TelaRecuperarSenha(
    onVoltar: () -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    fun emailValido() =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun recuperar() {
        if (!emailValido()) {
            Toast.makeText(context, R.string.erro_email_invalido, Toast.LENGTH_SHORT).show()
            return
        }
        carregando = true
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                carregando = false
                Toast.makeText(context, R.string.recuperar_email_enviado, Toast.LENGTH_LONG).show()
                onVoltar()
            }
            .addOnFailureListener {
                carregando = false
                Toast.makeText(context, R.string.erro_recuperar_senha, Toast.LENGTH_SHORT).show()
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
            text = stringResource(R.string.recuperar_titulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 20.sp,
            color = CorTexto, modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.recuperar_instrucao),
            fontFamily = GoogleSans, fontSize = 15.sp, color = CorTexto,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
        )

        CampoArace(
            valor = email, aoMudar = { email = it },
            rotulo = stringResource(R.string.email),
            teclado = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.recuperar_botao),
            onClick = { recuperar() },
            habilitado = !carregando,
        )
        LinkVoltar(onVoltar = onVoltar)
    }
}
