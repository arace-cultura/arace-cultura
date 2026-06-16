<?php

namespace App\Libraries;

use App\Collections\CustomerCollection;
use App\Collections\ProducerCollection;
use App\Collections\ProductCollection;
use RuntimeException;

final class AraceFirestore
{
    private const FALLBACK_PRODUCTS = [
        [
            'id'         => 'panela-barro',
            'nome'       => 'Panela de barro',
            'artesao'    => 'Espirito das Pedras',
            'categoria'  => 'ceramica',
            'preco'      => 245,
            'avaliacoes' => 24,
            'estrelas'   => 4.5,
            'favorito'   => false,
            'cor'        => '#C1734A',
            'destaque'   => true,
        ],
        [
            'id'         => 'preguica-madeira',
            'nome'       => 'Preguica de machao',
            'artesao'    => 'Atelier Capixaba',
            'categoria'  => 'madeira',
            'preco'      => 290,
            'avaliacoes' => 11,
            'estrelas'   => 4,
            'favorito'   => false,
            'cor'        => '#8F5E35',
            'destaque'   => true,
        ],
        [
            'id'         => 'panela-barro-2',
            'nome'       => 'Panela de barro n. 2',
            'artesao'    => 'Arte Local',
            'categoria'  => 'ceramica',
            'preco'      => 180,
            'avaliacoes' => 38,
            'estrelas'   => 5,
            'favorito'   => true,
            'cor'        => '#D28A4D',
            'destaque'   => true,
        ],
    ];

    private const FALLBACK_PRODUCERS = [
        ['id' => 'espirito-das-pedras', 'nome' => 'Espirito das Pedras', 'iniciais' => 'EP', 'produtos' => 12],
        ['id' => 'arte-arace', 'nome' => 'Arte Arace', 'iniciais' => 'AA', 'produtos' => 8],
        ['id' => 'nativo-pottery', 'nome' => 'Nativo Pottery', 'iniciais' => 'NP', 'produtos' => 21],
    ];

    public function products(bool $featured = false): array
    {
        try {
            $products = $this->collectionItems(ProductCollection::class, 'nome');
            $products = array_map(fn (array $product): array => $this->normalizeProduct($product), $products);

            if ($featured) {
                $products = array_values(array_filter($products, static fn (array $product): bool => $product['destaque'] ?? true));
            }

            return $products ?: self::FALLBACK_PRODUCTS;
        } catch (\Throwable) {
            return self::FALLBACK_PRODUCTS;
        }
    }

    public function product(string $id): ?array
    {
        try {
            $product = $this->collection(ProductCollection::class)->get($id);

            return $product === null ? null : $this->normalizeProduct($this->entityPayload($product));
        } catch (\Throwable) {
            foreach (self::FALLBACK_PRODUCTS as $product) {
                if ($product['id'] === $id) {
                    return $product;
                }
            }

            return null;
        }
    }

    public function producers(): array
    {
        try {
            $producers = $this->collectionItems(ProducerCollection::class, 'nome');
            $producers = array_map(fn (array $producer): array => $this->normalizeProducer($producer), $producers);

            return $producers ?: self::FALLBACK_PRODUCERS;
        } catch (\Throwable) {
            return self::FALLBACK_PRODUCERS;
        }
    }

    public function createCustomer(array $payload): array
    {
        if (isset($payload['senha'])) {
            $payload['senha'] = password_hash((string) $payload['senha'], PASSWORD_DEFAULT);
        }

        return $this->entityPayload($this->collection(CustomerCollection::class)->add($payload));
    }

    public function createProducer(array $payload): array
    {
        $payload['tipo']                = 'produtor';
        $payload['cadastrado']          = true;
        $payload['produtos']            = $payload['produtos'] ?? 0;
        $payload['iniciais']            = $this->initials($payload['nomeLoja'] ?? $payload['nome']);
        $payload['createdAt']           = date(DATE_ATOM);
        $payload['nome_loja']           = $payload['nomeLoja'] ?? $payload['nome'];
        $payload['email_comercial']     = $payload['email'];
        $payload['telefone_comercial']  = $payload['telefone'] ?? null;
        $payload['categoria_principal'] = $payload['categoria'] ?? null;
        $payload['distrito_id']         = $payload['distritoId'] ?? null;

        return $this->entityPayload($this->collection(ProducerCollection::class)->add($payload));
    }

    private function collection(string $class): object
    {
        if (! class_exists('Tatter\Firebase\Firestore\Collection')) {
            throw new RuntimeException('A biblioteca tatter/firebase ainda nao esta instalada.');
        }

        helper('firestore');

        return collection($class);
    }

    private function collectionItems(string $class, string $orderBy): array
    {
        $collection = $this->collection($class);
        $query      = $collection->orderBy($orderBy);
        $items      = [];

        foreach ($collection->list($query) as $entity) {
            $items[] = $this->entityPayload($entity);
        }

        return $items;
    }

    private function entityPayload(object $entity): array
    {
        $payload = method_exists($entity, 'toRawArray') ? $entity->toRawArray() : (array) $entity;

        if (method_exists($entity, 'id')) {
            $payload['id'] = $entity->id();
        }

        return $payload;
    }

    private function normalizeProduct(array $product): array
    {
        $product['id']         = (string) ($product['id'] ?? $product['produto_id'] ?? $product['sku'] ?? '');
        $product['nome']       = $product['nome'] ?? $product['nome_produto'] ?? $product['titulo'] ?? 'Produto Arace';
        $product['artesao']    = $product['artesao'] ?? $product['produtor'] ?? $product['nome_produtor'] ?? $product['nome_loja'] ?? 'Produtor Arace';
        $product['categoria']  = $product['categoria'] ?? $product['categoria_produto'] ?? $product['colecao'] ?? 'artesanato';
        $product['preco']      = (float) ($product['preco'] ?? $product['preco_produto'] ?? $product['valor'] ?? 0);
        $product['avaliacoes'] = (int) ($product['avaliacoes'] ?? $product['total_avaliacoes'] ?? 0);
        $product['estrelas']   = (float) ($product['estrelas'] ?? $product['avaliacao'] ?? 4);
        $product['cor']        = $product['cor'] ?? '#b5a898';
        $product['img']        = $product['img'] ?? $product['imagem'] ?? $product['imagem_produto'] ?? '';

        if ($product['id'] === '') {
            $product['id'] = url_title($product['nome'], '-', true);
        }

        return $product;
    }

    private function normalizeProducer(array $producer): array
    {
        $producer['id']       = (string) ($producer['id'] ?? $producer['produtor_id'] ?? '');
        $producer['nome']     = $producer['nome'] ?? $producer['nomeLoja'] ?? $producer['nome_loja'] ?? 'Produtor Arace';
        $producer['iniciais'] = $producer['iniciais'] ?? $this->initials($producer['nome']);
        $producer['produtos'] = (int) ($producer['produtos'] ?? $producer['total_produtos'] ?? 0);

        if ($producer['id'] === '') {
            $producer['id'] = url_title($producer['nome'], '-', true);
        }

        return $producer;
    }

    private function initials(string $name): string
    {
        $words   = preg_split('/\s+/', trim($name)) ?: [];
        $letters = array_map(static fn (string $word): string => mb_substr($word, 0, 1), array_slice($words, 0, 2));

        return mb_strtoupper(implode('', $letters)) ?: 'AR';
    }
}
