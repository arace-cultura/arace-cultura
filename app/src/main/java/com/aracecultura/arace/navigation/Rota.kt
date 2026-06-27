package com.aracecultura.arace.navigation

import kotlinx.serialization.Serializable

sealed interface Rota {
    // ---- Autenticação ----
    @Serializable data object AuthGraph : Rota
    @Serializable data object Entrada : Rota
    @Serializable data object Login : Rota
    @Serializable data object RecuperarSenha : Rota      // ← faltava
    @Serializable data object Cadastro : Rota

    // ---- App principal (atrás do footer) ----
    @Serializable data object MainGraph : Rota
    @Serializable data object Home : Rota
    @Serializable data object Explorar : Rota
    @Serializable data object Carrinho : Rota
    @Serializable data object PerfilCliente : Rota
    @Serializable data object PerfilProdutor : Rota
    @Serializable data object CriarProduto : Rota
    @Serializable data class  Produto(val produtoId: String) : Rota
    @Serializable data class  Categoria(val categoria: String) : Rota
    @Serializable data class  ProdutorPublico(val lojaId: String) : Rota
    @Serializable data object FinalizarCompra : Rota

    @Serializable data object TelaVendas : Rota
    // ---- Fluxo de virar produtor ----
    @Serializable data object CadastroProdutorGraph : Rota // grafo que agrupa o fluxo inteiro
    @Serializable data object EscolhaLoja : Rota         // criar nova x entrar em existente
    @Serializable data object EntrarLoja : Rota          // entrar em loja com senha
    @Serializable data object CadastroProdutor : Rota    // ← UMA rota; Tela1/2/3 viram passos internos]

}