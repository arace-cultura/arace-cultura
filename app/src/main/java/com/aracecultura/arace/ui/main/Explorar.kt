package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.main.jetpack.theme.ComposecriarprodutoTheme


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.aracecultura.arace.ui.main.jetpack.components.ButtonFilter
import com.aracecultura.arace.ui.main.jetpack.components.ButtonMoreCustomizable
import com.aracecultura.arace.ui.main.jetpack.components.Categorias
import com.aracecultura.arace.ui.main.jetpack.components.CalculadoraCategoria

class Explorar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            // Define a estratégia de descarte da composição
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                // Seu tema e código Compose aqui
                ComposecriarprodutoTheme {
                    Tela()
                }
            }
        }
    }
}

@Composable
@Preview
fun Tela() {
    val libreCaslonDisplay = FontFamily(Font(R.font.librecaslondisplay_regular))
    val bgColor = colorResource(id = R.color.bgColor)
    val btColor = colorResource(id = R.color.bt_default)
    var textName by remember { mutableStateOf("") }
    var textDesc1 by remember { mutableStateOf("") }
    var textDesc2 by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
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
                .padding(2.dp, 70.dp)
                .verticalScroll(scrollState)

        ) {
            var mostrarCategorias by remember { mutableStateOf(false) }
            Row(modifier = Modifier.fillMaxWidth() .height(40.dp)) {
                ButtonFilter(
                    text = "Filtros",
                    onClick = { mostrarCategorias = !mostrarCategorias }
                )
            }

            AnimatedVisibility(
                visible = ButtonFilter.statusCategoriasAbertas,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Categorias(
                        categorias = listOf("Têxteis", "Cerâmica", "Entalhe"),
                    ) { categoria ->
                        val larguraDinamica by remember(categoria) {
                            mutableStateOf(CalculadoraCategoria.calcularLargura(categoria))
                        }

                        ButtonMoreCustomizable(
                            text = categoria,
                            textColor = btColor,
                            containerColor = bgColor,
                            borderColor = btColor,
                            widthButton = larguraDinamica,
                            heightButton = 40.0,
                            modifier = Modifier,
                            onClick = { println("Clicou em $categoria") }
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(50.dp)
                    .background(bgColor)

            ) {
                Text(
                    "Descubra",
                    Modifier.width(200.dp),
                    fontFamily = libreCaslonDisplay,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            }


        }
    }
}

