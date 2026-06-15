package com.aracecultura.arace.ui.components.perfil.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.perfil.BotaoVisualizacao
import com.aracecultura.arace.ui.theme.bgDefault

@OptIn(ExperimentalMaterial3Api::class) // Necessário para ajustar os RadioButtons nativos
@Composable
fun PerfilCliente(
    uid: String,
    viewModel: PerfilViewModel = viewModel(),
    onEditClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onModoChanged: (Boolean) -> Unit = {} // Repassa se é produtor (true) ou cliente (false)
) {
    val usuario by viewModel.usuario.collectAsState()
    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarDadosUsuario(uid)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.85f)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f)
                            .background(Color(0xFFD66027))
                    ) {
                        if (usuario.bannerUrl.isNotBlank()) {
                            AsyncImage(
                                model = usuario.bannerUrl,
                                contentDescription = stringResource(R.string.cd_banner_perfil),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(Modifier.weight(2f))
                }

                // Botão de Editar
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFF3B4045), CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_editar_perfil),
                        contentDescription = stringResource(R.string.cd_editar_perfil),
                        tint = Color.White
                    )
                }

                BotaoVisualizacao(
                    // Estar nesta tela = modo cliente. O radio reflete a tela
                    // atual, não usuario.isProdutor (que indica "tem loja" e
                    // ficaria preso em Produtor para contas produtoras).
                    modoAtualIsProdutor = false,
                    onModoChanged = { isProdutor ->
                        viewModel.alterarModoVisualizacao(isProdutor, uid)
                        onModoChanged(isProdutor)
                    },
                    modifier = Modifier.align(Alignment.TopEnd).offset(y = (-2).dp)
                )

                // Textos
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(bgDefault)
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = usuario.nome.ifEmpty { stringResource(R.string.usuario) },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = usuario.email.ifEmpty { stringResource(R.string.carregando) },
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.Center)
                        .offset(y = (-15).dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    if (usuario.fotoUrl.isNotBlank()) {
                        AsyncImage(
                            model = usuario.fotoUrl,
                            contentDescription = stringResource(R.string.cd_foto_perfil),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = usuario.nome.take(1).uppercase().ifBlank { "?" },
                            fontSize = 48.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção de informações
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgDefault)
                    .padding(20.dp)
            ) {
                InfoRow(
                    label = stringResource(R.string.nome),
                    value = usuario.nome.ifEmpty { stringResource(R.string.usuario) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow(
                    label = stringResource(R.string.email),
                    value = usuario.email.ifEmpty { stringResource(R.string.carregando) }
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = stringResource(R.string.configuracoes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .clickable { onSettingsClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.sair),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .clickable { onLogoutClick() }
                        .padding(8.dp) // Área de clique levemente maior
                )
            }
        }
    }
}
