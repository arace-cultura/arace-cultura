<?php

// Define que o arquivo pertence à pasta padrão de controladores do CodeIgniter 4.
namespace App\Controllers;

// Importa os serviços externos/customizados de validação de dados e armazenamento de arquivos.
use App\Libraries\BrasilApiValidator; // Validador customizado (ex: checar CPF)
use App\Libraries\SupabaseStorage;   // Gerenciador de uploads para o Supabase

/**
 * AccountController lida com as páginas da conta do usuário comum (perfil, carrinho, favoritos)
 * e com o painel gerencial do produtor (dashboard, produtos, pedidos, configurações da loja).
 */
final class AccountController extends BaseController
{
    /**
     * Carrega a página visual do perfil do usuário comum.
     */
    public function profile()
    {
        // Retorna a view 'arace-perfil' injetando o array de dados do usuário logado
        return view('user/arace-perfil', $this->accountData());
    }

    /**
     * Carrega a página de configurações gerais da conta.
     */
    public function config()
    {
        return view('main/arace-config', $this->accountData());
    }

    /**
     * Carrega a página do carrinho de compras do usuário.
     */
    public function cart()
    {
        $data = $this->accountData();
        // Busca os itens do carrinho salvos no Firestore usando a sessão atual
        $cart = service('araceFirestore')->cartForSession($data['usuario'] ?? []);

        // Constrói a visualização misturando os dados do usuário, itens do carrinho e os valores somados (totais)
        return view('main/arace-carrinho', [
            ...$data,
            'carrinho' => $cart,
            'totais'   => service('araceFirestore')->cartTotals($cart),
        ]);
    }

    /**
     * Carrega a página de produtos favoritos do usuário.
     */
    public function favorites()
    {
        $data = $this->accountData();

        return view('user/arace-favoritos', [
            ...$data,
            'favoritos' => service('araceFirestore')->favoritesForSession($data['usuario'] ?? []),
        ]);
    }

    /**
     * Carrega o perfil público/visual do produtor.
     */
    public function producerProfile()
    {
        return view('user-producter/arace-producer-profile', $this->accountData());
    }

    /**
     * Carrega o Painel (Dashboard) principal do produtor, calculando suas métricas em tempo real.
     */
    public function producerDashboard()
    {
        $data = $this->accountData();
        $firestore = service('araceFirestore');
        
        // Pega a entidade de produtor vinculada ao usuário logado
        $produtor = $firestore->producerFromSession($data['usuario'] ?? []);
        $produtorId = (string) ($produtor['id'] ?? '');
        
        // Filtra a lista global de produtos para extrair apenas os que pertencem a este produtor
        $produtos = array_values(array_filter(
            $firestore->products(),
            static fn (array $produto): bool => $produtorId !== '' && (string) ($produto['produtorId'] ?? '') === $produtorId
        ));
        
        // Busca todos os pedidos que foram feitos para a loja deste produtor
        $pedidos = $firestore->ordersForProducerSession($data['usuario'] ?? []);
        
        // CÁLCULO DE MÉTRICAS:
        // 1. Soma o valor total de todos os pedidos para gerar o faturamento acumulado
        $faturamento = array_sum(array_map(static fn (array $pedido): float => (float) ($pedido['valor'] ?? 0), $pedidos));
        
        // 2. Conta quantos desses pedidos ainda estão marcados com o status 'pendente'
        $pendentes = count(array_filter($pedidos, static fn (array $pedido): bool => ($pedido['status'] ?? '') === 'pendente'));
        
        // 3. Extrai as notas (estrelas) de avaliação dos produtos do produtor
        $avaliacoes = array_values(array_filter(array_map(static fn (array $produto): float => (float) ($produto['estrelas'] ?? 0), $produtos)));

        // Renderiza o painel gerencial injetando as estatísticas calculadas
        return view('user-producter/arace-producer-painel-produtos', [
            ...$data,
            'produtor' => $produtor,
            'produtos' => $produtos,
            'pedidos' => $pedidos,
            'metricas' => [
                'faturamento' => $faturamento,
                'pedidos' => count($pedidos),
                'pendentes' => $pendentes,
                // Calcula a média das avaliações (soma tudo e divide pelo total de avaliações existentes)
                'avaliacao' => $avaliacoes === [] ? 0 : array_sum($avaliacoes) / count($avaliacoes),
            ],
        ]);
    }

    /**
     * Carrega a página de configurações internas do produtor.
     */
    public function producerConfig()
    {
        return view('user-producter/arace-producer-config', $this->accountData());
    }

    /**
     * Carrega a listagem de pedidos recebidos pelo produtor e agrupa as quantidades por status.
     */
    public function producerOrders()
    {
        $data = $this->accountData();
        $pedidos = service('araceFirestore')->ordersForProducerSession($data['usuario'] ?? []);
        
        // Conta a frequência de cada status (Ex: quantos 'pendente', quantos 'enviado')
        $stats = array_count_values(array_map(static fn (array $pedido): string => (string) ($pedido['status'] ?? 'pendente'), $pedidos));

        return view('user-producter/arace-producer-pedidos', [
            ...$data,
            'pedidos' => $pedidos,
            'pedidoStats' => [
                'pendente' => $stats['pendente'] ?? 0,
                'producao' => $stats['producao'] ?? 0,
                'enviado'  => $stats['enviado'] ?? 0,
                'entregue' => $stats['entregue'] ?? 0,
            ],
        ]);
    }

    /**
     * Exibe o perfil público da loja do produtor contendo seus dados e seu catálogo de produtos.
     */
    public function producerStoreProfile()
    {
        $data = $this->accountData();
        $firestore = service('araceFirestore');
        $data['produtor'] = $firestore->producerFromSession($data['usuario'] ?? []);
        $produtorId = (string) ($data['produtor']['id'] ?? '');
        
        // Carrega e filtra os produtos que serão expostos na vitrine da loja
        $data['produtos'] = array_values(array_filter(
            $firestore->products(),
            static fn (array $produto): bool => $produtorId !== '' && (string) ($produto['produtorId'] ?? '') === $produtorId
        ));

        return view('user-producter/arace-producer-profile-loja', $data);
    }

    /**
     * Abre o formulário visual de edição dos dados comerciais da loja (nome, bio, etc).
     */
    public function producerStoreConfig()
    {
        $data = $this->accountData();
        $data['produtor'] = service('araceFirestore')->producerFromSession($data['usuario'] ?? []);

        return view('user-producter/arace-producer-config-loja', $data);
    }

    /**
     * PROCESSA O FORMULÁRIO: Atualiza os dados cadastrais do perfil do usuário na sessão e no banco.
     */
    public function updateProfile()
    {
        // Validação de Segurança: Bloqueia a ação se o usuário não estiver logado
        $sessionUser = session()->get('arace_user') ?? [];
        if (! is_array($sessionUser) || $sessionUser === []) {
            return $this->response->setStatusCode(401)->setJSON([
                'success' => false,
                'message' => 'Sessao expirada. Entre novamente.',
            ]);
        }

        $payload = $this->requestPayload(); // Captura os dados enviados (seja JSON ou POST)
        $brasilApi = new BrasilApiValidator();

        // 1. Aplica regras rígidas de validação do CodeIgniter nos campos
        // 2. Se o CPF foi digitado, aciona a Brasil API para atestar se o número do CPF é matematicamente válido
        if (! $this->validateData($payload, [
            'nome'       => 'permit_empty|min_length[2]|max_length[120]',
            'username'   => 'permit_empty|max_length[60]',
            'bio'        => 'permit_empty|max_length[500]',
            'nascimento' => 'permit_empty|valid_date[Y-m-d]',
            'sexo'       => 'permit_empty|in_list[f,m,nb]',
            'telefone'   => 'permit_empty|max_length[30]',
            'cidade'     => 'permit_empty|max_length[120]',
            'estado'     => 'permit_empty|max_length[2]',
            'cpf'        => 'permit_empty|max_length[20]',
            'fotoUrl'    => 'permit_empty',
        ]) || (isset($payload['cpf']) && $payload['cpf'] !== '' && ! $brasilApi->validCpf((string) $payload['cpf']))) {
            
            // TRATAMENTO DE ERRO: Se a requisição veio de um formulário normal (HTML tradicional), redireciona de volta
            if (! $this->wantsJson()) {
                return redirect()
                    ->back()
                    ->withInput()
                    ->with('erro', 'Confira os dados do perfil.')
                    ->with('erros', $this->validator->getErrors());
            }

            // TRATAMENTO DE ERRO: Se veio via AJAX/JavaScript, responde um JSON com erro 422 (Unprocessable Entity)
            return $this->response->setStatusCode(422)->setJSON([
                'success' => false,
                'message' => 'Confira os dados do perfil.',
                'errors'  => $this->validator->getErrors(),
            ]);
        }

        try {
            // Remove o e-mail do payload por segurança, impedindo que o usuário mude o login indevidamente neste formulário
            unset($payload['email']);

            // Tenta pescar o arquivo físico da foto de perfil enviada
            $avatar = $this->uploadedFile('fotoUrl', 'avatar');
            if ($avatar !== null) {
                // Se a foto existe e é válida, faz o upload para o bucket de armazenamento do Supabase
                $payload['fotoUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $avatar,
                    (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'usuario')
                );
            }

            // Envia as atualizações de texto do perfil para o banco Firestore
            $usuario = service('araceFirestore')->updateUserFromSession($sessionUser, $payload);
            
            // Atualiza a sessão local do PHP para que o site passe a exibir o novo nome/foto imediatamente
            session()->set('arace_user', $usuario);

            if (! $this->wantsJson()) {
                return redirect()->back()->with('sucesso', 'Perfil atualizado.');
            }

            return $this->response->setJSON([
                'success' => true,
                'message' => 'Perfil atualizado.',
                'user'    => $usuario,
            ]);
        } catch (\Throwable $exception) {
            // Registra falhas graves (como queda de conexão ou erro no Supabase) nos logs do servidor
            log_message('error', 'Nao foi possivel salvar perfil: {message}', [
                'message' => $exception->getMessage(),
            ]);

            if (! $this->wantsJson()) {
                return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar o perfil agora. Confira o envio da imagem para o Supabase.');
            }

            return $this->response->setStatusCode(503)->setJSON([
                'success' => false,
                'message' => 'Nao foi possivel salvar o perfil agora. Confira o envio da imagem para o Supabase.',
            ]);
        }
    }

    /**
     * PROCESSA O FORMULÁRIO: Atualiza as configurações comerciais da loja do produtor.
     */
    public function updateProducerStore()
    {
        $sessionUser = session()->get('arace_user') ?? [];
        if (! is_array($sessionUser) || $sessionUser === []) {
            return redirect()->route('auth_login')->with('erro', 'Sessao expirada. Entre novamente.');
        }

        $payload = $this->request->getPost();

        // Valida o formato dos inputs da loja
        if (! $this->validateData($payload, [
            'nomeLoja'  => 'permit_empty|min_length[2]|max_length[120]',
            'lojaBio'   => 'permit_empty|max_length[1000]',
            'email'     => 'permit_empty|valid_email',
            'telefone'  => 'permit_empty|max_length[30]',
            'cnpj'      => 'permit_empty|max_length[30]',
            'categoria' => 'permit_empty|max_length[80]',
        ])) {
            return redirect()
                ->back()
                ->withInput()
                ->with('erro', 'Confira os dados da loja.')
                ->with('erros', $this->validator->getErrors());
        }

        try {
            // Controla o upload do arquivo do Logo da Loja
            $logo = $this->uploadedFile('fotoUrl');
            if ($logo !== null) {
                $payload['fotoUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $logo,
                    'loja-' . (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'produtor')
                );
            }

            // Controla o upload do arquivo do Banner da Loja
            $banner = $this->uploadedFile('bannerUrl');
            if ($banner !== null) {
                $payload['bannerUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $banner,
                    'banner-loja-' . (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'produtor')
                );
            }

            // Salva as alterações estruturais da loja no Firestore
            $produtor = service('araceFirestore')->updateProducerFromSession($sessionUser, $payload);

            return redirect()->back()->with('sucesso', 'Configuracoes da loja atualizadas.')->with('produtor', $produtor);
        } catch (\Throwable $exception) {
            log_message('error', 'Nao foi possivel salvar configuracoes da loja: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar a loja agora. Confira o envio da imagem para o Supabase.');
        }
    }

    /**
     * FUNÇÃO AUXILIAR INTERNA (PRIVADA)
     * Resgata, valida e atualiza os dados do usuário da sessão atual contra o banco Firestore,
     * garantindo consistência de dados entre as páginas.
     */
    private function accountData(): array
    {
        $sessionUser = session()->get('arace_user') ?? [];
        $usuario     = [];

        if (is_array($sessionUser)) {
            $firestore = service('araceFirestore');
            // Se o método 'userFromSession' existir no serviço, sincroniza buscando os dados mais recentes do Firestore
            $usuario   = method_exists($firestore, 'userFromSession')
                ? $firestore->userFromSession($sessionUser)
                : $sessionUser;
        }

        if ($usuario !== []) {
            session()->set('arace_user', $usuario);
        }

        return ['usuario' => $usuario];
    }

    /**
     * FUNÇÃO AUXILIAR INTERNA (PRIVADA)
     * Detecta dinamicamente a origem dos parâmetros de entrada da requisição.
     * Se for um JSON bruto (enviado via JavaScript Fetch/Axios), processa o JSON; caso contrário, lê como POST normal.
     */
    private function requestPayload(): array
    {
        $contentType = strtolower($this->request->getHeaderLine('Content-Type'));

        if (str_contains($contentType, 'application/json')) {
            try {
                return $this->request->getJSON(true) ?? [];
            } catch (\Throwable) {
                return [];
            }
        }

        return $this->request->getPost();
    }

    /**
     * FUNÇÃO AUXILIAR INTERNA (PRIVADA)
     * Analisa e isola de forma segura o arquivo binário enviado no formulário,
     * validando se o arquivo não sofreu corrupções no envio e se está pronto para ser movido.
     */
    private function uploadedFile(string ...$fieldNames): ?\CodeIgniter\HTTP\Files\UploadedFile
    {
        foreach ($fieldNames as $fieldName) {
            $file = $this->request->getFile($fieldName);

            if ($file === null || $file->getError() === UPLOAD_ERR_NO_FILE) {
                continue;
            }

            if (! $file->isValid() || $file->hasMoved()) {
                throw new \RuntimeException('A imagem enviada nao chegou valida ao servidor.');
            }

            return $file;
        }

        return null;
    }

    /**
     * FUNÇÃO AUXILIAR INTERNA (PRIVADA)
     * Identifica se o cliente que realizou a chamada espera uma resposta estruturada em JSON 
     * (comum em chamadas de API assíncronas feitas via JavaScript/AJAX).
     */
    private function wantsJson(): bool
    {
        return $this->request->isAJAX()
            || str_contains(strtolower($this->request->getHeaderLine('Accept')), 'application/json');
    }
}