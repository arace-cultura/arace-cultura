package com.aracecultura.arace.ui.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.CategoriasProduto
import com.aracecultura.arace.ui.theme.GoogleSans

@Composable
private fun TituloPasso(texto: String) {
    Text(
        text = texto,
        fontFamily = GoogleSans, fontWeight = FontWeight.Medium, fontSize = 24.sp,
        color = CorTexto, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
    )
}

/** Passo 1: dados da loja (nome, loja, tipo de pessoa). */
@Composable
fun CadastroProdutorPasso1(
    viewModel: CadastroProdutorViewModel,
    onProximo: () -> Unit,
    onVoltar: () -> Unit,
) {
    val draft = viewModel.draft.value
    var nome by remember { mutableStateOf(draft.nomeCompleto) }
    var loja by remember { mutableStateOf(draft.nomeLoja) }
    var tipo by remember { mutableStateOf(if (draft.tipoPessoa == "PJ") "PJ" else "PF") }

    fun salvar() = viewModel.atualizarDraft {
        it.copy(nomeCompleto = nome, nomeLoja = loja, tipoPessoa = tipo)
    }

    FundoAuth(comImagem = false) {
        TituloPasso(stringResource(R.string.cad_produtor_titulo))

        CampoArace(
            valor = nome, aoMudar = { nome = it },
            rotulo = stringResource(R.string.cad_nome_completo),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = loja, aoMudar = { loja = it },
            rotulo = stringResource(R.string.cad_nome_loja_empresa),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        )

        Text(
            text = stringResource(R.string.cad_voce_e),
            fontFamily = GoogleSans, fontSize = 16.sp, color = CorTexto,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            OpcaoRadio(
                texto = stringResource(R.string.cad_pessoa_fisica),
                selecionado = tipo == "PF",
                onSelecionar = { tipo = "PF" },
                modifier = Modifier.weight(1f),
            )
            OpcaoRadio(
                texto = stringResource(R.string.cad_pessoa_juridica),
                selecionado = tipo == "PJ",
                onSelecionar = { tipo = "PJ" },
                modifier = Modifier.weight(1f),
            )
        }

        BotaoArace(
            texto = stringResource(R.string.proximo),
            onClick = { salvar(); onProximo() },
        )
        LinkVoltar(onVoltar = { salvar(); onVoltar() })
    }
}

/** Passo 2: dados fiscais (razão social, CNPJ/CPF, telefone). */
@Composable
fun CadastroProdutorPasso2(
    viewModel: CadastroProdutorViewModel,
    onProximo: () -> Unit,
    onVoltar: () -> Unit,
) {
    val draft = viewModel.draft.value
    var razao by remember { mutableStateOf(draft.razaoSocial) }
    var cnpj by remember { mutableStateOf(draft.cnpj) }
    var telefone by remember { mutableStateOf(draft.telefone) }

    fun salvar() = viewModel.atualizarDraft {
        it.copy(razaoSocial = razao, cnpj = cnpj, telefone = telefone)
    }

    FundoAuth(comImagem = false) {
        TituloPasso(stringResource(R.string.cad_produtor_titulo))

        CampoArace(
            valor = razao, aoMudar = { razao = it },
            rotulo = stringResource(R.string.cad_razao_social),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = cnpj, aoMudar = { cnpj = it },
            rotulo = stringResource(R.string.cad_cnpj_cpf), teclado = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = telefone, aoMudar = { telefone = it },
            rotulo = stringResource(R.string.cad_numero_telefone), teclado = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
        )

        BotaoArace(
            texto = stringResource(R.string.proximo),
            onClick = { salvar(); onProximo() },
        )
        LinkVoltar(onVoltar = { salvar(); onVoltar() })
    }
}

/** Passo 3: detalhes da loja, imagens e finalização. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProdutorPasso3(
    viewModel: CadastroProdutorViewModel,
    onVoltar: () -> Unit,
    onConcluir: () -> Unit,
) {
    val context = LocalContext.current
    val draft = viewModel.draft.value

    var cep by remember { mutableStateOf(draft.cep) }
    var endereco by remember { mutableStateOf(draft.endereco) }
    var tipoArt by remember { mutableStateOf(draft.tipoArtesanato) }
    var categoria by remember { mutableStateOf(draft.categoriaProduto) }
    var historia by remember { mutableStateOf(draft.historia) }
    var chavePix by remember { mutableStateOf(draft.chavePix) }
    var senhaLoja by remember { mutableStateOf(viewModel.senhaLoja.value) }
    var erroSenha by remember { mutableStateOf(false) }
    var categoriaAberta by remember { mutableStateOf(false) }

    // espelhos locais das imagens (os campos no ViewModel são vars simples)
    var bannerUri by remember { mutableStateOf(viewModel.bannerUri) }
    var fotoLojaUri by remember { mutableStateOf(viewModel.fotoLojaUri) }
    var fotosHistoria by remember { mutableStateOf(viewModel.fotosHistoriaUris) }

    val bannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> bannerUri = uri; viewModel.bannerUri = uri }

    val fotoLojaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> fotoLojaUri = uri; viewModel.fotoLojaUri = uri }

    val fotosHistoriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris -> fotosHistoria = uris; viewModel.fotosHistoriaUris = uris }

    fun salvar() {
        viewModel.atualizarDraft {
            it.copy(
                cep = cep, endereco = endereco, tipoArtesanato = tipoArt,
                categoriaProduto = categoria, historia = historia, chavePix = chavePix.trim(),
            )
        }
        viewModel.atualizarSenha(senhaLoja)
    }

    // Reage ao resultado do salvamento disparado por salvarProdutor()
    LaunchedEffect(Unit) {
        viewModel.resultado.collect { resultado ->
            when (resultado) {
                is ResultadoCadastro.Sucesso -> onConcluir()
                is ResultadoCadastro.Erro ->
                    Toast.makeText(context, resultado.mensagem, Toast.LENGTH_LONG).show()
                else -> Unit
            }
        }
    }

    FundoAuth(comImagem = false) {
        TituloPasso(stringResource(R.string.cad_finalizar_titulo))

        CampoArace(
            valor = cep, aoMudar = { cep = it },
            rotulo = stringResource(R.string.cad_cep), teclado = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = endereco, aoMudar = { endereco = it },
            rotulo = stringResource(R.string.cad_endereco),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = tipoArt, aoMudar = { tipoArt = it },
            rotulo = stringResource(R.string.cad_tipo_artesanato),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )

        // Categoria: dropdown com as categorias fixas
        ExposedDropdownMenuBox(
            expanded = categoriaAberta,
            onExpandedChange = { categoriaAberta = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.cad_categoria_produto), fontFamily = GoogleSans) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaAberta) },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CorAzul, focusedLabelColor = CorAzul,
                ),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = categoriaAberta,
                onDismissRequest = { categoriaAberta = false },
            ) {
                CategoriasProduto.TODAS.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(opcao, fontFamily = GoogleSans) },
                        onClick = { categoria = opcao; categoriaAberta = false },
                    )
                }
            }
        }

        CampoArace(
            valor = historia, aoMudar = { historia = it },
            rotulo = stringResource(R.string.cad_historia_loja), umaLinha = false,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 12.dp),
        )
        CampoArace(
            valor = chavePix, aoMudar = { chavePix = it },
            rotulo = stringResource(R.string.cad_chave_pix),
            suporte = stringResource(R.string.cad_pix_helper),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        CampoArace(
            valor = senhaLoja, aoMudar = { senhaLoja = it; erroSenha = false },
            rotulo = stringResource(R.string.senha_loja), senha = true,
            suporte = if (erroSenha) stringResource(R.string.erro_senha_loja_curta)
            else stringResource(R.string.cad_senha_loja_helper),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )

        BotaoSelecionarImagem(
            texto = stringResource(R.string.selecionar_banner),
            statusTexto = if (bannerUri == null) stringResource(R.string.nenhum_banner)
            else stringResource(R.string.banner_selecionado),
            onClick = {
                bannerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        )
        BotaoSelecionarImagem(
            texto = stringResource(R.string.cad_selecionar_foto_loja),
            statusTexto = if (fotoLojaUri == null) stringResource(R.string.nenhuma_foto)
            else stringResource(R.string.foto_loja_selecionada),
            onClick = {
                fotoLojaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        )
        BotaoSelecionarImagem(
            texto = stringResource(R.string.cad_selecionar_fotos_historia),
            statusTexto = when (fotosHistoria.size) {
                0 -> stringResource(R.string.nenhuma_foto)
                1 -> stringResource(R.string.uma_foto_selecionada)
                else -> pluralStringResource(
                    R.plurals.fotos_selecionadas, fotosHistoria.size, fotosHistoria.size
                )
            },
            onClick = {
                fotosHistoriaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        )

        if (fotosHistoria.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 24.dp),
            ) {
                fotosHistoria.take(3).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                }
            }
        } else {
            Spacer(Modifier.height(12.dp))
        }

        BotaoArace(
            texto = stringResource(R.string.cad_cadastrar_produtor),
            onClick = {
                if (senhaLoja.length < 4) {
                    erroSenha = true
                    return@BotaoArace
                }
                salvar()
                viewModel.salvarProdutor(context)
            },
        )
        LinkVoltar(onVoltar = { salvar(); onVoltar() })
    }
}

@Composable
private fun OpcaoRadio(
    texto: String,
    selecionado: Boolean,
    onSelecionar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectable(selected = selecionado, onClick = onSelecionar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selecionado, onClick = onSelecionar)
        Text(texto, fontFamily = GoogleSans, fontSize = 16.sp, color = CorTexto)
    }
}

@Composable
private fun BotaoSelecionarImagem(
    texto: String,
    statusTexto: String,
    onClick: () -> Unit,
) {
    BotaoArace(
        texto = texto,
        onClick = onClick,
        larguraTotal = true,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
    )
    Text(
        text = statusTexto,
        fontFamily = GoogleSans, fontSize = 14.sp, color = CorTexto,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    )
}
