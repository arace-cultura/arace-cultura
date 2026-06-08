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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans
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
                    .weight(2f)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(3f)
                            .background(Color(0xFFD66027))
                    )
                    Spacer(Modifier.weight(4f))
                }

                // Botão de Editar
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFF3B4045), CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_editar_perfil),
                        contentDescription = "Editar Perfil",
                        tint = Color.White
                    )
                }

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
                        text = usuario.nome.ifEmpty { "Usuário" },
                        fontFamily = GoogleSans,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = usuario.email.ifEmpty { "carregando..." },
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.Center)
                        .offset(y = (-15).dp)
                        .background(Color.Gray, CircleShape)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção de informações
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .background(bgDefault)
                    .padding(20.dp)
            ) {
                InfoRow(label = "Nome", value = usuario.nome.ifEmpty { "Usuário" })
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow(label = "Email", value = usuario.email.ifEmpty { "carregando..." })
                Spacer(modifier = Modifier.height(32.dp))

                // Linha Modo de Visualização (Com RadioButtons alinhados e clicáveis na linha toda)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Modo de visualização",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 12.dp) // Alinha texto com o primeiro RadioButton
                    )

                    Column(horizontalAlignment = Alignment.Start) {

                        // Radio: Cliente
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    viewModel.alterarModoVisualizacao(false, uid)
                                    onModoChanged(false)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            // MinimumInteractiveComponentSize ajuda a alinhar perfeitamente sem margens invisíveis indesejadas
                            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                RadioButton(
                                    selected = !usuario.isProdutor,
                                    onClick = null, // Deixe nulo pois o Row lida com o clique
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFD66027),
                                        unselectedColor = Color(0xFFD66027)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cliente", fontSize = 18.sp, color = Color(0xFF4A5568))
                        }

                        // Radio: Produtor
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    viewModel.alterarModoVisualizacao(true, uid)
                                    onModoChanged(true)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                RadioButton(
                                    selected = usuario.isProdutor,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFFD66027),
                                        unselectedColor = Color(0xFFD66027)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Produtor", fontSize = 18.sp, color = Color(0xFF4A5568))
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "Configurações",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .clickable { onSettingsClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÃO SAIR ---
                Text(
                    text = "Sair",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .clickable { onLogoutClick() }
                        .padding(8.dp) // Área de clique levemente maior
                )
            }
        }
    }
}
