package com.aracecultura.arace.data.model

/**
 * Fonte única de verdade para as categorias de produto.
 * Usada na criação de produto, no cadastro de produtor e nos filtros de explorar.
 * Alterar aqui propaga para todas as telas — não duplique esta lista.
 */
object CategoriasProduto {
    val TODAS = listOf(
        "Artesanato",
        "Têxteis",
        "Cosméticos",
        "Casa",
        "Cerâmica",
        "Acessórios"
    )
}
