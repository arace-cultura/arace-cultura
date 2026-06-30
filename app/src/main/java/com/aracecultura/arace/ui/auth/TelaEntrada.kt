package com.aracecultura.arace.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.theme.GoogleSans

private val CorFundoEntrada = Color(0xFF0F171E)

@Composable
fun TelaEntrada(
    onCadastro: () -> Unit,
    onLogin: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(CorFundoEntrada),
    ) {

        val contentHeight = maxOf(maxHeight, 640.dp)
        val cardTop = contentHeight * 0.42f

        // Logo e logotipo escalam com a largura da tela
        val logoWidth = maxWidth * 0.22f
        val logoHeight = logoWidth * (1456f / 1509f)
        val logotipoWidth = maxWidth * 0.28f
        val logotipoHeight = logotipoWidth * (16f / 54f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight),
            ) {
                Image(
                    painter = painterResource(R.drawable.img_entrada),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(contentHeight - cardTop)
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(48.dp))
                    Text(
                        text = stringResource(R.string.entrada_slogan),
                        fontFamily = GoogleSans, fontWeight = FontWeight.Bold, fontSize = 26.sp,
                        color = CorTexto, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.842f),
                    )

                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = onCadastro,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CorAzul, contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(0.842f).height(60.dp),
                    ) {
                        Text(stringResource(R.string.entrada_criar_conta), fontFamily = GoogleSans, fontSize = 20.sp)
                    }

                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onLogin,
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, CorTexto),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CorTexto),
                        modifier = Modifier.fillMaxWidth(0.842f).height(60.dp),
                    ) {
                        Text(stringResource(R.string.entrada_entrar), fontFamily = GoogleSans, fontSize = 20.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(cardTop),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 62.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .width(logoWidth)
                                .height(logoHeight),
                        )
                        Spacer(Modifier.height(15.dp))
                        Image(
                            painter = painterResource(R.drawable.arace),
                            contentDescription = null,
                            modifier = Modifier
                                .width(logotipoWidth)
                                .height(logotipoHeight),
                        )
                    }
                }
            }
        }
    }
}
