package com.aracecultura.arace.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Resultado de uma consulta de CEP no ViaCEP. */
sealed interface ResultadoCep {
    data class Valido(val logradouro: String, val localidade: String, val uf: String) : ResultadoCep
    /** Formato ok porém o CEP não existe na base. */
    data object NaoEncontrado : ResultadoCep
    /** Não deu para consultar (offline/timeout): não confirma nem nega o CEP. */
    data object ErroRede : ResultadoCep
}

/**
 * Confirma a existência de um CEP via API pública do ViaCEP. A validação de
 * formato (8 dígitos) é feita antes, em [Validacoes.cepFormatoValido]; aqui só
 * confirmamos que o CEP de fato existe.
 */
object CepRepository {

    @Serializable
    private data class ViaCepResposta(
        val logradouro: String = "",
        val localidade: String = "",
        val uf: String = "",
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun consultar(cep: String): ResultadoCep {
        val digitos = cep.filter { it.isDigit() }
        if (digitos.length != 8) return ResultadoCep.NaoEncontrado
        return try {
            val corpo = HttpClient(Android).use { cliente ->
                cliente.get("https://viacep.com.br/ws/$digitos/json/").bodyAsText()
            }
            // ViaCEP responde {"erro": ...} para CEP inexistente — nesse caso a
            // localidade vem vazia, o que usamos como sinal de "não encontrado".
            val resp = json.decodeFromString<ViaCepResposta>(corpo)
            if (resp.localidade.isBlank()) ResultadoCep.NaoEncontrado
            else ResultadoCep.Valido(resp.logradouro, resp.localidade, resp.uf)
        } catch (_: Exception) {
            ResultadoCep.ErroRede
        }
    }
}
