package com.aracecultura.arace.ui.components.perfil.cliente

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun EditarPerfilUsuario(
    uid: String,
    viewModel: PerfilViewModel = viewModel(),
    onVoltarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val usuario by viewModel.usuario.collectAsState()
    val produtor by viewModel.produtor.collectAsState()
    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var nomeInput by remember { mutableStateOf("") }
    var novaFotoUri by remember { mutableStateOf<Uri?>(null) }
    var novoBannerUri by remember { mutableStateOf<Uri?>(null) }

    // Troca de senha da loja (só para contas produtoras)
    var senhaAtual by remember { mutableStateOf("") }
    var senhaNova by remember { mutableStateOf("") }
    var erroSenha by remember { mutableStateOf<String?>(null) }

    val fotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novaFotoUri = uri }

    val bannerPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novoBannerUri = uri }

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarDadosUsuario(uid)
            viewModel.carregarDadosProdutor(uid)
        }
    }

    LaunchedEffect(usuario) {
        if (nomeInput.isEmpty() && usuario.nome.isNotEmpty()) {
            nomeInput = usuario.nome
        }
    }

    Box(Modifier.background(bgDefault)) {
        Image(
            painter = painterResource(id = R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.3f
        )

        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

            // Bloco superior SEM cascata: o banner tem altura PRÓPRIA e a foto
            // sobrepõe a borda inferior dele. A altura do hero é só banner +
            // metade da foto — sem vão vazio. Ajuste os dois multiplicadores
            // de forma independente (banner e foto não se arrastam mais juntos).
            val alturaBanner = screenWidth * 0.45f
            val tamanhoFoto = screenWidth * 0.34f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaBanner + tamanhoFoto / 2)
            ) {
                // Banner: toque para trocar. Scrim 0.3 + ícone de edição.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(alturaBanner)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFFD66027))
                        .clickable {
                            bannerPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val bannerModel: Any? = novoBannerUri ?: usuario.bannerUrl.ifBlank { null }
                    if (bannerModel != null) {
                        AsyncImage(
                            model = bannerModel,
                            contentDescription = "Banner do perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_editar_imagem),
                        contentDescription = "Alterar banner",
                        modifier = Modifier.size(56.dp)
                    )
                }

                // Botão de Voltar
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFF3B4045), CircleShape)
                        .clickable { onVoltarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                // Foto: sobrepõe a borda inferior do banner. Scrim 0.3 + ícone.
                Box(
                    modifier = Modifier
                        .size(tamanhoFoto)
                        .align(Alignment.BottomCenter)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            fotoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val fotoModel: Any? = novaFotoUri ?: usuario.fotoUrl.ifBlank { null }
                    if (fotoModel != null) {
                        AsyncImage(
                            model = fotoModel,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_editar_imagem),
                        contentDescription = "Alterar foto de perfil",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgDefault)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Alterar Informações",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )

                // OutlinedTextField padrão
                OutlinedTextField(
                    value = nomeInput,
                    onValueChange = { nomeInput = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Campo de E-mail Desabilitado
                OutlinedTextField(
                    value = usuario.email,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("E-mail (Não alterável)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Troca de senha da loja — só para contas produtoras
                if (produtor != null) {
                    Text(
                        text = "Senha da loja",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = senhaAtual,
                            onValueChange = { senhaAtual = it; erroSenha = null },
                            label = { Text("Senha atual") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = erroSenha != null,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (erroSenha != null) {
                            Text(
                                text = erroSenha!!,
                                color = Color.Red,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = senhaNova,
                        onValueChange = { senhaNova = it },
                        label = { Text("Nova senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Salvar — se os campos de senha estiverem preenchidos,
                // a troca de senha é validada antes de salvar o resto.
                AppButton(
                    text = "Salvar Alterações",
                    onClick = {
                        erroSenha = null
                        val querTrocarSenha = senhaAtual.isNotBlank() || senhaNova.isNotBlank()
                        when {
                            querTrocarSenha && (senhaAtual.isBlank() || senhaNova.isBlank()) -> {
                                erroSenha = "Preencha a senha atual e a nova."
                            }
                            querTrocarSenha -> {
                                viewModel.alterarSenhaLoja(
                                    uid = uid,
                                    senhaAtual = senhaAtual,
                                    senhaNova = senhaNova,
                                    onSucesso = {
                                        viewModel.salvarEdicaoPerfil(
                                            context = context,
                                            novoNome = nomeInput,
                                            uid = uid,
                                            novaFotoUri = novaFotoUri,
                                            novoBannerUri = novoBannerUri,
                                            onSucesso = onVoltarClick
                                        )
                                    },
                                    onSenhaIncorreta = { erroSenha = "Senha atual incorreta." },
                                    onErro = { erroSenha = it }
                                )
                            }
                            else -> {
                                viewModel.salvarEdicaoPerfil(
                                    context = context,
                                    novoNome = nomeInput,
                                    uid = uid,
                                    novaFotoUri = novaFotoUri,
                                    novoBannerUri = novoBannerUri,
                                    onSucesso = onVoltarClick
                                )
                            }
                        }
                    },
                    textColor = Color.White,
                    containerColor = btColor,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}
