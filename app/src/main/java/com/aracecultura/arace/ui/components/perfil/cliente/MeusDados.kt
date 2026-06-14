package com.aracecultura.arace.ui.components.perfil.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun MeusDados(
    uid: String,
    viewModel: PerfilViewModel = viewModel(),
    onVoltarClick: () -> Unit = {},
    onEditarClick: () -> Unit = {}
) {
    val usuario by viewModel.usuario.collectAsState()
    val produtor by viewModel.produtor.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            viewModel.carregarDadosUsuario(uid)
            viewModel.carregarDadosProdutor(uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
            .verticalScroll(scrollState)
    ) {
        // Header no padrão da TelaConfiguracoes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVoltarClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.voltar),
                        tint = Color.DarkGray
                    )
                }
            }

            Text(
                text = stringResource(R.string.meus_dados),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecaoDados(titulo = stringResource(R.string.mdados_conta)) {
                InfoRow(label = stringResource(R.string.nome), value = usuario.nome.ifBlank { "—" })
                InfoRow(label = stringResource(R.string.e_mail), value = usuario.email.ifBlank { "—" })
            }

            produtor?.let { dados ->
                SecaoDados(titulo = stringResource(R.string.mdados_cadastro_produtor)) {
                    InfoRow(label = stringResource(R.string.mdados_loja), value = dados.nomeLoja.ifBlank { "—" })
                    if (dados.razaoSocial.isNotBlank()) {
                        InfoRow(label = stringResource(R.string.mdados_razao_social), value = dados.razaoSocial)
                    }
                    if (dados.cnpj.isNotBlank()) {
                        InfoRow(label = stringResource(R.string.mdados_cnpj), value = dados.cnpj)
                    }
                    InfoRow(label = stringResource(R.string.mdados_telefone), value = dados.telefone.ifBlank { "—" })
                    InfoRow(label = stringResource(R.string.cad_cep), value = dados.cep.ifBlank { "—" })
                    InfoRow(label = stringResource(R.string.cad_endereco), value = dados.endereco.ifBlank { "—" })
                    InfoRow(label = stringResource(R.string.mdados_artesanato), value = dados.tipoArtesanato.ifBlank { "—" })
                    InfoRow(label = stringResource(R.string.mdados_categoria), value = dados.categoriaProduto.ifBlank { "—" })
                }
            }

            AppButton(
                text = stringResource(R.string.editar_dados),
                onClick = onEditarClick,
                containerColor = btColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SecaoDados(
    titulo: String,
    conteudo: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFF7F7F7))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        conteudo()
    }
}
