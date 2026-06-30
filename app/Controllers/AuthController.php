<?php

namespace App\Controllers;

use DomainException; // Importa a exceção usada para erros de regras de negócio (ex: credenciais incorretas)

/**
 * AuthController lida com as rotas de entrada, saída e validação de login.
 * O termo "final" impede que outras classes herdem deste controlador.
 */
final class AuthController extends BaseController
{
    /**
     * Exibe a tela de login. 
     * Se o usuário já estiver logado, redireciona-o direto para o perfil.
     */
    public function index()
    {
        // Verifica se os dados da sessão indicam que o usuário já passou pela autenticação
        if (session()->get('arace_authenticated') === true && is_array(session()->get('arace_user'))) {
            return redirect()->route('user_arace_perfil');
        }

        // Caso não esteja logado, exibe o arquivo visual da tela de login
        return view('authentication/login-arace');
    }

    /**
     * Processa a tentativa de login (submissão do formulário ou requisição AJAX).
     */
    public function login()
    {
        // Captura o corpo da requisição de forma limpa (POST ou JSON)
        $payload = $this->requestPayload();
        $email   = trim((string) ($payload['email'] ?? ''));
        $senha   = (string) ($payload['senha'] ?? '');
        $lembrar = filter_var($payload['lembrar'] ?? false, FILTER_VALIDATE_BOOLEAN); // Converte para verdadeiro ou falso "puro"

        // Validação dos dados informados contra regras básicas de e-mail e preenchimento
        if (! $this->validateData(['email' => $email, 'senha' => $senha], [
            'email' => 'required|valid_email',
            'senha' => 'required',
        ])) {
            return $this->loginFailure('Informe um e-mail e uma senha validos.', 422);
        }

        try {
            // Passo 1: Autentica o usuário no serviço externo Firebase Auth
            $authUser = service('araceFirebaseAuth')->signIn($email, $senha);
            
            // Passo 2: Busca o perfil detalhado dele no banco NoSQL Firestore
            $user     = $this->profileForAuthenticatedUser($authUser);

            // Segurança da Sessão: Regenera o ID do cookie para evitar ataques de fixação de sessão
            session()->regenerate(true);
            
            // Grava os dados essenciais na sessão do servidor PHP
            session()->set([
                'arace_authenticated' => true,
                'arace_user'          => $user,
                'arace_remember'      => $lembrar,
            ]);

            // Se o frontend for uma aplicação JS (SPA, Axios, Fetch), retorna uma resposta JSON de sucesso
            if ($this->wantsJson()) {
                return $this->response->setJSON([
                    'success'  => true,
                    'redirect' => url_to('user_arace_perfil'),
                    'user'     => $user,
                ]);
            }

            // Se for um envio de formulário tradicional HTML, redireciona o navegador
            return redirect()->route('user_arace_perfil');
            
        } catch (DomainException $e) {
            // Captura erros intencionais do sistema (ex: senha errada).
            // Define o código HTTP: 401 para erro de senha/email e 403 para bloqueios estruturais.
            $status = $e->getMessage() === 'E-mail ou senha incorretos.' ? 401 : 403;

            return $this->loginFailure($e->getMessage(), $status);
        } catch (\Throwable) {
            // Captura falhas inesperadas (como queda de conexão com o Firebase)
            return $this->loginFailure('Nao foi possivel verificar a conta agora. Tente novamente.', 503);
        }
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Conecta os dados gerados pelo Firebase Auth com o registro correspondente no Firestore.
     */
    private function profileForAuthenticatedUser(array $authUser): array
    {
        try {
            return service('araceFirestore')->userForAuthenticatedUser($authUser);
        } catch (\Throwable $exception) {
            // Mecanismo de Fallback (Contingência): Se o Firebase Auth aceitou o login mas o Firestore falhou,
            // o sistema registra um aviso no log do servidor mas cria um perfil "temporário" na sessão 
            // usando os dados do Auth para evitar que o cliente sofra com tela preta ou erro interno.
            log_message('warning', 'Login liberado pelo Firebase Auth sem perfil Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            // Filtra e remove chaves nulas ou vazias do array improvisado
            return array_filter([
                'id'       => (string) ($authUser['uid'] ?? $authUser['id'] ?? ''),
                'uid'      => (string) ($authUser['uid'] ?? $authUser['id'] ?? ''),
                'nome'     => (string) ($authUser['nome'] ?? 'Usuario'),
                'email'    => (string) ($authUser['email'] ?? ''),
                'telefone' => $authUser['telefone'] ?? null,
            ], static fn ($value): bool => $value !== null && $value !== '');
        }
    }

    /**
     * Carrega a página visual do perfil puxando as informações armazenadas na sessão.
     */
    public function profile()
    {
        return view('user/arace-perfil', [
            'usuario' => session()->get('arace_user') ?? [],
        ]);
    }

    /**
     * Encerra a sessão ativa do usuário.
     */
    public function logout()
    {
        // Limpa os dados de autenticação e identificação da sessão
        session()->remove(['arace_authenticated', 'arace_user', 'arace_remember']);
        
        // Destrói a sessão anterior completamente por motivos de segurança cibernética
        session()->regenerate(true);

        // Envia o usuário de volta à tela de login com uma mensagem de confirmação rápida (flash data)
        return redirect()->route('auth_login')->with('sucesso', 'Voce saiu da sua conta.');
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Padroniza o comportamento de erro no login dependendo do tipo de requisição feita (JSON ou HTML).
     */
    private function loginFailure(string $message, int $status)
    {
        if ($this->wantsJson()) {
            return $this->response->setStatusCode($status)->setJSON([
                'success' => false,
                'message' => $message,
            ]);
        }

        // Redireciona para a página anterior mantendo os inputs digitados (menos a senha) e injetando a mensagem de erro
        return redirect()->back()->withInput()->with('erro', $message);
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Detecta se o cliente espera receber uma resposta estruturada em formato JSON.
     */
    private function wantsJson(): bool
    {
        return $this->request->isAJAX()
            || str_contains(strtolower($this->request->getHeaderLine('Accept')), 'application/json');
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Isola e normaliza a leitura dos dados que entraram na requisição.
     * Trata de forma transparente o recebimento de formulários HTML (x-www-form-urlencoded) e payloads JSON.
     */
    private function requestPayload(): array
    {
        $contentType = strtolower($this->request->getHeaderLine('Content-Type'));

        if (! str_contains($contentType, 'application/json')) {
            return $this->request->getPost();
        }

        try {
            return $this->request->getJSON(true) ?? [];
        } catch (\Throwable) {
            return []; // Retorna um array vazio caso o JSON enviado esteja quebrado/inválido
        }
    }
}