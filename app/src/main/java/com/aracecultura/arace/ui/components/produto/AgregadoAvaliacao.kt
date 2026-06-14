package com.aracecultura.arace.ui.components.produto

data class AgregadoAvaliacao(
    val soma: Double,
    val quantidade: Int,
    val media: Double
)

fun calcularAgregadoAvaliacao(
    somaAtual: Double,
    quantidadeAtual: Int,
    notaAnterior: Int?,
    novaNota: Int
): AgregadoAvaliacao {
    require(novaNota in 1..5)

    val novaSoma = if (notaAnterior == null) {
        somaAtual + novaNota
    } else {
        somaAtual - notaAnterior + novaNota
    }
    val novaQuantidade = if (notaAnterior == null) {
        quantidadeAtual + 1
    } else {
        quantidadeAtual
    }

    return AgregadoAvaliacao(
        soma = novaSoma,
        quantidade = novaQuantidade,
        media = if (novaQuantidade == 0) 0.0 else novaSoma / novaQuantidade
    )
}
