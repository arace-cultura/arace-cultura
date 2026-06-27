package com.aracecultura.arace.ui.auth

import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.aracecultura.arace.R
import com.aracecultura.arace.data.ImagemRepository
import com.aracecultura.arace.ui.theme.GoogleSans
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

/** Compose da antiga `Cadastro` (fragment_cadastro.xml). Lógica de auth/Firestore portada 1:1. */
@Composable
fun TelaCadastro(
    onSucesso: () -> Unit,
    onVoltar: () -> Unit,
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var fotoPerfilUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var carregando by remember { mutableStateOf(false) }

    val fotoPerfilLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> fotoPerfilUri = uri }

    val bannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> bannerUri = uri }

    fun credenciaisValidas(): Boolean {
        val nomeOk = nome.trim().isNotEmpty()
        val emailOk = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        val senhaOk = senha.trim().length >= 6
        return nomeOk && emailOk && senhaOk
    }

    // Upload em segundo plano: a navegação não espera as imagens. Usa o escopo
    // da activity (que sobrevive à saída desta tela), espelhando o Fragment.
    fun enviarImagensPerfil(uid: String) {
        if (fotoPerfilUri == null && bannerUri == null) return
        val activity = context as? ComponentActivity ?: return
        val appContext = context.applicationContext
        val foto = fotoPerfilUri
        val banner = bannerUri
        activity.lifecycleScope.launch {
            try {
                val updates = mutableMapOf<String, Any>()
                foto?.let { updates["fotoUrl"] = ImagemRepository.upload(appContext, uid, "perfil", it) }
                banner?.let { updates["bannerUrl"] = ImagemRepository.upload(appContext, uid, "banner", it) }
                if (updates.isNotEmpty()) {
                    db.collection("Usuarios").document(uid).set(updates, SetOptions.merge())
                }
            } catch (_: Exception) {
                // sem imagem: estado válido; edição de perfil cobre depois
            }
        }
    }

    fun cadastrar() {
        if (!credenciaisValidas()) {
            Toast.makeText(context, R.string.erro_preencha_dados, Toast.LENGTH_SHORT).show()
            return
        }
        // Acréscimo em relação ao Fragment: valida a confirmação de senha.
        if (senha != confirmarSenha) {
            Toast.makeText(context, R.string.erro_senhas_diferentes, Toast.LENGTH_SHORT).show()
            return
        }

        carregando = true
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.trim(), senha.trim())
            .addOnCompleteListener { cadastro ->
                if (cadastro.isSuccessful) {
                    val uid = cadastro.result.user?.uid
                    if (uid != null) {
                        db.collection("Usuarios").document(uid)
                            .set(hashMapOf("nome" to nome.trim(), "isProdutor" to false))
                            .addOnFailureListener {
                                Log.e("Cadastro", "Falha ao salvar dados do usuário", it)
                            }
                        enviarImagensPerfil(uid)
                        carregando = false
                        onSucesso()
                    } else {
                        carregando = false
                    }
                }
            }
            .addOnFailureListener { e ->
                carregando = false
                val msg = when (e) {
                    is FirebaseAuthWeakPasswordException -> R.string.erro_senha_fraca
                    is FirebaseAuthInvalidCredentialsException -> R.string.erro_email_invalido
                    is FirebaseAuthUserCollisionException -> R.string.erro_conta_existente
                    is FirebaseNetworkException -> R.string.erro_conexao
                    else -> R.string.erro_cadastro_usuario
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }

    FundoAuth {
        Image(
            painter = painterResource(R.drawable.lago),
            contentDescription = null,
            modifier = Modifier.size(150.dp).padding(bottom = 30.dp),
        )
        Text(
            text = stringResource(R.string.cadastro_titulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 24.sp,
            color = CorTexto, modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.cadastro_subtitulo),
            fontFamily = GoogleSans, fontWeight = FontWeight.Normal, fontSize = 18.sp,
            color = CorTexto, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        )

        CampoArace(
            valor = nome, aoMudar = { nome = it },
            rotulo = stringResource(R.string.nome),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = email, aoMudar = { email = it },
            rotulo = stringResource(R.string.email), teclado = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = senha, aoMudar = { senha = it },
            rotulo = stringResource(R.string.senha), senha = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = confirmarSenha, aoMudar = { confirmarSenha = it },
            rotulo = stringResource(R.string.confirme_senha), senha = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.selecionar_foto_perfil),
            onClick = {
                fotoPerfilLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            larguraTotal = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        )
        Text(
            text = stringResource(
                if (fotoPerfilUri == null) R.string.nenhuma_foto else R.string.foto_perfil_selecionada
            ),
            fontFamily = GoogleSans, fontSize = 14.sp, color = CorTexto,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.selecionar_banner),
            onClick = {
                bannerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            larguraTotal = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        )
        Text(
            text = stringResource(
                if (bannerUri == null) R.string.nenhum_banner else R.string.banner_selecionado
            ),
            fontFamily = GoogleSans, fontSize = 14.sp, color = CorTexto,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.concluir),
            onClick = { cadastrar() },
            habilitado = !carregando,
        )
        LinkVoltar(onVoltar = onVoltar)
    }
}
