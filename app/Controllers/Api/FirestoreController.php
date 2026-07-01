<?php

// Define a pasta virtual onde este arquivo reside. Como é uma API, fica dentro de Controllers\Api.
namespace App\Controllers\Api;

// Importa as bibliotecas e classes necessárias para o funcionamento do controlador.
use App\Libraries\AraceFirestore; // Serviço customizado para lidar com o banco Firestore
use App\Libraries\SupabaseStorage; // Serviço customizado para lidar com upload de arquivos no Supabase
use CodeIgniter\HTTP\ResponseInterface; // Interface padrão de respostas HTTP do CodeIgniter
use CodeIgniter\RESTful\ResourceController; // Classe base do CodeIgniter para criação de APIs RESTful

/**
 * FirestoreController gerencia as requisições (endpoints) da API relacionadas aos dados do Firestore.
 */
final class FirestoreController extends ResourceController
{
    /**
     * Retorna a lista de produtos.
     * Pode receber um parâmetro 'featured' (destaques) via URL (GET).
     */
    public function products(): ResponseInterface
    {
        return $this->respond([
            'source' => 'firestore',
            // O (bool) converte o parâmetro da URL para verdadeiro ou falso
            'data'   => (new AraceFirestore())->products((bool) $this->request->getGet('featured')),
        ]);
    }

    /**
     * Retorna os detalhes de um único produto com base no ID fornecido na URL.
     */
    public function product(string $id): ResponseInterface
    {
        $product = (new AraceFirestore())->product($id);

        // Se o produto não existir (null), retorna um erro 404 (Not Found).
        // Caso contrário, retorna os dados do produto com sucesso.
        return $product === null
            ? $this->failNotFound('Produto nao encontrado.')
            : $this->respond(['source' => 'firestore', 'data' => $product]);
    }

    /**
     * Retorna a lista de todos os produtores cadastrados.
     */
    public function producers(): ResponseInterface
    {
        return $this->respond([
            'source' => 'firestore',
            'data'   => (new AraceFirestore())->producers(),
        ]);
    }

    /**
     * Retorna os itens que estão no carrinho de compras do usuário logado e os totais calculados.
     */
    public function cart(): ResponseInterface
    {
        $cart = service('araceFirestore')->cartForSession($this->sessionUser());

        return $this->respond([
            'source' => 'firestore',
            'data'   => [
                'items'  => $cart,
                'totais' => service('araceFirestore')->cartTotals($cart),
            ],
        ]);
    }

    /**
     * Adiciona um novo item ao carrinho de compras.
     */
    public function addCartItem(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        
        $cart = service('araceFirestore')->addCartItemForSession(
            $this->sessionUser(),
            (string) ($payload['produtoId'] ?? $payload['productId'] ?? ''),
            (int) ($payload['quantidade'] ?? $payload['quantity'] ?? 1) // Define quantidade como 1 se não for enviada
        );

        // Retorna o carrinho atualizado com os novos totais
        return $this->respond([
            'source' => 'firestore',
            'data'   => [
                'items'  => $cart,
                'totais' => service('araceFirestore')->cartTotals($cart),
            ],
        ]);
    }

    /**
     * Atualiza a quantidade de um item que já está no carrinho.
     */
    public function updateCartItem(string $productId): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        
        $cart = service('araceFirestore')->updateCartItemForSession(
            $this->sessionUser(),
            $productId,
            (int) ($payload['quantidade'] ?? $payload['quantity'] ?? 1)
        );

        return $this->respond([
            'source' => 'firestore',
            'data'   => [
                'items'  => $cart,
                'totais' => service('araceFirestore')->cartTotals($cart),
            ],
        ]);
    }

    /**
     * Remove completamente um item do carrinho.
     */
    public function removeCartItem(string $productId): ResponseInterface
    {
        $cart = service('araceFirestore')->removeCartItemForSession($this->sessionUser(), $productId);

        return $this->respond([
            'source' => 'firestore',
            'data'   => [
                'items'  => $cart,
                'totais' => service('araceFirestore')->cartTotals($cart),
            ],
        ]);
    }

    /**
     * Retorna a lista de pedidos feitos na loja de um produtor específico (usuário logado).
     */
    public function producerOrders(): ResponseInterface
    {
        return $this->respond([
            'source' => 'firestore',
            'data'   => service('araceFirestore')->ordersForProducerSession($this->sessionUser()),
        ]);
    }

    /**
     * Atualiza o status de um pedido (Ex: de "pendente" para "enviado").
     */
    public function updateProducerOrder(string $orderId): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();

        return $this->respond([
            'source' => 'firestore',
            'data'   => service('araceFirestore')->updateOrderStatusForProducerSession(
                $this->sessionUser(),
                $orderId,
                (string) ($payload['status'] ?? 'pendente')
            ),
        ]);
    }

    /**
     * Cria um novo produto no catálogo do produtor, incluindo upload da imagem.
     */
    public function createProducerProduct(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        
        // Limpa os dados enviados, garantindo que apenas os campos permitidos continuem no array
        $payload = $this->cleanPayload($payload, [
            'nome', 'descricao', 'preco', 'categoria', 'estoque', 'imagem', 'imagemUrl', 'cor',
        ]);

        // Regras de validação de segurança e formato dos dados
        if (! $this->validateData($payload, [
            'nome'      => 'required|min_length[2]|max_length[140]',
            'descricao' => 'permit_empty|max_length[1200]',
            'preco'     => 'required',
            'categoria' => 'permit_empty|max_length[80]',
            'estoque'   => 'permit_empty|integer',
            'imagemUrl' => 'permit_empty|valid_url_strict',
            'imagem'    => 'permit_empty|valid_url_strict',
        ])) {
            // Se falhar na validação, devolve os erros para o frontend
            return $this->failValidationErrors($this->validator->getErrors());
        }

        try {
            $sessionUser = $this->sessionUser();
            $producer = service('araceFirestore')->producerFromSession($sessionUser);
            
            // Verifica se uma imagem (arquivo físico) foi enviada na requisição

            $image = $this->request->getFile('imagemArquivo');

            // Se houver arquivo de imagem e não houver erro no upload, envia para o Supabase
            if ($image !== null && $image->getError() !== UPLOAD_ERR_NO_FILE) {
                $payload['imagemUrl'] = (new SupabaseStorage())->uploadProductImage(
                    $image,
                    (string) ($producer['id'] ?? $sessionUser['id'] ?? $sessionUser['email'] ?? 'produtor')
                );
            }

            // Salva os dados do produto no Firestore e retorna código HTTP 201 (Created)
            return $this->respondCreated([
                'source' => 'firestore',
                'data'   => service('araceFirestore')->createProductForProducerSession($sessionUser, $payload),
            ]);
        } catch (\Throwable $exception) {
            // Se algo der errado (ex: erro no servidor, falha de rede), registra no log interno
            log_message('error', 'Nao foi possivel salvar produto do produtor: {message}', [
                'message' => $exception->getMessage(),
            ]);

            // Devolve um erro 500 para o usuário não ficar travado
            return $this->failServerError('Nao foi possivel salvar o produto no Firestore.');
        }
    }

    /**
     * Cria um novo usuário (cliente). Ele é criado primeiro no Firebase Auth, depois no Firestore.
     */
    public function createUser(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        $payload = $this->cleanPayload($payload, ['nome', 'email', 'senha']);

        // Validações básicas (nome, email válido, senha de pelo menos 6 caracteres)
        if (! $this->validateData($payload, [
            'nome'  => 'required|min_length[2]|max_length[120]',
            'email' => 'required|valid_email',
            'senha' => 'required|min_length[6]',
        ])) {
            return $this->failValidationErrors($this->validator->getErrors());
        }

        try {
            // Passo 1: Cria o usuário no sistema de autenticação (Firebase Auth)
            $authUser = service('araceFirebaseAuth')->createUser($payload);

            // Passo 2: Combina os dados do payload com os dados gerados pelo Auth (como o UID)
            // e salva no banco de dados Firestore
            return $this->respondCreated([
                'source' => 'firebase-auth+firestore',
                'data'   => (new AraceFirestore())->createUser([...$payload, ...$authUser]),
            ]);
        } catch (\DomainException $exception) {
            // Captura erros de regra de negócio (ex: email já existe)
            return $this->failValidationErrors($exception->getMessage());
        } catch (\Throwable) {
            // ROLLBACK: Se deu erro na hora de salvar no Firestore, mas o usuário foi criado no Auth,
            // deleta do Auth para não ter um usuário "fantasma" que não existe no banco de dados.
            if (! empty($authUser['uid'])) {
                service('araceFirebaseAuth')->deleteUser((string) $authUser['uid']);
            }

            return $this->failServerError('Nao foi possivel criar o cliente no Firebase Auth.');
        }
    }

    /**
     * Cria um perfil de produtor no banco de dados.
     */
    public function createProducer(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        $payload = $this->cleanPayload($payload, [
            'nome', 'nomeLoja', 'email', 'telefone', 'cpf', 'cnpj', 'categoria', 'distritoId',
        ]);

        if (! $this->validateData($payload, [
            'nome'  => 'required|min_length[2]|max_length[120]',
            'email' => 'required|valid_email',
        ])) {
            return $this->failValidationErrors($this->validator->getErrors());
        }

        try {
            return $this->respondCreated([
                'source' => 'firestore',
                'data'   => (new AraceFirestore())->createProducer($payload),
            ]);
        } catch (\Throwable) {
            return $this->failServerError('Nao foi possivel salvar o produtor no Firestore.');
        }
    }

    /**
     * FUNÇÃO AUXILIAR (PRIVADA)
     * Filtra os dados recebidos da requisição, mantendo apenas as chaves permitidas 
     * no array `$allowed` e removendo valores nulos ou vazios. Isso previne Injeção de Dados (Mass Assignment).
     */
    private function cleanPayload(array $payload, array $allowed): array
    {
        return array_filter(
            array_intersect_key($payload, array_flip($allowed)),
            static fn ($value): bool => $value !== null && $value !== ''
        );
    }

    /**
     * FUNÇÃO AUXILIAR (PRIVADA)
     * Busca os dados do usuário atualmente logado que estão salvos na sessão do PHP.
     */
    private function sessionUser(): array
    {
        $user = session()->get('arace_user');

        // Garante que o retorno será sempre um Array, mesmo que a sessão esteja vazia.
        return is_array($user) ? $user : [];
    }
}