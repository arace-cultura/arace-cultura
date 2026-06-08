package com.aracecultura.arace.ui.components.criar

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.components.AppButton
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarProduto(
    viewModel: ProdutoViewModel = viewModel()
) {
    val context = LocalContext.current
    val libreCaslonDisplay = FontFamily(Font(R.font.librecaslondisplay_regular))

    var textName by remember { mutableStateOf("") }
    var textDesc1 by remember { mutableStateOf("") }
    var textPreco by remember { mutableStateOf("") }

    val categorias = listOf("Artesanato", "Têxteis", "Cosméticos", "Casa", "Cerâmica", "Acessórios")
    var expandedCategoria by remember { mutableStateOf(false) }
    var selectedCategoria by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) { selectedImageUri = uri }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProdutoUiState.Success -> {
                Toast.makeText(context, (uiState as ProdutoUiState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                selectedImageUri = null
                textName = ""
                textDesc1 = ""
                textPreco = ""
                selectedCategoria = ""
            }
            is ProdutoUiState.Error -> {
                Toast.makeText(context, (uiState as ProdutoUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDefault)

    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bg_explorar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                "Adicionar Produto",
                Modifier.width(200.dp),
                fontFamily = libreCaslonDisplay,
                fontSize = 50.sp,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(50.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight()
                    .background(bgDefault)
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Imagem selecionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.83f)
                                .aspectRatio(348f / 284f)
                                .clip(RoundedCornerShape(15.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.83f)
                                .aspectRatio(348f / 284f)
                                .clip(RoundedCornerShape(15.dp))
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ButtonCustomized(
                            text = if (selectedImageUri == null) "escolher imagem" else "trocar imagem",
                            textColor = Color.Black,
                            containerColor = bgDefault,
                            borderColor = Color.Black
                        ) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                FixedTextField(
                    text = textName,
                    onTextChange = { textName = it },
                    placeholder = { Text("Nome", color = bgDefault) },
                    unfocusedContainerColor = btColor,
                    focusedContainerColor = btColor,
                    unfocusedBorderColor = btColor,
                    focusedBorderColor = btColor,
                    unfocusedTextColor = bgDefault,
                    focusedTextColor = bgDefault,
                    modifier = Modifier
                )
                Spacer(Modifier.height(20.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = selectedCategoria,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Categoria", color = btColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier
                            .fillMaxWidth(0.83f)
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = bgDefault,
                            unfocusedContainerColor = bgDefault,
                            focusedBorderColor = btColor,
                            unfocusedBorderColor = btColor,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    selectedCategoria = categoria
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                ExpandingTextField(
                    text = textDesc1,
                    onTextChange = { textDesc1 = it },
                    placeholder = { Text("Descrição", color = btColor) },
                    unfocusedContainerColor = bgDefault,
                    focusedContainerColor = bgDefault,
                    unfocusedBorderColor = btColor,
                    focusedBorderColor = btColor,
                    modifier = Modifier,
                )

                Spacer(Modifier.height(20.dp))

                FixedTextField(
                    text = textPreco,
                    onTextChange = { novoPreco ->
                        if (novoPreco.isEmpty() || novoPreco.matches(Regex("^\\d*[,.]?\\d*\$"))) {
                            textPreco = novoPreco
                        }
                    },
                    placeholder = { Text("Preço (R$)", color = btColor) },
                    focusedContainerColor = bgDefault,
                    unfocusedContainerColor = bgDefault,
                    focusedBorderColor = btColor,
                    unfocusedBorderColor = btColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    modifier = Modifier.fillMaxWidth(0.83f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )

                Spacer(Modifier.height(20.dp))

                // --- NOVO: Botão Adicionar reativo ao estado ---
                if (uiState is ProdutoUiState.Loading) {
                    CircularProgressIndicator(color = btColor)
                } else {
                    AppButton(
                        text = "Adicionar",
                        textColor = bgDefault,
                        containerColor = btColor,
                        borderColor = btColor,
                        onClick = {
                            if (selectedImageUri == null || textName.isEmpty() || selectedCategoria.isEmpty() || textPreco.isEmpty()) {
                                Toast.makeText(context, "Preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.salvarProduto(
                                    context = context,
                                    imageUri = selectedImageUri!!,
                                    nome = textName,
                                    categoria = selectedCategoria,
                                    descricao = textDesc1,
                                    precoStr = textPreco
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.472f)
                            .aspectRatio(348f / 90f)
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}