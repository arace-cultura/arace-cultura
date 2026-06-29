<?php

namespace App\Libraries;

use App\Collections\UsuarioCollection;
use App\Collections\ProducerCollection;
use App\Collections\ProductCollection;
use DomainException;
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

    public function createUser(array $payload): array
    {
        $payload['email'] = mb_strtolower(trim((string) ($payload['email'] ?? '')));

        if (isset($payload['senha'])) {
            $payload['senha'] = password_hash((string) $payload['senha'], PASSWORD_DEFAULT);
        }

        $user = $this->entityPayload($this->collection(UsuarioCollection::class)->add($payload));
        unset($user['senha']);

        return $user;
    }

    /**
     * Confere as credenciais diretamente na colecao Usuarios.
     * Retorna somente os dados seguros que podem ir para a sessao.
     */
    public function authenticateUser(string $email, string $password): ?array
    {
        $email      = mb_strtolower(trim($email));
        $collection = $this->collection(UsuarioCollection::class);
        $user       = null;
        $userEntity = null;

        // Cadastros novos usam e-mail normalizado e aproveitam a consulta indexada.
        $query = $collection->where('email', '=', $email)->limit(1);
        foreach ($collection->list($query) as $entity) {
            $userEntity = $entity;
            $user       = $this->entityPayload($entity);
            break;
        }

        // Mantem compatibilidade com contas antigas que tenham letras maiusculas.
        if ($user === null) {
            foreach ($collection->list() as $entity) {
                $candidate = $this->entityPayload($entity);
                if (mb_strtolower(trim((string) ($candidate['email'] ?? ''))) === $email) {
                    $userEntity = $entity;
                    $user       = $candidate;
                    break;
                }
            }
        }

        if ($user === null || ! $this->passwordMatches($password, (string) ($user['senha'] ?? ''))) {
            return null;
        }

        $status = mb_strtolower((string) ($user['status'] ?? 'ativo'));
        if (($user['ativo'] ?? true) === false
            || ($user['verificado'] ?? $user['emailVerificado'] ?? true) === false
            || in_array($status, ['bloqueado', 'inativo', 'pendente'], true)) {
            throw new DomainException('A conta ainda nao foi verificada ou esta inativa.');
        }

        // Registros antigos em texto puro sao aceitos uma unica vez e
        // atualizados imediatamente para o mesmo hash seguro dos cadastros novos.
        if ($userEntity !== null && password_get_info((string) $user['senha'])['algo'] === null) {
            try {
                $collection->update($userEntity, [
                    'senha' => password_hash($password, PASSWORD_DEFAULT),
                ]);
            } catch (\Throwable) {
                // Uma falha de migracao nao invalida credenciais ja conferidas.
            }
        }

        return array_filter([
            'id'       => (string) ($user['id'] ?? ''),
            'nome'     => (string) ($user['nome'] ?? 'Usuario'),
            'email'    => (string) ($user['email'] ?? $email),
            'telefone' => $user['telefone'] ?? null,
            'cidade'   => $user['cidade'] ?? null,
            'estado'   => $user['estado'] ?? null,
            'cpf'      => $user['cpf'] ?? null,
            'avatar'   => $user['avatar'] ?? null,
        ], static fn ($value): bool => $value !== null && $value !== '');
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

    private function passwordMatches(string $plainPassword, string $storedPassword): bool
    {
        if ($plainPassword === '' || $storedPassword === '') {
            return false;
        }

        // As contas criadas pela aplicacao usam password_hash(). O segundo
        // caso permite a primeira entrada de registros legados em texto puro.
        return password_verify($plainPassword, $storedPassword)
            || (password_get_info($storedPassword)['algo'] === null
                && hash_equals($storedPassword, $plainPassword));
    }
}
