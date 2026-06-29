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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.data.CampoCadastro
import com.aracecultura.arace.data.Formatadores
import com.aracecultura.arace.data.Validacoes
import com.aracecultura.arace.ui.auth.DialogoDadosInvalidos
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.components.MascaraCep
import com.aracecultura.arace.ui.components.MascaraCpfCnpj
import com.aracecultura.arace.ui.components.MascaraTelefone
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
fun EditarPerfilUsuario(
    uid: String,
    viewModel: PerfilViewModel = viewModel(),
    onVoltarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val mensagemPreenchaSenhas = stringResource(R.string.preencha_senhas)
    val mensagemSenhaAtualIncorreta = stringResource(R.string.senha_atual_incorreta)
    val mensagemSenhaFraca = stringResource(R.string.senha_forte_helper)
    val usuario by viewModel.usuario.collectAsState()
    val produtor by viewModel.produtor.collectAsState()
    val scrollState = rememberScrollState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var nomeInput by remember { mutableStateOf("") }
    var telefoneInput by remember { mutableStateOf("") }
    var telefonePreenchido by remember { mutableStateOf(false) }
    var camposInvalidos by remember { mutableStateOf<List<CampoCadastro>>(emptyList()) }
    var novaFotoUri by remember { mutableStateOf<Uri?>(null) }
    var novoBannerUri by remember { mutableStateOf<Uri?>(null) }
    var produtorPreenchido by remember { mutableStateOf(false) }
    var nomeCompletoProdutorInput by remember { mutableStateOf("") }
    var nomeLojaInput by remember { mutableStateOf("") }
    var tipoPessoaInput by remember { mutableStateOf("") }
    var razaoSocialInput by remember { mutableStateOf("") }
    var cnpjInput by remember { mutableStateOf("") }
    var telefoneLojaInput by remember { mutableStateOf("") }
    var cepInput by remember { mutableStateOf("") }
    var enderecoInput by remember { mutableStateOf("") }
    var tipoArtesanatoInput by remember { mutableStateOf("") }
    var categoriaProdutoInput by remember { mutableStateOf("") }
    var historiaInput by remember { mutableStateOf("") }
    var chavePixInput by remember { mutableStateOf("") }
    var novoBannerLojaUri by remember { mutableStateOf<Uri?>(null) }
    var novaFotoLojaUri by remember { mutableStateOf<Uri?>(null) }
    var novasFotosHistoriaUris by remember { mutableStateOf(List<Uri?>(3) { null }) }
    var fotoHistoriaSelecionada by remember { mutableStateOf(0) }
    var erroSalvarProdutor by remember { mutableStateOf<String?>(null) }

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

    val bannerLojaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novoBannerLojaUri = uri }

    val fotoLojaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) novaFotoLojaUri = uri }

    val historiaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            novasFotosHistoriaUris = novasFotosHistoriaUris.toMutableList().also {
                it[fotoHistoriaSelecionada] = uri
            }
        }
    }

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
        // Pré-preenche o telefone uma vez (sem sobrescrever a edição em curso).
        if (!telefonePreenchido && usuario.telefone.isNotEmpty()) {
            telefoneInput = Formatadores.digitos(usuario.telefone, 11)
            telefonePreenchido = true
        }
    }

    LaunchedEffect(produtor?.uid) {
        val dados = produtor ?: return@LaunchedEffect
        if (!produtorPreenchido) {
            nomeCompletoProdutorInput = dados.nomeCompleto
            nomeLojaInput = dados.nomeLoja
            tipoPessoaInput = dados.tipoPessoa
            razaoSocialInput = dados.razaoSocial
            cnpjInput = Formatadores.digitos(dados.cnpj, 14)
            telefoneLojaInput = Formatadores.digitos(dados.telefone, 11)
            cepInput = Formatadores.digitos(dados.cep, 8)
            enderecoInput = dados.endereco
            tipoArtesanatoInput = dados.tipoArtesanato
            categoriaProdutoInput = dados.categoriaProduto
            historiaInput = dados.historia
            chavePixInput = dados.chavePix
            produtorPreenchido = true
        }
    }

    if (camposInvalidos.isNotEmpty()) {
        DialogoDadosInvalidos(campos = camposInvalidos, onDismiss = { camposInvalidos = emptyList() })
    }

    fun salvarProdutorOuVoltar() {
        if (produtor == null) {
            onVoltarClick()
            return
        }
        viewModel.salvarEdicaoProdutor(
            context = context,
            uid = uid,
            nomeCompleto = nomeCompletoProdutorInput,
            nomeLoja = nomeLojaInput,
            tipoPessoa = tipoPessoaInput,
            razaoSocial = razaoSocialInput,
            cnpj = Formatadores.cpfOuCnpj(cnpjInput),
            telefone = Formatadores.telefone(telefoneLojaInput),
            cep = Formatadores.cep(cepInput),
            endereco = enderecoInput,
            tipoArtesanato = tipoArtesanatoInput,
            categoriaProduto = categoriaProdutoInput,
            historia = historiaInput,
            chavePix = chavePixInput,
            novoBannerUri = novoBannerLojaUri,
            novaFotoLojaUri = novaFotoLojaUri,
            novasFotosHistoriaUris = novasFotosHistoriaUris,
            onSucesso = onVoltarClick,
            onErro = { erroSalvarProdutor = it }
        )
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
                            contentDescription = stringResource(R.string.cd_banner_perfil),
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
                        contentDescription = stringResource(R.string.cd_alterar_banner),
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
                        contentDescription = stringResource(R.string.voltar),
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
                            contentDescription = stringResource(R.string.cd_foto_perfil),
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
                        contentDescription = stringResource(R.string.cd_alterar_foto_perfil),
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
                    text = stringResource(R.string.alterar_informacoes),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )

                OutlinedTextField(
                    value = nomeInput,
                    onValueChange = { nomeInput = it },
                    label = { Text(stringResource(R.string.label_nome_completo)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = telefoneInput,
                    onValueChange = { telefoneInput = Formatadores.digitos(it, 11) },
                    label = { Text(stringResource(R.string.telefone)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = MascaraTelefone,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Campo de E-mail Desabilitado
                OutlinedTextField(
                    value = usuario.email,
                    onValueChange = {},
                    enabled = false,
                    label = { Text(stringResource(R.string.label_email_nao_alteravel)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                produtor?.let { dadosProdutor ->
                    Text(
                        text = stringResource(R.string.mdados_cadastro_produtor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ImagemEditavel(
                            label = stringResource(R.string.selecionar_banner),
                            model = novoBannerLojaUri ?: dadosProdutor.banner.ifBlank { null },
                            onClick = {
                                bannerLojaPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ImagemEditavel(
                            label = stringResource(R.string.cad_selecionar_foto_loja),
                            model = novaFotoLojaUri ?: dadosProdutor.fotoLoja.ifBlank { null },
                            onClick = {
                                fotoLojaPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = stringResource(R.string.cad_selecionar_fotos_historia),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { index ->
                            ImagemEditavel(
                                label = stringResource(
                                    when (index) {
                                        0 -> R.string.cad_foto_historia_1
                                        1 -> R.string.cad_foto_historia_2
                                        else -> R.string.cad_foto_historia_3
                                    }
                                ),
                                model = novasFotosHistoriaUris[index]
                                    ?: dadosProdutor.fotosHistoria.getOrNull(index)?.ifBlank { null },
                                onClick = {
                                    fotoHistoriaSelecionada = index
                                    historiaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = nomeCompletoProdutorInput,
                        onValueChange = { nomeCompletoProdutorInput = it },
                        label = { Text(stringResource(R.string.cad_nome_completo)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = nomeLojaInput,
                        onValueChange = { nomeLojaInput = it },
                        label = { Text(stringResource(R.string.cad_nome_loja_empresa)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = tipoPessoaInput,
                        onValueChange = { tipoPessoaInput = it },
                        label = { Text(stringResource(R.string.cad_voce_e)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = razaoSocialInput,
                        onValueChange = { razaoSocialInput = it },
                        label = { Text(stringResource(R.string.cad_razao_social)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = cnpjInput,
                        onValueChange = { cnpjInput = Formatadores.digitos(it, 14) },
                        label = { Text(stringResource(R.string.cad_cnpj_cpf)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = MascaraCpfCnpj,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = telefoneLojaInput,
                        onValueChange = { telefoneLojaInput = Formatadores.digitos(it, 11) },
                        label = { Text(stringResource(R.string.cad_numero_telefone)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        visualTransformation = MascaraTelefone,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = cepInput,
                        onValueChange = { cepInput = Formatadores.digitos(it, 8) },
                        label = { Text(stringResource(R.string.cad_cep)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = MascaraCep,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = enderecoInput,
                        onValueChange = { enderecoInput = it },
                        label = { Text(stringResource(R.string.cad_endereco)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = tipoArtesanatoInput,
                        onValueChange = { tipoArtesanatoInput = it },
                        label = { Text(stringResource(R.string.cad_tipo_artesanato)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = categoriaProdutoInput,
                        onValueChange = { categoriaProdutoInput = it },
                        label = { Text(stringResource(R.string.cad_categoria_produto)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = chavePixInput,
                        onValueChange = { chavePixInput = it },
                        label = { Text(stringResource(R.string.cad_chave_pix)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = historiaInput,
                        onValueChange = { historiaInput = it },
                        label = { Text(stringResource(R.string.cad_historia_loja)) },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    erroSalvarProdutor?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Troca de senha da loja — só para contas produtoras
                if (produtor != null) {
                    Text(
                        text = stringResource(R.string.senha_loja),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = senhaAtual,
                            onValueChange = { senhaAtual = it; erroSenha = null },
                            label = { Text(stringResource(R.string.label_senha_atual)) },
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
                        label = { Text(stringResource(R.string.label_nova_senha)) },
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
                    text = stringResource(R.string.salvar_alteracoes),
                    onClick = {
                        erroSenha = null
                        val querTrocarSenha = senhaAtual.isNotBlank() || senhaNova.isNotBlank()
                        when {
                            !Validacoes.telefoneValido(telefoneInput) -> {
                                camposInvalidos = listOf(CampoCadastro.TELEFONE)
                            }
                            querTrocarSenha && (senhaAtual.isBlank() || senhaNova.isBlank()) -> {
                                erroSenha = mensagemPreenchaSenhas
                            }
                            querTrocarSenha && !Validacoes.senhaForte(senhaNova) -> {
                                erroSenha = mensagemSenhaFraca
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
                                            novoTelefone = Formatadores.telefone(telefoneInput),
                                            novaFotoUri = novaFotoUri,
                                            novoBannerUri = novoBannerUri,
                                            onSucesso = { salvarProdutorOuVoltar() }
                                        )
                                    },
                                    onSenhaIncorreta = { erroSenha = mensagemSenhaAtualIncorreta },
                                    onErro = { erroSenha = it }
                                )
                            }
                            else -> {
                                viewModel.salvarEdicaoPerfil(
                                    context = context,
                                    novoNome = nomeInput,
                                    uid = uid,
                                    novoTelefone = Formatadores.telefone(telefoneInput),
                                    novaFotoUri = novaFotoUri,
                                    novoBannerUri = novoBannerUri,
                                    onSucesso = { salvarProdutorOuVoltar() }
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

@Composable
private fun ImagemEditavel(
    label: String,
    model: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.24f))
            )
            Image(
                painter = painterResource(R.drawable.ic_editar_imagem),
                contentDescription = label,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF3B4045),
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
