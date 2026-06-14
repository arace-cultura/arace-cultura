package com.aracecultura.arace.ui.components.produto

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.aracecultura.arace.R

@Composable
fun Avaliacao(
    avaliacao: Double = 0.0,
    modifier: Modifier = Modifier
) {
    // Garante que o valor fique estritamente entre 0.0 e 5.0
    val avaliacaoSegura = avaliacao.coerceIn(0.0, 5.0)

    // Lógica de arredondamento
    val avaliacaoArredondada = if (avaliacaoSegura >= 4.9) {
        5.0
    } else {
        // Divide por 0.5, pega a parte inteira (truncando/arredondando para baixo) e multiplica de volta
        (avaliacaoSegura / 0.5).toInt() * 0.5
    }

    // Cálculo da quantidade de cada tipo de estrela
    val estrelasCheias = avaliacaoArredondada.toInt()
    val temEstrelaMeioCheia = (avaliacaoArredondada - estrelasCheias) >= 0.5
    val estrelasVazias = 5 - estrelasCheias - (if (temEstrelaMeioCheia) 1 else 0)

    Row(modifier = modifier) {
        // Renderiza as estrelas cheias
        repeat(estrelasCheias) {
            Icon(
                painter = painterResource(id = R.drawable.ic_estrela1),
                contentDescription = stringResource(R.string.cd_estrela_cheia)
            )
        }

        // Renderiza a estrela meio-cheia, se houver
        if (temEstrelaMeioCheia) {
            Icon(
                painter = painterResource(id = R.drawable.ic_estrela0_5),
                contentDescription = stringResource(R.string.cd_estrela_meia)
            )
        }

        // Renderiza as estrelas vazias restantes para completar 5
        repeat(estrelasVazias) {
            Icon(
                painter = painterResource(id = R.drawable.ic_estrela0),
                contentDescription = stringResource(R.string.cd_estrela_vazia)
            )
        }
    }
}