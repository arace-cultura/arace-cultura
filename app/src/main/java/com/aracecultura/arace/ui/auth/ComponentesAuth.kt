package com.aracecultura.arace.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.data.CampoCadastro
import com.aracecultura.arace.ui.theme.GoogleSans

// Cores espelhadas dos recursos XML usados nas telas de auth
internal val CorBg = Color(0xFFF5F5F5)      // @color/bg
internal val CorTexto = Color(0xFF333333)   // @color/texto
internal val CorAzul = Color(0xFF2F5E91)    // @color/arace_blue
internal val CorVerde = Color(0xFF679662)   // @color/verdeArace
internal val CorLaranja = Color(0xFFD45D22) // @color/laranja

/**
 * Fundo padrão das telas de auth: cor base + imagem decorativa opcional
 * (alpha 0.15) e um conteúdo rolável com padding de 40dp, como nos XML.
 */
@Composable
internal fun FundoAuth(
    comImagem: Boolean = true,
    alinhamento: Alignment.Horizontal = Alignment.CenterHorizontally,
    arranjo: Arrangement.Vertical = Arrangement.Top,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    Box(Modifier.fillMaxSize().background(CorBg)) {
        if (comImagem) {
            Image(
                painter = painterResource(R.drawable.img_bg_explorar),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.15f),
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(40.dp),
            horizontalAlignment = alinhamento,
            verticalArrangement = arranjo,
            content = conteudo,
        )
    }
}

/** Campo de texto arredondado com borda azul; igual ao TextInputLayout dos XML. */
@Composable
internal fun CampoArace(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    senha: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    umaLinha: Boolean = true,
    suporte: String? = null,
    mascara: VisualTransformation? = null,
) {
    var visivel by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo, fontFamily = GoogleSans) },
        singleLine = umaLinha,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (senha && !visivel) KeyboardType.Password else teclado,
        ),
        visualTransformation = when {
            senha && !visivel -> PasswordVisualTransformation()
            mascara != null -> mascara
            else -> VisualTransformation.None
        },
        trailingIcon = if (senha) {
            {
                Text(
                    text = if (visivel) "Ocultar" else "Mostrar",
                    color = CorAzul,
                    fontSize = 13.sp,
                    fontFamily = GoogleSans,
                    modifier = Modifier
                        .clickable { visivel = !visivel }
                        .padding(end = 12.dp),
                )
            }
        } else null,
        supportingText = suporte?.let { texto -> { Text(texto, fontFamily = GoogleSans) } },
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CorAzul,
            focusedLabelColor = CorAzul,
        ),
        modifier = modifier,
    )
}

/** Botão preenchido padrão (azul por default). */
@Composable
internal fun BotaoArace(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cor: Color = CorAzul,
    habilitado: Boolean = true,
    larguraTotal: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(containerColor = cor, contentColor = Color.White),
        modifier = modifier,
    ) {
        Text(
            text = texto,
            fontFamily = GoogleSans,
            fontSize = 18.sp,
            modifier = if (larguraTotal) Modifier.padding(vertical = 6.dp)
            else Modifier.padding(horizontal = 40.dp, vertical = 12.dp),
        )
    }
}

/** Rótulo legível de cada campo, para listar no pop-up de dados inválidos. */
@Composable
internal fun rotuloCampo(campo: CampoCadastro): String = stringResource(
    when (campo) {
        CampoCadastro.NOME -> R.string.campo_nome
        CampoCadastro.NOME_LOJA -> R.string.campo_nome_loja
        CampoCadastro.RAZAO_SOCIAL -> R.string.campo_razao_social
        CampoCadastro.EMAIL -> R.string.campo_email
        CampoCadastro.TELEFONE -> R.string.campo_telefone
        CampoCadastro.CPF -> R.string.campo_cpf
        CampoCadastro.CNPJ -> R.string.campo_cnpj
        CampoCadastro.CEP -> R.string.campo_cep
        CampoCadastro.ENDERECO -> R.string.campo_endereco
        CampoCadastro.TIPO_ARTESANATO -> R.string.campo_tipo_artesanato
        CampoCadastro.CATEGORIA -> R.string.campo_categoria
        CampoCadastro.CHAVE_PIX -> R.string.campo_chave_pix
        CampoCadastro.SENHA -> R.string.campo_senha
        CampoCadastro.CONFIRMAR_SENHA -> R.string.campo_confirmar_senha
    }
)

/**
 * Pop-up (estilo do de logout) exibido ao final do cadastro quando algum dado é
 * inválido. Lista os campos reprovados para o usuário corrigir.
 */
@Composable
internal fun DialogoDadosInvalidos(
    campos: List<CampoCadastro>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFAF7F2),
        title = {
            Text(
                text = stringResource(R.string.dados_invalidos_titulo),
                color = Color(0xFF2E2B27),
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.dados_invalidos_msg),
                    color = Color(0xFF7A7168),
                    fontFamily = GoogleSans,
                )
                campos.forEach { campo ->
                    Text(
                        text = "•  ${rotuloCampo(campo)}",
                        color = Color(0xFF2E2B27),
                        fontFamily = GoogleSans,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dados_invalidos_ok),
                    color = Color(0xFFCE5A14),
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    )
}

/** Link "Voltar" em texto, como os TextView de voltar dos XML. */
@Composable
internal fun LinkVoltar(onVoltar: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.voltar),
        fontFamily = GoogleSans,
        fontSize = 16.sp,
        color = Color.Black,
        modifier = modifier
            .padding(top = 16.dp)
            .clickable { onVoltar() },
    )
}
