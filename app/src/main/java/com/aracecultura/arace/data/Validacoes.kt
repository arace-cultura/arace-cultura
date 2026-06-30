package com.aracecultura.arace.data

/**
 * Campos validados nos fluxos de cadastro. O pop-up de "dados inválidos" recebe
 * a lista de campos reprovados e mostra o rótulo de cada um (mapeado para string
 * na camada de UI), para o usuário saber exatamente o que corrigir.
 */
enum class CampoCadastro {
    NOME, NOME_LOJA, RAZAO_SOCIAL, EMAIL, TELEFONE, CPF, CNPJ, CEP,
    ENDERECO, TIPO_ARTESANATO, CATEGORIA, CHAVE_PIX, SENHA, CONFIRMAR_SENHA
}

/**
 * Validações puras (sem dependência de Android) usadas em todos os fluxos de
 * auth. Tudo o que é verificável localmente é checado aqui; o CEP, que depende
 * de consulta externa, fica em [CepRepository].
 */
object Validacoes {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private fun apenasDigitos(valor: String) = valor.filter { it.isDigit() }

    /** Texto obrigatório simples (ex.: endereço, categoria, chave Pix). */
    fun preenchido(valor: String, minimo: Int = 2): Boolean = valor.trim().length >= minimo

    /** Nome: ao menos 2 caracteres, só letras e separadores comuns. */
    fun nomeValido(nome: String): Boolean {
        val limpo = nome.trim()
        return limpo.length >= 2 &&
            limpo.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' }
    }

    /** Nome completo: além de [nomeValido], exige nome e sobrenome. */
    fun nomeCompletoValido(nome: String): Boolean {
        if (!nomeValido(nome)) return false
        val partes = nome.trim().split(Regex("\\s+"))
        return partes.size >= 2 && partes.all { it.isNotBlank() }
    }

    fun emailValido(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    /** Telefone BR: 10 (fixo) ou 11 (celular) dígitos; DDD plausível; celular começa com 9. */
    fun telefoneValido(telefone: String): Boolean {
        val d = apenasDigitos(telefone)
        if (d.length != 10 && d.length != 11) return false
        val ddd = d.substring(0, 2).toInt()
        if (ddd < 11) return false
        if (d.length == 11 && d[2] != '9') return false
        return true
    }

    /** Apenas o formato (8 dígitos); a existência é confirmada por [CepRepository]. */
    fun cepFormatoValido(cep: String): Boolean = apenasDigitos(cep).length == 8

    /** CPF com dígitos verificadores válidos (módulo 11). */
    fun cpfValido(cpf: String): Boolean {
        val d = apenasDigitos(cpf)
        if (d.length != 11 || d.all { it == d[0] }) return false
        val nums = d.map { it - '0' }
        for (j in 9..10) {
            var soma = 0
            for (i in 0 until j) soma += nums[i] * (j + 1 - i)
            val dig = ((soma * 10) % 11).let { if (it == 10) 0 else it }
            if (dig != nums[j]) return false
        }
        return true
    }

    /** CNPJ com dígitos verificadores válidos (módulo 11). */
    fun cnpjValido(cnpj: String): Boolean {
        val d = apenasDigitos(cnpj)
        if (d.length != 14 || d.all { it == d[0] }) return false
        val nums = d.map { it - '0' }
        val pesos1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val pesos2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        fun digito(pesos: IntArray): Int {
            val soma = pesos.indices.sumOf { nums[it] * pesos[it] }
            val resto = soma % 11
            return if (resto < 2) 0 else 11 - resto
        }
        return digito(pesos1) == nums[12] && digito(pesos2) == nums[13]
    }

    /** CPF ou CNPJ conforme o tipo de pessoa (PF/PJ). */
    fun documentoValido(documento: String, pessoaJuridica: Boolean): Boolean =
        if (pessoaJuridica) cnpjValido(documento) else cpfValido(documento)

    /** Senha forte: 8+ caracteres com maiúscula, minúscula, número e símbolo. */
    fun senhaForte(senha: String): Boolean {
        if (senha.length < 8) return false
        return senha.any { it.isUpperCase() } &&
            senha.any { it.isLowerCase() } &&
            senha.any { it.isDigit() } &&
            senha.any { !it.isLetterOrDigit() }
    }
}
