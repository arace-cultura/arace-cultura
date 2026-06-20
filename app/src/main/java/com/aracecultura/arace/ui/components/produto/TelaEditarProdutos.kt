package com.aracecultura.arace.ui.components.produto

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import java.util.Locale

private val Laranja = Color(0xFFD45D22)
private val CinzaPlaceholder = Color(0xFFD9D9D9)

@Composable
fun TelaEditarProdutos(
    uid: String,
    onBack: () -> Unit = {},
    viewModel: EditarProdutosViewModel = viewModel()
) {
    val context = LocalContext.current
    val produtos by viewModel.produtos.collectAsState()
    val carregando by viewModel.carregando.collectAsState()
    val mensagem by viewModel.mensagem.collectAsState()

    LaunchedEffect(uid) { viewModel.carregar() }

    LaunchedEffect(mensagem) {
        mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.limparMensagem()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = stringResource(R.string.voltar),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(R.string.editar_produtos),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center).padding(vertical = 16.dp)
            )
        }

        when {
            carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.carregando), color = Color.Gray)
            }
            produtos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.editar_produtos_vazio),
                    color = Color.Gray,
                    modifier = Modifier.padding(24.dp)
                )
            }
            // spacedBy participa do recálculo de layout durante foco/IME e
            // causa jitter ao digitar (mesmo padrão visto no carrinho); o
            // espaçamento vai como padding por item.
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(items = produtos, key = { it.id }) { produto ->
                    Box(modifier = Modifier.padding(bottom = 24.dp)) {
                        ProdutoEditavelCard(
                            produto = produto,
                            onSalvar = { nome, descricao, preco, imagemUri ->
                                viewModel.salvar(context, produto.id, nome, descricao, preco, imagemUri)
                            },
                            onExcluir = { viewModel.excluir(produto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProdutoEditavelCard(
    produto: Produto,
    onSalvar: (nome: String, descricao: String, preco: String, imagemUri: Uri?) -> Unit,
    onExcluir: () -> Unit
) {
    var nome by remember(produto.id) { mutableStateOf(produto.nome) }
    var descricao by remember(produto.id) { mutableStateOf(produto.descricao) }
    var preco by remember(produto.id) {
        mutableStateOf(String.format(Locale("pt", "BR"), "%.2f", produto.preco))
    }
    var imagemUri by remember(produto.id) { mutableStateOf<Uri?>(null) }
    var descricaoExpandida by remember(produto.id) { mutableStateOf(false) }
    var showExcluirDialog by remember(produto.id) { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) imagemUri = uri }

    val cores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Laranja,
        unfocusedBorderColor = Laranja,
        cursorColor = Laranja
    )

    if (showExcluirDialog) {
        AlertDialog(
            onDismissRequest = { showExcluirDialog = false },
            containerColor = Color(0xFFFAF7F2),
            title = {
                Text(
                    text = stringResource(R.string.excluir_produto_titulo),
                    color = Color(0xFF2E2B27),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.excluir_produto_msg),
                    color = Color(0xFF7A7168)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExcluirDialog = false
                        onExcluir()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.sim_excluir_produto),
                        color = Color(0xFFCE5A14),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExcluirDialog = false }) {
                    Text(stringResource(R.string.voltar), color = Color(0xFF7A7168))
                }
            }
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        // Coluna esquerda: imagem + ações
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CinzaPlaceholder)
                    .clickable {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val model: Any? = imagemUri ?: produto.imagens.firstOrNull()
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = stringResource(R.string.cd_imagem_produto),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Scrim 0.3 + ícone de editar imagem
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Icon(
                    painter = painterResource(R.drawable.ic_editar_imagem),
                    contentDescription = stringResource(R.string.cd_trocar_imagem),
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Excluir produto (contorno + lixeira ic_deletar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                    .clickable { showExcluirDialog = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_deletar),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.excluir_produto), fontSize = 14.sp, color = Color.Black)
            }

            // Salvar edições (preenchido laranja)
            AppButton(
                text = stringResource(R.string.salvar_edicoes),
                onClick = { onSalvar(nome, descricao, preco, imagemUri) },
                textColor = Color.White,
                containerColor = Laranja,
                shape = RoundedCornerShape(50),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().height(44.dp)
            )
        }

        // Coluna direita: campos
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = cores,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                shape = RoundedCornerShape(12.dp),
                colors = cores,
                maxLines = if (descricaoExpandida) Int.MAX_VALUE else 3,
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_down),
                        contentDescription = if (descricaoExpandida) stringResource(R.string.cd_recolher) else stringResource(R.string.cd_expandir),
                        tint = Color.Gray,
                        modifier = Modifier
                            .clickable { descricaoExpandida = !descricaoExpandida }
                            .rotate(if (descricaoExpandida) 180f else 0f)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = preco,
                onValueChange = { preco = it },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = cores,
                prefix = { Text(stringResource(R.string.prefixo_real)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
