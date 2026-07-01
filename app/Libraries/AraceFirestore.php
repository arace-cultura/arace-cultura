<?php

namespace App\Libraries;

use App\Collections\UsuarioCollection;  // Mapeamento da coleção de usuários no Firestore
use App\Collections\ProducerCollection; // Mapeamento da coleção de produtores/artesãos no Firestore
use App\Collections\ProductCollection;  // Mapeamento da coleção de produtos no Firestore
use RuntimeException;                  // Exceção para falhas estruturais ou de drivers ausentes

/**
 * AraceFirestore centraliza as operações de leitura, escrita e normalização de dados 
 * utilizando o banco NoSQL Firebase Firestore através da biblioteca Tatter.
 */
final class AraceFirestore
{
    /**
     * Lista de produtores reserva (Fallback) usada caso a API do Firestore falhe 
     * ou o banco de dados retorne vazio, garantindo resiliência à interface.
     */
    private const FALLBACK_PRODUCERS = [
        ['id' => 'espirito-das-pedras', 'nome' => 'Espirito das Pedras', 'iniciais' => 'EP', 'produtos' => 12],
        ['id' => 'arte-arace', 'nome' => 'Arte Arace', 'iniciais' => 'AA', 'produtos' => 8],
        ['id' => 'nativo-pottery', 'nome' => 'Nativo Pottery', 'iniciais' => 'NP', 'produtos' => 21],
    ];

    /**
     * Puxa a lista completa de produtos e aplica as normalizações de chaves.
     * @param bool $featured Se verdadeiro, filtra apenas os produtos marcados como destaque.
     */
    public function products(bool $featured = false): array
    {
        try {
            // Busca todos os registros brutos mapeados pela classe de coleção
            $products = $this->collectionItems(ProductCollection::class);
            // Uniformiza os campos de cada produto para evitar quebras por variação de chaves
            $products = array_map(fn (array $product): array => $this->normalizeProduct($product), $products);

            if ($featured) {
                // Filtra os itens mantendo apenas os sinalizados em destaque
                $products = array_values(array_filter($products, static fn (array $product): bool => $product['destaque'] ?? true));
            }

            return $products;
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar produtos no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return []; // Abordagem tolerante a falhas: retorna uma lista vazia se o banco cair
        }
    }

    /**
     * Busca um único produto com base no seu ID identificador exclusivo.
     */
    public function product(string $id): ?array
    {
        try {
            $product = $this->collection(ProductCollection::class)->get($id);

            // Transforma a entidade bruta em um payload limpo e depois normaliza as chaves
            return $product === null ? null : $this->normalizeProduct($this->entityPayload($product));
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao buscar produto no Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return null;
        }
    }

    /**
     * Recupera todos os produtores/lojas parceiras ordenados pelo nome.
     */
    public function producers(): array
    {
        try {
            // Não ordenamos direto no Firestore: como os documentos da coleção "Produtores"
            // guardam o nome dentro de "membros", uma query orderBy('nome') descartaria
            // qualquer documento sem o campo raiz "nome". Buscamos tudo e ordenamos em PHP.
            $producers = $this->collectionItems(ProducerCollection::class);
            $producers = array_map(fn (array $producer): array => $this->normalizeProducer($producer), $producers);

            usort($producers, static fn (array $a, array $b): int => strcasecmp((string) ($a['nome'] ?? ''), (string) ($b['nome'] ?? '')));

            // Operador Elvis: se o array vier vazio da API, injeta instantaneamente a constante de Fallback
            return $producers ?: self::FALLBACK_PRODUCERS;
        } catch (\Throwable) {
            return self::FALLBACK_PRODUCERS;
        }
    }

    /**
     * Resgata o perfil do produtor atrelado ao usuário que está atualmente na sessão ativa.
     */
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

    /**
     * Cria e persiste o documento inicial de dados complementares de um usuário no Firestore.
     */
    public function createUser(array $payload): array
    {
        // Resguarda a captura do UID gerado pelo Firebase Auth sob qualquer variação de nome de chave
        $uid = (string) ($payload['uid'] ?? $payload['id'] ?? $payload['firebaseUid'] ?? '');

        // Limpeza, sanitização e formatação prévia dos inputs recebidos do formulário
        $payload['email'] = mb_strtolower(trim((string) ($payload['email'] ?? '')));
        $payload['telefone'] = $this->formatPhone((string) ($payload['telefone'] ?? ''));
        $payload['nome'] = trim((string) ($payload['nome'] ?? 'Usuario'));
        $payload['firebaseUid'] = $uid !== '' ? $uid : null;

        // Marca se o usuario ja e produtor. Por padrao todo usuario nasce como cliente (false).
        // O vinculo usuario<->produtor no banco e feito pelo uid.
        $payload['isProdutor'] = array_key_exists('isProdutor', $payload)
            ? (bool) $payload['isProdutor']
            : ($uid !== '' && $this->producerExistsForUid($uid));

        // PROTEÇÃO DE DADOS: Remove credenciais sensíveis e chaves voláteis antes de salvar no banco NoSQL
        unset($payload['senha'], $payload['confirmarSenha'], $payload['password'], $payload['id']);

        if ($uid !== '') {
            $payload['uid'] = $uid;
        }

        // Insere o documento na coleção e extrai o array resultante do modelo criado
        $user = $this->entityPayload($this->collection(UsuarioCollection::class)->add($payload));

        return $this->safeUser($user);
    }

    /**
     * Padrão Just-In-Time (JIT): Retorna o perfil do Firestore para o usuário autenticado. 
     * Se o documento não existir, ele cria o perfil no banco de forma transparente e em tempo de execução.
     */
    public function userForAuthenticatedUser(array $authUser): array
    {
        $uid   = (string) ($authUser['uid'] ?? $authUser['id'] ?? '');
        $email = mb_strtolower(trim((string) ($authUser['email'] ?? '')));
        $user  = null;

        try {
            $collection = $this->collection(UsuarioCollection::class);

            // Estratégia de Busca 1: Tenta recuperar o registro apontando direto para o ID do documento (UID)
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

            if ($user === null && $uid !== '') {
                foreach (['uid', 'firebaseUid'] as $field) {
                    $query = $collection->where($field, '=', $uid)->limit(1);
                    foreach ($collection->list($query) as $entity) {
                        $user = $this->entityPayload($entity);
                        break 2;
                    }
                }
            }

            // Estratégia de Busca 2: Caso falhe por ID, realiza uma query de busca filtrando pelo e-mail cadastrado
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

        // AUTO-CADASTRADO JIT: Se após as buscas o usuário ainda não tiver dados no Firestore, cria-os na hora
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

        // Sincroniza e garante a coerência dos IDs de autenticação cruzados
        $user['uid'] = $uid !== '' ? $uid : ($user['uid'] ?? $user['firebaseUid'] ?? $user['id'] ?? '');
        $user['firebaseUid'] = $user['uid'];

        // Retorna o cruzamento dos dados do Auth com os dados complementares do Firestore
        return $this->safeUser([...$user, ...$authUser, 'id' => $user['uid']]);
    }

    /**
     * Sincroniza as informações do usuário contidas na sessão com o estado em tempo real no banco NoSQL.
     */
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

        // Se o banco falhar na leitura, o sistema usa os dados armazenados na própria sessão do PHP como escudo
        return $this->safeUser($user ?? $sessionUser);
    }

    /**
     * Aplica uma lista controlada de atualizações (White-listing) no perfil do usuário de forma segura.
     */
    public function updateUserFromSession(array $sessionUser, array $payload): array
    {
        $collection = $this->collection(UsuarioCollection::class);
        $entity     = $this->userEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Usuario autenticado nao foi encontrado no Firestore.');
        }

        // Lista restrita (White-list) de campos autorizados para gravação direta pelo formulário do usuário
        $allowed = [
            'nome', 'username', 'telefone', 'cidade', 'estado', 
            'cpf', 'fotoUrl', 'bio', 'nascimento', 'sexo'
        ];

        $updates = [];
        foreach ($allowed as $field) {
            if (array_key_exists($field, $payload)) {
                $updates[$field] = is_string($payload[$field]) ? trim($payload[$field]) : $payload[$field];
            }
        }

        // Tratamento de formatação especial em campos padronizados
        if (isset($updates['telefone'])) {
            $updates['telefone'] = $this->formatPhone((string) $updates['telefone']);
        }
        if (isset($updates['cpf'])) {
            $updates['cpf'] = $this->formatCpf((string) $updates['cpf']);
        }

        // Salva de fato a alteração no documento correspondente no Firestore
        $collection->update($entity, $updates);

        // Retorna a representação do usuário completamente recarregada e sincronizada
        return $this->userFromSession($this->safeUser([...$sessionUser, ...$updates]));
    }

    /**
     * Atualiza dados de configuração e metadados de uma conta do tipo Produtor/Lojas Parceiras.
     */
    public function updateProducerFromSession(array $sessionUser, array $payload): array
    {
        $collection = $this->collection(ProducerCollection::class);
        $entity     = $this->producerEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Produtor autenticado nao foi encontrado no Firestore.');
        }

        $allowed = [
            'nomeLoja', 'lojaBio', 'cnpj', 'categoria', 'email', 'telefone', 
            'fotoUrl', 'bannerUrl', 'cepOrigem', 'cidade', 'estado', 'endereco', 
            'retiradaLocal', 'envioCorreios', 'entregaLocal', 'pix', 
            'horarioSemanaInicio', 'horarioSemanaFim', 'horarioSabadoInicio', 'horarioSabadoFim',
            'fotosHistoria'
        ];

        $updates = [];
        foreach ($allowed as $field) {
            if (array_key_exists($field, $payload)) {
                $updates[$field] = is_string($payload[$field]) ? trim($payload[$field]) : $payload[$field];
            }
        }

        // Normalização interna de aliases e compatibilidade de chaves alternativas do legado
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
        if (isset($updates['fotosHistoria'])) {
            $updates['fotosHistoria'] = $this->normalizeUrlList($updates['fotosHistoria']);
        }
        if (isset($updates['nomeLoja']) || isset($updates['email']) || isset($updates['telefone']) || isset($updates['categoria'])) {
            $current = $this->entityPayload($entity);
            $updates['membros'] = $this->normalizeMembersPayload([...$current, ...$updates]);
        }
        
        // Atribuição direta para inputs booleanos do tipo checkbox de modalidades de entrega.
        // Só altera quando a tela realmente envia algum campo de entrega.
        if (
            array_key_exists('retiradaLocal', $payload)
            || array_key_exists('envioCorreios', $payload)
            || array_key_exists('entregaLocal', $payload)
        ) {
            $updates['retiradaLocal'] = array_key_exists('retiradaLocal', $payload);
            $updates['envioCorreios'] = array_key_exists('envioCorreios', $payload);
            $updates['entregaLocal'] = array_key_exists('entregaLocal', $payload);
        }

        $collection->update($entity, $updates);

        return $this->normalizeProducer([...$this->entityPayload($entity), ...$updates]);
    }

    /**
     * Cadastra um novo produto na vitrine, associando-o obrigatoriamente à sessão do produtor logado.
     */
    public function createProductForProducerSession(array $sessionUser, array $payload): array
    {
        $producerCollection = $this->collection(ProducerCollection::class);
        $producerEntity = $this->producerEntityFromSession($producerCollection, $sessionUser);

        if ($producerEntity === null) {
            throw new RuntimeException('Produtor autenticado nao foi encontrado no Firestore.');
        }

        $producer = $this->normalizeProducer($this->entityPayload($producerEntity));
        $producerId = (string) ($producer['id'] ?? '');

        if ($producerId === '') {
            throw new RuntimeException('Produtor autenticado nao foi encontrado no Firestore.');
        }

        $nome = trim((string) ($payload['nome'] ?? ''));
        if ($nome === '') {
            throw new RuntimeException('Informe o nome do produto.');
        }

        $categoria = trim((string) ($payload['categoria'] ?? ''));
        $preco = (float) str_replace(',', '.', (string) ($payload['preco'] ?? 0)); // Converte padrão de moeda BR (vírgula) para decimal flutuante
        $quantidade = max(0, (int) ($payload['quantidade'] ?? $payload['estoque'] ?? 0)); // Impede quantidade negativa acidental
        $imagem = trim((string) ($payload['imagemUrl'] ?? $payload['imagem'] ?? ''));

        // Estrutura de dados completa contendo todas as redundâncias históricas exigidas pelo frontend
        $product = [
            'nome'                 => $nome,
            'nome_produto'         => $nome,
            'descricao'            => trim((string) ($payload['descricao'] ?? '')),
            'preco'                => $preco,
            'preco_produto'        => $preco,
            'categoria'            => $categoria,
            'categorias'           => $categoria !== '' ? [$categoria] : [],
            'quantidade'           => $quantidade,
            'estoque'              => $quantidade,
            'disponivel'           => $quantidade > 0,
            'destaque'             => false,
            'produtorId'           => $producerId,
            'produtor_id'          => $producerId,
            'artesao'              => (string) ($producer['nomeLoja'] ?? $producer['nome'] ?? ''),
            'produtor'             => (string) ($producer['nomeLoja'] ?? $producer['nome'] ?? ''),
            'nome_produtor'        => (string) ($producer['nome'] ?? ''),
            'nome_loja'            => (string) ($producer['nomeLoja'] ?? $producer['nome'] ?? ''),
            'imagens'              => $imagem !== '' ? [$imagem] : [],
            'imagem'               => $imagem,
            'imagemUrl'            => $imagem,
            'img'                  => $imagem,
            'cor'                  => trim((string) ($payload['cor'] ?? '#b5a898')),
            'quantidadeAvaliacoes' => 0,
            'somaAvaliacoes'       => 0,
            'avaliacoes'           => 0,
            'avaliacao'            => 0,
            'estrelas'             => 0,
            'createdAt'            => date(DATE_ATOM), // Formato ISO 8601 padrão para datas no Firestore
            'updatedAt'            => date(DATE_ATOM),
        ];

        // Insere o produto no Firestore
        $created = $this->entityPayload($this->collection(ProductCollection::class)->add($product));
        
        // Incrementa de forma atômica e reativa o contador interno de produtos na conta do produtor parceiro
        $producerCollection->update($producerEntity, ['produtos' => (int) ($producer['produtos'] ?? 0) + 1]);

        return $this->normalizeProduct([...$product, ...$created]);
    }

    public function cartForSession(array $sessionUser): array
    {
        $user  = $this->userFromSession($sessionUser);
        $items = is_array($user['carrinho'] ?? null) ? $user['carrinho'] : [];

        return array_values(array_filter(array_map(function (array $item): ?array {
            $productId = (string) ($item['produtoId'] ?? $item['productId'] ?? $item['id'] ?? '');
            if ($productId === '') {
                return null;
            }

            $product = $this->product($productId);
            if ($product === null) {
                return null;
            }

            $quantity = max(1, (int) ($item['quantidade'] ?? $item['quantity'] ?? 1));

            return [
                'produtoId'  => $productId,
                'quantidade' => $quantity,
                'produto'    => $product,
                'subtotal'   => $quantity * (float) ($product['preco'] ?? 0),
            ];
        }, $items)));
    }

    public function addCartItemForSession(array $sessionUser, string $productId, int $quantity = 1): array
    {
        return $this->mutateCartForSession($sessionUser, $productId, max(1, $quantity), true);
    }

    public function updateCartItemForSession(array $sessionUser, string $productId, int $quantity): array
    {
        return $this->mutateCartForSession($sessionUser, $productId, max(0, $quantity), false);
    }

    public function removeCartItemForSession(array $sessionUser, string $productId): array
    {
        return $this->mutateCartForSession($sessionUser, $productId, 0, false);
    }

    public function ordersForProducerSession(array $sessionUser): array
    {
        $producer = $this->producerFromSession($sessionUser);
        $orders   = is_array($producer['pedidos'] ?? null) ? $producer['pedidos'] : [];

        return array_values(array_map(fn (array $order): array => $this->normalizeOrder($order), $orders));
    }

    public function updateOrderStatusForProducerSession(array $sessionUser, string $orderId, string $status): array
    {
        $collection = $this->collection(ProducerCollection::class);
        $entity     = $this->producerEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Produtor autenticado nao foi encontrado no Firestore.');
        }

        $producer = $this->entityPayload($entity);
        $orders   = is_array($producer['pedidos'] ?? null) ? $producer['pedidos'] : [];

        foreach ($orders as &$order) {
            if ((string) ($order['id'] ?? '') === $orderId) {
                $order['status'] = $status;
                $order['updatedAt'] = date(DATE_ATOM);
                break;
            }
        }
        unset($order);

        $collection->update($entity, ['pedidos' => $orders]);

        return array_values(array_map(fn (array $order): array => $this->normalizeOrder($order), $orders));
    }

    public function cartTotals(array $cart): array
    {
        $subtotal = array_sum(array_map(static fn (array $item): float => (float) ($item['subtotal'] ?? 0), $cart));

        return [
            'subtotal' => $subtotal,
            'desconto' => 0.0,
            'frete'    => 0.0,
            'total'    => $subtotal,
        ];
    }

    private function mutateCartForSession(array $sessionUser, string $productId, int $quantity, bool $increment): array
    {
        $productId = trim($productId);
        if ($productId === '') {
            throw new RuntimeException('Produto nao informado.');
        }

        $collection = $this->collection(UsuarioCollection::class);
        $entity     = $this->userEntityFromSession($collection, $sessionUser);

        if ($entity === null) {
            throw new RuntimeException('Usuario autenticado nao foi encontrado no Firestore.');
        }

        $user  = $this->entityPayload($entity);
        $items = is_array($user['carrinho'] ?? null) ? $user['carrinho'] : [];
        $found = false;

        foreach ($items as &$item) {
            $currentId = (string) ($item['produtoId'] ?? $item['productId'] ?? $item['id'] ?? '');
            if ($currentId !== $productId) {
                continue;
            }

            $currentQuantity = max(1, (int) ($item['quantidade'] ?? $item['quantity'] ?? 1));
            $nextQuantity = $increment ? $currentQuantity + $quantity : $quantity;
            $item['produtoId'] = $productId;
            $item['quantidade'] = $nextQuantity;
            $found = true;
            break;
        }
        unset($item);

        if (! $found && $quantity > 0) {
            $items[] = [
                'produtoId' => $productId,
                'quantidade' => $quantity,
                'adicionadoEm' => date(DATE_ATOM),
            ];
        }

        $items = array_values(array_filter($items, static fn (array $item): bool => (int) ($item['quantidade'] ?? 0) > 0));
        $collection->update($entity, ['carrinho' => $items]);

        return $this->cartForSession([...$sessionUser, 'carrinho' => $items]);
    }

    private function normalizeOrder(array $order): array
    {
        $items = is_array($order['itens'] ?? null) ? $order['itens'] : [];
        $first = $items[0] ?? [];

        return [
            'id'       => (string) ($order['id'] ?? $order['numero'] ?? ''),
            'cliente'  => (string) ($order['cliente'] ?? $order['clienteNome'] ?? $order['nomeCliente'] ?? 'Cliente Arace'),
            'endereco' => (string) ($order['endereco'] ?? $order['local'] ?? ''),
            'produto'  => (string) ($order['produto'] ?? $first['nome'] ?? $first['produto'] ?? 'Pedido Arace'),
            'qtd'      => (int) ($order['qtd'] ?? $order['quantidade'] ?? $first['quantidade'] ?? 1),
            'valor'    => (float) ($order['valor'] ?? $order['total'] ?? 0),
            'data'     => (string) ($order['data'] ?? $order['createdAt'] ?? ''),
            'status'   => (string) ($order['status'] ?? 'pendente'),
        ];
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
            'carrinho'  => is_array($user['carrinho'] ?? null) ? $user['carrinho'] : [],
            'isProdutor' => (bool) ($user['isProdutor'] ?? false),
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

        $uid = trim((string) ($sessionUser['uid'] ?? $sessionUser['id'] ?? $sessionUser['firebaseUid'] ?? ''));
        if ($uid !== '') {
            foreach (['uid', 'firebaseUid'] as $field) {
                $query = $collection->where($field, '=', $uid)->limit(1);
                foreach ($collection->list($query) as $entity) {
                    return $entity;
                }
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

        $uid = trim((string) ($sessionUser['uid'] ?? $sessionUser['id'] ?? $sessionUser['firebaseUid'] ?? ''));
        if ($uid !== '') {
            foreach (['uid', 'firebaseUid'] as $field) {
                $query = $collection->where($field, '=', $uid)->limit(1);
                foreach ($collection->list($query) as $entity) {
                    return $entity;
                }
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
        $uid = trim((string) ($payload['uid'] ?? $payload['firebaseUid'] ?? ''));
        if ($uid !== '') {
            $payload['uid'] = $uid;
            $payload['firebaseUid'] = $uid;
        }

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
        $payload['fotosHistoria']       = $this->normalizeUrlList($payload['fotosHistoria'] ?? []);
        $payload['membros']             = $this->normalizeMembersPayload($payload);

        $producer = $this->entityPayload($this->collection(ProducerCollection::class)->add($payload));

        // Reflete o novo papel no perfil do usuario: quem cadastra uma loja passa a ser produtor.
        // O vinculo usuario<->produtor e feito pelo uid; o e-mail e usado apenas como fallback.
        $uid   = (string) ($payload['uid'] ?? $payload['firebaseUid'] ?? '');
        $email = mb_strtolower(trim((string) ($payload['email'] ?? '')));
        if ($uid !== '' || $email !== '') {
            $this->markUserAsProducer($uid, $email);
        }

        return $producer;
    }

    /**
     * Marca o usuario (coleção Usuarios) como produtor (isProdutor = true).
     * Localiza pelo uid (ID do documento do usuario) e, em ultimo caso, pelo e-mail.
     * Falha de forma silenciosa: o cadastro do produtor nao deve quebrar se o usuario ainda nao existir.
     */
    private function markUserAsProducer(string $uid, string $email = ''): void
    {
        try {
            $collection = $this->collection(UsuarioCollection::class);

            // Busca principal: pelo uid, que e o proprio ID do documento do usuario.
            $uid = trim($uid);
            if ($uid !== '') {
                $entity = $collection->get($uid);
                if ($entity !== null) {
                    $collection->update($entity, ['isProdutor' => true]);

                    return;
                }

                foreach (['uid', 'firebaseUid'] as $field) {
                    $query = $collection->where($field, '=', $uid)->limit(1);
                    foreach ($collection->list($query) as $entity) {
                        $collection->update($entity, ['isProdutor' => true]);

                        return;
                    }
                }
            }

            // Fallback: pelo e-mail.
            $email = mb_strtolower(trim($email));
            if ($email !== '') {
                $query = $collection->where('email', '=', $email)->limit(1);
                foreach ($collection->list($query) as $entity) {
                    $collection->update($entity, ['isProdutor' => true]);

                    return;
                }
            }
        } catch (\Throwable $exception) {
            log_message('warning', 'Nao foi possivel marcar usuario como produtor: {message}', [
                'message' => $exception->getMessage(),
            ]);
        }
    }

    /**
     * Verifica se existe um documento na coleção Produtores atrelado ao uid informado.
     * O vinculo pode estar no proprio ID do documento (uid) ou num campo uid/firebaseUid.
     */
    private function producerExistsForUid(string $uid): bool
    {
        $uid = trim($uid);
        if ($uid === '') {
            return false;
        }

        try {
            $collection = $this->collection(ProducerCollection::class);

            // O vinculo pode estar no proprio ID do documento (uid) ...
            if ($collection->get($uid) !== null) {
                return true;
            }

            // ... ou num campo uid/firebaseUid dentro do documento.
            foreach (['uid', 'firebaseUid'] as $field) {
                $query = $collection->where($field, '=', $uid)->limit(1);
                foreach ($collection->list($query) as $entity) {
                    return true;
                }
            }
        } catch (\Throwable) {
            return false;
        }

        return false;
    }

    /**
     * Backfill: garante que TODOS os usuarios da coleção tenham o campo booleano "isProdutor".
     * Para cada usuario, o valor e definido como true quando existe uma loja/produtor associada
     * ao mesmo e-mail e false caso contrario. Retorna um resumo da operação.
     *
     * @return array{total:int, produtores:int, clientes:int, atualizados:int}
     */
    public function backfillIsProdutor(bool $force = false): array
    {
        $collection = $this->collection(UsuarioCollection::class);
        $resumo     = ['total' => 0, 'produtores' => 0, 'clientes' => 0, 'atualizados' => 0];

        foreach ($collection->list() as $entity) {
            $resumo['total']++;

            $payload = $this->entityPayload($entity);
            // O ID do documento do usuario e o proprio uid; o vinculo com Produtores e por uid.
            $uid     = trim((string) ($payload['id'] ?? $payload['uid'] ?? $payload['firebaseUid'] ?? ''));
            $isProdutor = $uid !== '' && $this->producerExistsForUid($uid);

            $isProdutor ? $resumo['produtores']++ : $resumo['clientes']++;

            // Só grava se o campo ainda não existir ou se estiver divergente (ou quando forçado).
            $atual = array_key_exists('isProdutor', $payload) ? (bool) $payload['isProdutor'] : null;
            if ($force || $atual === null || $atual !== $isProdutor) {
                $collection->update($entity, ['isProdutor' => $isProdutor]);
                $resumo['atualizados']++;
            }
        }

        return $resumo;
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

    /**
     * Extrai o "membro principal" do campo "membros" da coleção Produtores.
     * O campo pode chegar como um mapa único (chaves nomeadas) ou como uma lista
     * de membros; nos dois casos devolvemos um array associativo com os dados do membro.
     */
    private function primaryMember(mixed $membros): array
    {
        if (! is_array($membros) || $membros === []) {
            return [];
        }

        // Lista sequencial (0,1,2...) => é uma lista de membros, pegamos o primeiro.
        if (array_keys($membros) === range(0, count($membros) - 1)) {
            $first = $membros[0] ?? [];

            return is_array($first) ? $first : [];
        }

        // Caso contrário já é o mapa de um único membro.
        return $membros;
    }

    private function normalizeMembersPayload(array $producer): array
    {
        $existing = $producer['membros'] ?? [];
        $member = $this->primaryMember(is_array($existing) ? $existing : []);

        $nomeLoja = (string) ($producer['nomeLoja'] ?? $producer['nome_loja'] ?? $producer['nome'] ?? '');
        $email = mb_strtolower(trim((string) ($producer['email'] ?? $producer['email_comercial'] ?? '')));
        $telefone = (string) ($producer['telefone'] ?? $producer['telefone_comercial'] ?? '');

        $member = [
            ...$member,
            'uid' => (string) ($producer['uid'] ?? $producer['firebaseUid'] ?? $member['uid'] ?? ''),
            'nome' => $nomeLoja,
            'nomeLoja' => $nomeLoja,
            'nomeCompleto' => (string) ($producer['nomeCompleto'] ?? $member['nomeCompleto'] ?? $producer['nome'] ?? $nomeLoja),
            'email' => $email,
            'telefone' => $telefone !== '' ? $this->formatPhone($telefone) : '',
            'tipoArtesanato' => (string) ($producer['categoria'] ?? $producer['categoria_principal'] ?? $member['tipoArtesanato'] ?? ''),
            'tipoPessoa' => (string) ($producer['tipoPessoa'] ?? $member['tipoPessoa'] ?? (! empty($producer['cnpj']) ? 'juridica' : 'fisica')),
        ];

        return [array_filter($member, static fn ($value): bool => $value !== null && $value !== '')];
    }

    private function normalizeUrlList(mixed $urls): array
    {
        if (is_string($urls)) {
            $urls = preg_split('/[\r\n,]+/', $urls) ?: [];
        }

        if (! is_array($urls)) {
            return [];
        }

        return array_values(array_unique(array_filter(array_map(
            static fn ($url): string => trim((string) $url),
            $urls
        ), static fn (string $url): bool => $url !== '')));
    }

    private function normalizeProducer(array $producer): array
    {
        // A coleção "Produtores" guarda os dados de identificação do artesão dentro de "membros"
        // (que pode vir como um mapa único ou como uma lista de membros). Extraímos o membro
        // principal para conseguir ler nomeLoja/nomeCompleto/telefone/tipoArtesanato/tipoPessoa.
        $membro = $this->primaryMember($producer['membros'] ?? null);

        $nomeLoja     = (string) ($membro['nomeLoja'] ?? $producer['nomeLoja'] ?? $producer['nome_loja'] ?? '');
        $nomeCompleto = (string) ($membro['nomeCompleto'] ?? $producer['nomeCompleto'] ?? '');
        $telefoneRaw  = (string) ($membro['telefone'] ?? $producer['telefone'] ?? $producer['telefone_comercial'] ?? '');

        $producer['id']            = (string) ($producer['id'] ?? $producer['produtor_id'] ?? '');
        $producer['nome']          = $nomeLoja ?: ((string) ($producer['nome'] ?? '') ?: $nomeCompleto);
        $producer['nomeLoja']      = $nomeLoja ?: $producer['nome'];
        $producer['nomeCompleto']  = $nomeCompleto;
        $producer['tipoArtesanato'] = (string) ($membro['tipoArtesanato'] ?? $producer['tipoArtesanato'] ?? '');
        $producer['tipoPessoa']    = (string) ($membro['tipoPessoa'] ?? $producer['tipoPessoa'] ?? '');
        $producer['categoriaProduto'] = (string) ($producer['categoriaProduto'] ?? $producer['categoria'] ?? '');
        $producer['cep']           = (string) ($producer['cep'] ?? $producer['cepOrigem'] ?? '');
        $producer['endereco']      = (string) ($producer['endereco'] ?? '');
        $producer['stability']     = $producer['stability'] ?? '';
        $producer['lojaBio']  = $producer['lojaBio'] ?? $producer['bio'] ?? '';
        $producer['email']    = $producer['email'] ?? $producer['email_comercial'] ?? $membro['email'] ?? '';
        $producer['telefone'] = $telefoneRaw !== '' ? $this->formatPhone($telefoneRaw) : '';
        $producer['categoria'] = $producer['categoriaProduto'] ?: ($producer['categoria_principal'] ?? $producer['tipoArtesanato'] ?? '');
        $producer['fotosHistoria'] = $this->normalizeUrlList($producer['fotosHistoria'] ?? []);
        $producer['fotoUrl'] = $producer['fotoUrl']
            ?? $producer['lojaAvatar']
            ?? $producer['avatar']
            ?? $producer['imagemUrl']
            ?? $producer['imagem']
            ?? $producer['img']
            ?? $producer['foto']
            ?? '';
        $producer['lojaAvatar'] = $producer['fotoUrl'];
        $producer['bannerUrl'] = $producer['bannerUrl']
            ?? $producer['banner']
            ?? $producer['capaUrl']
            ?? $producer['lojaBanner']
            ?? ($producer['fotosHistoria'][0] ?? '');
        $producer['iniciais'] = $producer['iniciais'] ?? $this->initials($producer['nome']);
        $producer['produtos'] = (int) ($producer['produtos'] ?? $producer['total_produtos'] ?? 0);
        $producer['pedidos'] = is_array($producer['pedidos'] ?? null) ? $producer['pedidos'] : [];

        if ($producer['id'] === '') {
            $producer['id'] = url_title($producer['nome'], '-', true);
        }

        return $producer;
    }

    //Tranformar nome em sigla

    private function initials(string $name): string
    {
        $words   = preg_split('/\s+/', trim($name)) ?: [];
        $letters = array_map(static fn (string $word): string => mb_substr($word, 0, 1), array_slice($words, 0, 2));

        return mb_strtoupper(implode('', $letters)) ?: 'AR';
    }
//Validação telefone
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


    //alidação de CPF
    private function formatCpf(string $cpf): string
    {
        $digits = preg_replace('/\D/', '', $cpf) ?? '';

        if (strlen($digits) !== 11) {
            return trim($cpf);
        }

        return sprintf('%s.%s.%s-%s', substr($digits, 0, 3), substr($digits, 3, 3), substr($digits, 6, 3), substr($digits, 9));
    }
}
