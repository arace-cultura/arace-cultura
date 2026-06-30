<?php

namespace App\Libraries;

use App\Collections\UsuarioCollection;
use App\Collections\ProducerCollection;
use App\Collections\ProductCollection;
use RuntimeException;

final class AraceFirestore
{
    private const FALLBACK_PRODUCERS = [
        ['id' => 'espirito-das-pedras', 'nome' => 'Espirito das Pedras', 'iniciais' => 'EP', 'produtos' => 12],
        ['id' => 'arte-arace', 'nome' => 'Arte Arace', 'iniciais' => 'AA', 'produtos' => 8],
        ['id' => 'nativo-pottery', 'nome' => 'Nativo Pottery', 'iniciais' => 'NP', 'produtos' => 21],
    ];

    public function products(bool $featured = false): array
    {
        try {
            $products = $this->collectionItems(ProductCollection::class);
            $products = array_map(fn (array $product): array => $this->normalizeProduct($product), $products);

            if ($featured) {
                $products = array_values(array_filter($products, static fn (array $product): bool => $product['destaque'] ?? true));
            }

            return $products;
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar produtos no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return [];
        }
    }

    public function product(string $id): ?array
    {
        try {
            $product = $this->collection(ProductCollection::class)->get($id);

            return $product === null ? null : $this->normalizeProduct($this->entityPayload($product));
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar produto no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

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

    public function producerFromSession(array $sessionUser): array
    {
        try {
            $collection = $this->collection(ProducerCollection::class);
            $entity     = $this->producerEntityFromSession($collection, $sessionUser);

            return $entity === null ? [] : $this->normalizeProducer($this->entityPayload($entity));
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar produtor atual no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return [];
        }
    }

    public function createUser(array $payload): array
    {
        $uid = (string) ($payload['uid'] ?? $payload['id'] ?? $payload['firebaseUid'] ?? '');

        $payload['email'] = mb_strtolower(trim((string) ($payload['email'] ?? '')));
        $payload['telefone'] = $this->formatPhone((string) ($payload['telefone'] ?? ''));
        $payload['nome'] = trim((string) ($payload['nome'] ?? 'Usuario'));
        $payload['firebaseUid'] = $uid !== '' ? $uid : null;

        unset($payload['senha'], $payload['confirmarSenha'], $payload['password'], $payload['id']);

        if ($uid !== '') {
            $payload['uid'] = $uid;
        }

        $user = $this->entityPayload($this->collection(UsuarioCollection::class)->add($payload));

        return $this->safeUser($user);
    }

    public function userForAuthenticatedUser(array $authUser): array
    {
        $uid   = (string) ($authUser['uid'] ?? $authUser['id'] ?? '');
        $email = mb_strtolower(trim((string) ($authUser['email'] ?? '')));
        $user  = null;

        try {
            $collection = $this->collection(UsuarioCollection::class);

            if ($uid !== '') {
                try {
                    $entity = $collection->get($uid);
                    if ($entity !== null) {
                        $user = $this->entityPayload($entity);
                    }
                } catch (\Throwable $exception) {
                    log_message('warning', 'Perfil do usuario autenticado sera reconstruido: {message}', [
                        'message' => $exception->getMessage(),
                    ]);
                }
            }

            if ($user === null && $email !== '') {
                $query = $collection->where('email', '=', $email)->limit(1);

                try {
                    foreach ($collection->list($query) as $entity) {
                        $user = $this->entityPayload($entity);
                        break;
                    }
                } catch (\Throwable $exception) {
                    log_message('warning', 'Perfil legado por e-mail sera ignorado: {message}', [
                        'message' => $exception->getMessage(),
                    ]);
                }
            }
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar perfil do usuario autenticado: {message}', [
                'message' => $exception->getMessage(),
            ]);
        }

        if ($user === null) {
            if ($email === '') {
                return $this->safeUser($authUser);
            }

            return $this->createUser([
                'uid'           => $uid,
                'firebaseUid'   => $uid,
                'nome'          => $authUser['nome'] ?? $authUser['displayName'] ?? ($email !== '' ? strstr($email, '@', true) : 'Usuario'),
                'email'         => $email,
                'telefone'      => $authUser['telefone'] ?? $authUser['phoneNumber'] ?? '',
                'emailVerified' => $authUser['emailVerified'] ?? false,
                'disabled'      => $authUser['disabled'] ?? false,
            ]);
        }

        $user['uid'] = $uid !== '' ? $uid : ($user['uid'] ?? $user['firebaseUid'] ?? $user['id'] ?? '');
        $user['firebaseUid'] = $user['uid'];

        return $this->safeUser([...$user, ...$authUser, 'id' => $user['uid']]);
    }

    public function userFromSession(array $sessionUser): array
    {
        $user = null;

        try {
            $collection = $this->collection(UsuarioCollection::class);
            $id         = trim((string) ($sessionUser['id'] ?? ''));

            if ($id !== '') {
                $entity = $collection->get($id);
                if ($entity !== null) {
                    $user = $this->entityPayload($entity);
                }
            }

            if ($user === null && ! empty($sessionUser['email'])) {
                $email = mb_strtolower(trim((string) $sessionUser['email']));
                $query = $collection->where('email', '=', $email)->limit(1);

                foreach ($collection->list($query) as $entity) {
                    $user = $this->entityPayload($entity);
                    break;
                }
            }
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar usuario atual no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);
        }

        return $this->safeUser($user ?? $sessionUser);
    }

    public function updateUserFromSession(array $sessionUser, array $payload): array
    {
        $collection = $this->collection(UsuarioCollection::class);
        $entity     = $this->userEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Usuario autenticado nao foi encontrado no Firestore.');
        }

        $allowed = [
            'nome',
            'username',
            'telefone',
            'cidade',
            'estado',
            'cpf',
            'fotoUrl',
            'bio',
            'nascimento',
            'sexo',
        ];

        $updates = [];
        foreach ($allowed as $field) {
            if (array_key_exists($field, $payload)) {
                $updates[$field] = is_string($payload[$field]) ? trim($payload[$field]) : $payload[$field];
            }
        }

        if (isset($updates['telefone'])) {
            $updates['telefone'] = $this->formatPhone((string) $updates['telefone']);
        }
        if (isset($updates['cpf'])) {
            $updates['cpf'] = $this->formatCpf((string) $updates['cpf']);
        }

        $collection->update($entity, $updates);

        return $this->userFromSession($this->safeUser([...$sessionUser, ...$updates]));
    }

    public function updateProducerFromSession(array $sessionUser, array $payload): array
    {
        $collection = $this->collection(ProducerCollection::class);
        $entity     = $this->producerEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Produtor autenticado nao foi encontrado no Firestore.');
        }

        $allowed = [
            'nomeLoja',
            'lojaBio',
            'cnpj',
            'categoria',
            'email',
            'telefone',
            'fotoUrl',
            'bannerUrl',
            'cepOrigem',
            'cidade',
            'estado',
            'endereco',
            'retiradaLocal',
            'envioCorreios',
            'entregaLocal',
            'banco',
            'tipoConta',
            'agencia',
            'conta',
            'pix',
            'horarioSemanaInicio',
            'horarioSemanaFim',
            'horarioSabadoInicio',
            'horarioSabadoFim',
        ];

        $updates = [];
        foreach ($allowed as $field) {
            if (array_key_exists($field, $payload)) {
                $updates[$field] = is_string($payload[$field]) ? trim($payload[$field]) : $payload[$field];
            }
        }

        if (isset($updates['nomeLoja'])) {
            $updates['nome'] = $updates['nomeLoja'];
            $updates['nome_loja'] = $updates['nomeLoja'];
            $updates['iniciais'] = $this->initials((string) $updates['nomeLoja']);
        }
        if (isset($updates['lojaBio'])) {
            $updates['bio'] = $updates['lojaBio'];
        }
        if (isset($updates['email'])) {
            $updates['email'] = mb_strtolower((string) $updates['email']);
            $updates['email_comercial'] = $updates['email'];
        }
        if (isset($updates['telefone'])) {
            $updates['telefone'] = $this->formatPhone((string) $updates['telefone']);
            $updates['telefone_comercial'] = $updates['telefone'];
        }
        if (isset($updates['categoria'])) {
            $updates['categoria_principal'] = $updates['categoria'];
        }
        $updates['retiradaLocal'] = array_key_exists('retiradaLocal', $payload);
        $updates['envioCorreios'] = array_key_exists('envioCorreios', $payload);
        $updates['entregaLocal'] = array_key_exists('entregaLocal', $payload);

        $collection->update($entity, $updates);

        return $this->normalizeProducer([...$this->entityPayload($entity), ...$updates]);
    }

    private function safeUser(array $user): array
    {
        return array_filter([
            'id'       => (string) ($user['id'] ?? $user['uid'] ?? $user['firebaseUid'] ?? ''),
            'uid'      => (string) ($user['uid'] ?? $user['firebaseUid'] ?? $user['id'] ?? ''),
            'nome'     => (string) ($user['nome'] ?? 'Usuario'),
            'username' => $user['username'] ?? $user['usuario'] ?? null,
            'email'    => (string) ($user['email'] ?? ''),
            'telefone' => isset($user['telefone']) ? $this->formatPhone((string) $user['telefone']) : null,
            'cidade'   => $user['cidade'] ?? null,
            'estado'   => $user['estado'] ?? null,
            'cpf'      => isset($user['cpf']) ? $this->formatCpf((string) $user['cpf']) : null,
            'fotoUrl'  => $user['fotoUrl'] ?? $user['avatar'] ?? null,
            'avatar'   => $user['fotoUrl'] ?? $user['avatar'] ?? null,
            'bio'      => $user['bio'] ?? null,
            'nascimento' => $user['nascimento'] ?? $user['dataNascimento'] ?? null,
            'sexo'     => $user['sexo'] ?? $user['genero'] ?? null,
            'genero'   => $user['sexo'] ?? $user['genero'] ?? null,
            'createdAt' => $user['createdAt'] ?? $user['criadoEm'] ?? null,
        ], static fn ($value): bool => $value !== null && $value !== '');
    }

    private function userEntityFromSession(object $collection, array $sessionUser): ?object
    {
        $id = trim((string) ($sessionUser['id'] ?? ''));
        if ($id !== '') {
            $entity = $collection->get($id);
            if ($entity !== null) {
                return $entity;
            }
        }

        if (! empty($sessionUser['email'])) {
            $email = mb_strtolower(trim((string) $sessionUser['email']));
            $query = $collection->where('email', '=', $email)->limit(1);

            foreach ($collection->list($query) as $entity) {
                return $entity;
            }
        }

        return null;
    }

    private function producerEntityFromSession(object $collection, array $sessionUser): ?object
    {
        $ids = array_filter([
            trim((string) ($sessionUser['producerId'] ?? '')),
            trim((string) ($sessionUser['produtorId'] ?? '')),
            trim((string) ($sessionUser['id'] ?? '')),
            trim((string) ($sessionUser['uid'] ?? '')),
        ]);

        foreach ($ids as $id) {
            try {
                $entity = $collection->get($id);
                if ($entity !== null) {
                    return $entity;
                }
            } catch (\Throwable) {
            }
        }

        if (! empty($sessionUser['email'])) {
            $email = mb_strtolower(trim((string) $sessionUser['email']));
            $fields = ['email', 'email_comercial'];

            foreach ($fields as $field) {
                $query = $collection->where($field, '=', $email)->limit(1);
                foreach ($collection->list($query) as $entity) {
                    return $entity;
                }
            }
        }

        return null;
    }

    public function createProducer(array $payload): array
    {
        if (isset($payload['telefone'])) {
            $payload['telefone'] = $this->formatPhone((string) $payload['telefone']);
        }
        if (isset($payload['cpf'])) {
            $payload['cpf'] = $this->formatCpf((string) $payload['cpf']);
        }

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

    private function collectionItems(string $class, ?string $orderBy = null): array
    {
        $collection = $this->collection($class);
        $query      = $orderBy === null ? null : $collection->orderBy($orderBy);
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
        $quantidadeAvaliacoes = (int) ($product['quantidadeAvaliacoes'] ?? $product['avaliacoes'] ?? $product['total_avaliacoes'] ?? 0);
        $somaAvaliacoes       = (float) ($product['somaAvaliacoes'] ?? 0);
        $estrelas             = $product['estrelas'] ?? $product['avaliacao'] ?? null;

        if ($estrelas === null && $quantidadeAvaliacoes > 0 && $somaAvaliacoes > 0) {
            $estrelas = $somaAvaliacoes / $quantidadeAvaliacoes;
        }

        $categorias = $product['categorias'] ?? $product['categoria'] ?? $product['categoria_produto'] ?? $product['colecao'] ?? 'artesanato';
        if (is_string($categorias) && str_contains($categorias, ',')) {
            $categorias = array_map('trim', explode(',', $categorias));
        }

        $product['id']                   = (string) ($product['id'] ?? $product['produto_id'] ?? $product['sku'] ?? '');
        $product['nome']                 = $product['nome'] ?? $product['nome_produto'] ?? $product['titulo'] ?? 'Produto Arace';
        $product['descricao']            = $product['descricao'] ?? $product['description'] ?? '';
        $product['produtorId']           = (string) ($product['produtorId'] ?? $product['produtor_id'] ?? '');
        $product['artesao']              = $product['artesao'] ?? $product['produtor'] ?? $product['nome_produtor'] ?? $product['nome_loja'] ?? 'Produtor Arace';
        $product['categorias']           = is_array($categorias) ? array_values($categorias) : [$categorias];
        $product['categoria']            = (string) ($product['categorias'][0] ?? 'artesanato');
        $product['preco']                = (float) ($product['preco'] ?? $product['preco_produto'] ?? $product['valor'] ?? 0);
        $product['quantidadeAvaliacoes'] = $quantidadeAvaliacoes;
        $product['somaAvaliacoes']       = $somaAvaliacoes;
        $product['avaliacoes']           = $quantidadeAvaliacoes;
        $product['estrelas']             = (float) ($estrelas ?? 0);
        $product['cor']                  = $product['cor'] ?? '#b5a898';
        $product['imagens']              = $this->productImages($product);
        $product['img']                  = $product['imagens'][0] ?? '';
        $product['imagem']               = $product['img'];

        if ($product['id'] === '') {
            $product['id'] = url_title($product['nome'], '-', true);
        }

        return $product;
    }

    private function productImages(array $product): array
    {
        $fields = [
            'imagens',
            'images',
            'fotos',
            'foto',
            'img',
            'imagem',
            'imagemUrl',
            'imagemURL',
            'imagem_url',
            'imagem_produto',
            'image',
            'imageUrl',
            'imageURL',
            'image_url',
            'urlImagem',
            'url_imagem',
            'publicUrl',
            'publicURL',
            'public_url',
            'storageUrl',
            'storage_url',
            'supabaseUrl',
            'supabase_url',
            'thumbnail',
            'capa',
            'url',
        ];

        $images = [];

        foreach ($fields as $field) {
            if (array_key_exists($field, $product)) {
                $images = array_merge($images, $this->imageUrlsFromValue($product[$field]));
            }
        }

        return array_values(array_unique(array_filter(
            $images,
            static fn (string $url): bool => $url !== ''
        )));
    }

    private function imageUrlsFromValue(mixed $value): array
    {
        if ($value === null || $value === '') {
            return [];
        }

        if (is_string($value)) {
            $value = trim($value);

            return filter_var($value, FILTER_VALIDATE_URL) ? [$value] : [];
        }

        if (is_object($value)) {
            $value = method_exists($value, 'toArray') ? $value->toArray() : (array) $value;
        }

        if (! is_array($value)) {
            return [];
        }

        $images = [];
        $preferredKeys = ['url', 'publicUrl', 'public_url', 'imageUrl', 'image_url', 'imagemUrl', 'imagem_url', 'src'];

        foreach ($preferredKeys as $key) {
            if (array_key_exists($key, $value)) {
                $images = array_merge($images, $this->imageUrlsFromValue($value[$key]));
            }
        }

        foreach ($value as $item) {
            $images = array_merge($images, $this->imageUrlsFromValue($item));
        }

        return $images;
    }

    private function normalizeProducer(array $producer): array
    {
        $producer['id']       = (string) ($producer['id'] ?? $producer['produtor_id'] ?? '');
        $producer['nome']     = $producer['nome'] ?? $producer['nomeLoja'] ?? $producer['nome_loja'] ?? 'Produtor Arace';
        $producer['nomeLoja'] = $producer['nomeLoja'] ?? $producer['nome_loja'] ?? $producer['nome'];
        $producer['lojaBio']  = $producer['lojaBio'] ?? $producer['bio'] ?? '';
        $producer['email']    = $producer['email'] ?? $producer['email_comercial'] ?? '';
        $producer['telefone'] = isset($producer['telefone']) ? $this->formatPhone((string) $producer['telefone']) : ($producer['telefone_comercial'] ?? '');
        $producer['categoria'] = $producer['categoria'] ?? $producer['categoria_principal'] ?? '';
        $producer['fotoUrl'] = $producer['fotoUrl'] ?? $producer['lojaAvatar'] ?? $producer['avatar'] ?? '';
        $producer['lojaAvatar'] = $producer['fotoUrl'];
        $producer['bannerUrl'] = $producer['bannerUrl'] ?? $producer['banner'] ?? '';
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

    private function formatPhone(string $phone): string
    {
        $digits = preg_replace('/\D/', '', $phone) ?? '';

        if (strlen($digits) === 11) {
            return sprintf('(%s) %s-%s', substr($digits, 0, 2), substr($digits, 2, 5), substr($digits, 7));
        }

        if (strlen($digits) === 10) {
            return sprintf('(%s) %s-%s', substr($digits, 0, 2), substr($digits, 2, 4), substr($digits, 6));
        }

        return trim($phone);
    }

    private function formatCpf(string $cpf): string
    {
        $digits = preg_replace('/\D/', '', $cpf) ?? '';

        if (strlen($digits) !== 11) {
            return trim($cpf);
        }

        return sprintf('%s.%s.%s-%s', substr($digits, 0, 3), substr($digits, 3, 3), substr($digits, 6, 3), substr($digits, 9));
    }
}
