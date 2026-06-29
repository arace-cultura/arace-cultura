package com.aracecultura.arace.ui.components.criar

import org.junit.Assert.assertEquals
import org.junit.Test

class NomeProdutoTest {

    @Test
    fun limitarNomeProdutoDigitado_removeQuebrasELimitaTamanho() {
        val nome = "Canecas!\n".repeat(10)

        val resultado = limitarNomeProdutoDigitado(nome)

        assertEquals(LIMITE_NOME_PRODUTO, resultado.length)
        assertEquals(false, resultado.contains('\n'))
    }

    @Test
    fun normalizarNomeProduto_removeEspacosExcedentes() {
        val resultado = normalizarNomeProduto("  Caneca   de\nbarro  ")

        assertEquals("Caneca de barro", resultado)
    }
}
