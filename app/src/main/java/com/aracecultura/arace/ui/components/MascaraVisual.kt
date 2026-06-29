package com.aracecultura.arace.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.aracecultura.arace.data.Formatadores

/**
 * Transformação visual para campos com máscara (telefone, CPF/CNPJ, CEP).
 *
 * O estado do campo guarda **apenas os dígitos**; a máscara (parênteses, traços,
 * pontos…) é aplicada só na exibição. Como o texto editado não contém os
 * separadores, o [OffsetMapping] consegue posicionar o cursor corretamente e ele
 * nunca "pula" ao digitar — que era o defeito da formatação feita direto no
 * `onValueChange`.
 *
 * [formatar] recebe os dígitos crus e devolve a string formatada (ex.:
 * [Formatadores.telefone]).
 */
private class MascaraVisual(
    private val formatar: (String) -> String,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val cru = text.text
        val formatado = formatar(cru)

        // Quantos dígitos existem antes de cada posição do texto formatado.
        // Os separadores não contam, então o índice no texto cru é exatamente
        // a contagem de dígitos acumulada.
        val digitosAntes = IntArray(formatado.length + 1)
        var contador = 0
        formatado.forEachIndexed { i, c ->
            digitosAntes[i] = contador
            if (c.isDigit()) contador++
        }
        digitosAntes[formatado.length] = contador

        val mapeamento = object : OffsetMapping {
            // Cursor no texto cru -> posição no formatado: a posição mais à
            // direita que ainda tem exatamente `offset` dígitos antes dela, de
            // modo que o cursor fique depois dos separadores e o próximo dígito
            // seja anexado sem retroceder.
            override fun originalToTransformed(offset: Int): Int {
                val alvo = offset.coerceIn(0, contador)
                for (i in formatado.length downTo 0) {
                    if (digitosAntes[i] == alvo) return i
                }
                return formatado.length
            }

            override fun transformedToOriginal(offset: Int): Int =
                digitosAntes[offset.coerceIn(0, formatado.length)]
        }

        return TransformedText(AnnotatedString(formatado), mapeamento)
    }
}

/** Máscara de telefone: `(DD) NNNNN-NNNN`. Estado guarda só os dígitos. */
val MascaraTelefone: VisualTransformation = MascaraVisual(Formatadores::telefone)

/** Máscara de CPF/CNPJ; alterna o formato conforme a quantidade de dígitos. */
val MascaraCpfCnpj: VisualTransformation = MascaraVisual(Formatadores::cpfOuCnpj)

/** Máscara de CEP: `NNNNN-NNN`. Estado guarda só os dígitos. */
val MascaraCep: VisualTransformation = MascaraVisual(Formatadores::cep)
