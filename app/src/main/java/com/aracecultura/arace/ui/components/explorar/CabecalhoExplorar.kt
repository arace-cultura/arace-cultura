package com.aracecultura.arace.ui.components.explorar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans
import com.aracecultura.arace.ui.theme.bgDefault
import com.aracecultura.arace.ui.theme.btColor

@Composable
internal fun CabecalhoExplorar(viewmodel: ExplorarProdutoViewmodel) {
    var buscaAberta by rememberSaveable { mutableStateOf(false) }
    val textoBusca by viewmodel.textoBusca.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val teclado = LocalSoftwareKeyboardController.current

    fun fecharBusca() {
        viewmodel.setTextoBusca("")
        buscaAberta = false
        teclado?.hide()
        focusManager.clearFocus()
    }

    BackHandler(enabled = buscaAberta, onBack = ::fecharBusca)

    LaunchedEffect(buscaAberta) {
        if (buscaAberta) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(bgDefault),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = !buscaAberta,
            enter = fadeIn(animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(120))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.explorar_titulo),
                    fontFamily = GoogleSans,
                    fontSize = 36.sp
                )
                IconButton(onClick = { buscaAberta = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_explorar_produto_outline),
                        contentDescription = stringResource(R.string.abrir_busca),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = buscaAberta,
            enter = expandHorizontally(
                expandFrom = Alignment.CenterHorizontally,
                animationSpec = tween(220)
            ) + fadeIn(animationSpec = tween(180)),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.CenterHorizontally,
                animationSpec = tween(180)
            ) + fadeOut(animationSpec = tween(120))
        ) {
            BasicTextField(
                value = textoBusca,
                onValueChange = viewmodel::setTextoBusca,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontFamily = GoogleSans,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(btColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        teclado?.hide()
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
                decorationBox = { campoTexto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(1.dp, btColor, RoundedCornerShape(24.dp))
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = ::fecharBusca) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_left),
                                contentDescription = stringResource(R.string.fechar_busca),
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (textoBusca.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.buscar_produtos),
                                    color = Color.Gray,
                                    fontFamily = GoogleSans,
                                    fontSize = 18.sp
                                )
                            }
                            campoTexto()
                        }
                        Icon(
                            painter = painterResource(R.drawable.ic_explorar_produto_outline),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .size(24.dp)
                        )
                    }
                }
            )
        }
    }
}
