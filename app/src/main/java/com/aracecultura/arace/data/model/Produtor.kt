package com.aracecultura.arace.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produtor(
    val nomeCompleto: String = "",
    val nomeLoja: String = "",
    val tipoPessoa: String = "", // "PF" ou "PJ"
    val razaoSocial: String = "",
    val cnpj: String = "",
    val telefone: String = "",
    val cep: String = "",
    val endereco: String = "",
    val tipoArtesanato: String = "",
    val categoriaProduto: String = ""
) : Parcelable