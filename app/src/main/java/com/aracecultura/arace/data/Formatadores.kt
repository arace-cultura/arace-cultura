package com.aracecultura.arace.data

object Formatadores {
    /** Mantém apenas os dígitos de [valor], limitados a [limite] caracteres. */
    fun digitos(valor: String, limite: Int): String =
        valor.filter { it.isDigit() }.take(limite)

    fun cep(valor: String): String {
        val d = digitos(valor, 8)
        return when {
            d.length <= 5 -> d
            else -> "${d.take(5)}-${d.drop(5)}"
        }
    }

    fun cpfOuCnpj(valor: String): String {
        val d = digitos(valor, 14)
        return if (d.length <= 11) {
            when {
                d.length <= 3 -> d
                d.length <= 6 -> "${d.take(3)}.${d.drop(3)}"
                d.length <= 9 -> "${d.take(3)}.${d.drop(3).take(3)}.${d.drop(6)}"
                else -> "${d.take(3)}.${d.drop(3).take(3)}.${d.drop(6).take(3)}-${d.drop(9)}"
            }
        } else {
            when {
                d.length <= 2 -> d
                d.length <= 5 -> "${d.take(2)}.${d.drop(2)}"
                d.length <= 8 -> "${d.take(2)}.${d.drop(2).take(3)}.${d.drop(5)}"
                d.length <= 12 -> "${d.take(2)}.${d.drop(2).take(3)}.${d.drop(5).take(3)}/${d.drop(8)}"
                else -> "${d.take(2)}.${d.drop(2).take(3)}.${d.drop(5).take(3)}/${d.drop(8).take(4)}-${d.drop(12)}"
            }
        }
    }

    fun telefone(valor: String): String {
        val d = digitos(valor, 11)
        return when {
            d.length <= 2 -> d
            d.length <= 6 -> "(${d.take(2)}) ${d.drop(2)}"
            d.length <= 10 -> "(${d.take(2)}) ${d.drop(2).take(4)}-${d.drop(6)}"
            else -> "(${d.take(2)}) ${d.drop(2).take(5)}-${d.drop(7)}"
        }
    }
}
