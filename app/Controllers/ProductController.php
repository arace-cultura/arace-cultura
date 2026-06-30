<?php

namespace App\Controllers;

use App\Libraries\AraceFirestore; // Classe que faz a ponte com o banco NoSQL Firestore
use CodeIgniter\Exceptions\PageNotFoundException; // Exceção para disparar erro 404 (Página Não Encontrada)

/**
 * ProductController controla a listagem, busca, filtros e exibição individual de produtos.
 */
final class ProductController extends BaseController
{
    /**
     * Exibe a página de listagem e busca de produtos (Vitrine/Busca).
     * Aceita filtros via URL do tipo GET (ex: ?q=caneca&categoria=ceramica)
     */
    public function index(): string
    {
        $firestore = new AraceFirestore();
        $produtos  = $firestore->products(); // Puxa todos os produtos cadastrados do banco

        // Captura os parâmetros de busca da URL, limpando espaços em branco nas pontas
        $q         = trim((string) $this->request->getGet('q'));
        $categoria = trim((string) $this->request->getGet('categoria'));

        // FILTRO 1: Se o usuário digitou algo no campo de busca de texto livre
        if ($q !== '') {
            // Filtra o array de produtos mantendo apenas os que dão match com o termo digitado
            $produtos = array_values(array_filter($produtos, static function (array $produto) use ($q): bool {
                // stripos busca sem diferenciar maiúsculas de minúsculas (Case Insensitive).
                // Procura o termo no Nome, na Descrição ou no nome do Artesão/Produtor.
                return stripos((string) ($produto['nome'] ?? ''), $q) !== false
                    || stripos((string) ($produto['descricao'] ?? ''), $q) !== false
                    || stripos((string) ($produto['artesao'] ?? ''), $q) !== false;
            }));
        }

        // FILTRO 2: Se o usuário selecionou uma categoria específica para filtrar
        if ($categoria !== '') {
            $produtos = array_values(array_filter($produtos, static function (array $produto) use ($categoria): bool {
                // Suporta se o produto tem um array de múltiplas 'categorias' ou apenas uma String 'categoria'
                $categorias = $produto['categorias'] ?? [$produto['categoria'] ?? ''];
                
                // Converte todas as categorias do produto para letras minúsculas (evita problemas com acentos/caixa)
                $categorias = array_map(static fn ($item): string => mb_strtolower((string) $item), $categorias);

                // Verifica se a categoria buscada está presente na lista do produto
                return in_array(mb_strtolower($categoria), $categorias, true);
            }));
        }

        // Renderiza a view de resultados passando a lista tratada e os termos usados para preencher os inputs da tela
        return view('main/arace-search', [
            'produtos'  => $produtos,
            'q'         => $q,
            'categoria' => $categoria,
        ]);
    }

    /**
     * Exibe a página de detalhes de um produto específico.
     * Pode receber o ID diretamente pela rota `/produto/show/ID_AQUI` ou via query string `?id=ID_AQUI`.
     */
    public function show(?string $id = null): string
    {
        // Junta as duas possibilidades de recebimento do ID e limpa espaços vazios
        $id = trim((string) ($id ?? $this->request->getGet('id')));

        // Se nenhum ID foi informado na URL, interrompe o código e exibe erro 404 de página não encontrada
        if ($id === '') {
            throw PageNotFoundException::forPageNotFound('Produto nao informado.');
        }

        $firestore = new AraceFirestore();
        $produto   = $firestore->product($id); // Busca o documento exato do produto no Firestore

        // Se o ID não corresponder a nenhum produto existente no banco, joga erro 404
        if ($produto === null) {
            throw PageNotFoundException::forPageNotFound('Produto nao encontrado.');
        }

        // Se encontrou, renderiza a tela do produto
        return view('main/arace-produto', [
            'produto'      => $produto,
            // CARROSSEL DE RECOMENDADOS:
            // Puxa os produtos novamente e remove o produto atual da lista para não recomendar o item que ele já está vendo
            'recomendados' => array_values(array_filter(
                $firestore->products(),
                static fn (array $item): bool => (string) ($item['id'] ?? '') !== $id
            )),
        ]);
    }
}