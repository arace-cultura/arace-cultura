<?php
// Define a "pasta virtual" (namespace) onde este arquivo está localizado no projeto.
// Isso ajuda a organizar o código e evitar conflitos de nomes.
namespace App\Collections;
// Importa arquivos necessários de outras partes do sistema
use App\Entities\Product;// Classe que representa um único registro (produto)
use Tatter\Firebase\Firestore\Collection;// Classe base que fornece os métodos para lidar com o banco de dados Firestore

final class ProductCollection extends Collection
{
    public const NAME   = 'Produtos';
    public const ENTITY = Product::class;

    protected bool $skipValidation = true;

    protected array $allowedFields = [
        'nome',
        'nome_produto',
        'descricao',
        'description',
        'preco',
        'preco_produto',
        'precoAntigo',
        'categoria',
        'categorias',
        'categoria_produto',
        'colecao',
        'quantidade',
        'estoque',
        'disponivel',
        'destaque',
        'produtorId',
        'produtor_id',
        'artesao',
        'produtor',
        'nome_produtor',
        'nome_loja',
        'imagens',
        'imagem',
        'imagemUrl',
        'img',
        'cor',
        'quantidadeAvaliacoes',
        'somaAvaliacoes',
        'avaliacoes',
        'avaliacao',
        'estrelas',
        'createdAt',
        'updatedAt',
    ];

    protected array $validationRules = [];
}
