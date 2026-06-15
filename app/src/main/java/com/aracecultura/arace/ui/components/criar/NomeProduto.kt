package com.aracecultura.arace.ui.components.criar

const val LIMITE_NOME_PRODUTO = 40

fun limitarNomeProdutoDigitado(nome: String): String =
    nome.replace(Regex("[\\r\\n]+"), " ")
        .take(LIMITE_NOME_PRODUTO)

fun normalizarNomeProduto(nome: String): String =
    limitarNomeProdutoDigitado(nome)
        .replace(Regex("\\s+"), " ")
        .trim()
