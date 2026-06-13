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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.bgDefault

@Composable
fun EditarPerfilUsuario(
    uid: String,
    viewModel: PerfilViewModel = viewModel(),
    onVoltarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val usuario by viewModel.usuario.collectAsState()
    val scrollState = rememberScrollState()

    var nomeInput by remember { mutableStateOf("") }
    var novaFotoUri by remember { mutableStateOf<Uri?>(null) }
    var novoBannerUri by remember { mutableStateOf<Uri?>(null) }

    val fotoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novaFotoUri = uri }

    val bannerPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novoBannerUri = uri }

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarDadosUsuario(uid)
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

            // Bloco Superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                Column(Modifier.fillMaxSize()) {
                    // Banner: toque para trocar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f)
                            .background(Color(0xFFD66027))
                            .clickable {
                                bannerPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
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
                        Text(
                            text = "Toque para alterar o banner",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(
                                    Color.Black.copy(alpha = 0.35f),
                                    RoundedCornerShape(topStart = 8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.weight(4f))
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

                // Foto de perfil centralizada: toque para trocar
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.Center)
                        .offset(y = 20.dp)
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
                    } else {
                        Text(
                            text = "Toque para\nadicionar foto",
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
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

                Spacer(modifier = Modifier.weight(1f))

                // Botão Salvar
                Button(
                    onClick = {
                        viewModel.salvarEdicaoPerfil(
                            context = context,
                            novoNome = nomeInput,
                            uid = uid,
                            novaFotoUri = novaFotoUri,
                            novoBannerUri = novoBannerUri,
                            onSucesso = onVoltarClick
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD66027)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Salvar Alterações",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
