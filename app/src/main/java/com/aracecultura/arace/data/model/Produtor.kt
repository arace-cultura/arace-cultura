package com.aracecultura.arace.data.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class Produtor(
    @DocumentId val uid: String = "",
    val nomeCompleto: String = "",
    val nomeLoja: String = "",
    val tipoPessoa: String = "",
    val razaoSocial: String = "",
    val cnpj: String = "",
    val telefone: String = "",
    val cep: String = "",
    val endereco: String = "",
    val tipoArtesanato: String = "",
    val categoriaProduto: String = "",
    val banner: String = "",
    val fotoLoja: String = "",
    val fotosHistoria: List<String> = emptyList(),
    val historia: String = "",
    // Hash da senha da loja (login compartilhado). Gravado junto na criação
    // para evitar uma segunda escrita (update) que poderia ser barrada.
    val senhaHash: String = "",
    // UIDs das contas que administram esta loja. É a posse usada pelas regras
    // do Firestore: só membros editam a loja e seus produtos.
    val membros: List<String> = emptyList()
) : Parcelable