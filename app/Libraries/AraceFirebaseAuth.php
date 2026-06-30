<?php

namespace App\Libraries;

use DomainException; // Exceção para erros de regras de negócio (ex: e-mail já em uso)
use Kreait\Firebase\Auth\UserRecord; // Objeto de retorno padrão do Firebase com os dados do usuário
use Kreait\Firebase\Exception\Auth\EmailExists; // Exceção disparada se o e-mail já existir no Firebase
use Kreait\Firebase\Auth\SignIn\FailedToSignIn; // Exceção disparada por credenciais incorretas no login
use Kreait\Firebase\Exception\Auth\UserDisabled; // Exceção disparada se o usuário tentar logar em conta banida
use Kreait\Firebase\Exception\Auth\UserNotFound; // Exceção disparada quando o UID ou e-mail procurado não existe
use Kreait\Firebase\Exception\Auth\WeakPassword; // Exceção disparada quando a senha não atinge os critérios mínimos do Firebase
use RuntimeException; // Exceção genérica para falhas inesperadas de infraestrutura (ex: rede fora do ar)

/**
 * AraceFirebaseAuth centraliza e simplifica a comunicação direta com o serviço de autenticação do Firebase.
 */
final class AraceFirebaseAuth
{
    /**
     * Registra um novo usuário no Firebase Auth.
     */
    public function createUser(array $payload): array
    {
        // Normaliza as entradas: e-mail em minúsculas e remove espaços vazios nas bordas
        $email    = mb_strtolower(trim((string) ($payload['email'] ?? '')));
        $password = (string) ($payload['senha'] ?? $payload['password'] ?? '');
        $name     = trim((string) ($payload['nome'] ?? ''));

        try {
            // Dispara o comando para o SDK criando a conta no Firebase, limpando campos vazios do array antes do envio
            $record = $this->auth()->createUser(array_filter([
                'email'         => $email,
                'password'      => $password,
                'displayName'   => $name,
                'emailVerified' => false,
                'disabled'      => false,
            ], static fn ($value): bool => $value !== null && $value !== ''));

            // Converte o objeto UserRecord retornado pelo Firebase em um array PHP limpo
            return $this->recordPayload($record);
            
        } catch (EmailExists) {
            // Intercepta o erro nativo e amortece para uma mensagem amigável que o frontend pode exibir direto na tela
            throw new DomainException('Este e-mail ja esta cadastrado.');
        } catch (WeakPassword) {
            throw new DomainException('A senha precisa ter pelo menos 6 caracteres.');
        } catch (\Throwable $exception) {
            // Registra nos logs do CodeIgniter o erro técnico real detalhado para análise do desenvolvedor
            log_message('error', 'Falha ao criar usuario no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            throw new RuntimeException('Nao foi possivel criar o usuario no Firebase Auth.', 0, $exception);
        }
    }

    /**
     * Realiza a autenticação (login) validando o e-mail e a senha fornecidos.
     */
    public function signIn(string $email, string $password): array
    {
        $email = mb_strtolower(trim($email));

        try {
            // Tenta fazer o login no Firebase Auth usando as credenciais passadas
            $result = $this->auth()->signInWithEmailAndPassword($email, $password);
            $uid    = (string) ($result->firebaseUserId() ?? '');

            if ($uid === '') {
                throw new RuntimeException('Firebase Auth nao retornou o UID.');
            }

            // Busca os dados cadastrais do usuário usando o UID retornado pelo login
            $record = $this->auth()->getUser($uid);

            // Verifica se a conta está bloqueada ou inativa no painel do Firebase
            if ($record->disabled) {
                throw new DomainException('A conta esta desativada.');
            }

            $data = $result->data();

            // Retorna um compilado contendo os dados do usuário + tokens JWT necessários para controle de APIs
            return [
                ...$this->recordPayload($record),
                'id'           => $uid,
                'uid'          => $uid,
                'email'        => mb_strtolower(trim((string) ($data['email'] ?? $record->email ?? $email))),
                'nome'         => $record->displayName ?: ($data['displayName'] ?? null),
                'idToken'      => $result->idToken(), // Token JWT de acesso de curta duração
                'refreshToken' => $result->refreshToken(), // Token de renovação de longa duração
            ];
            
        } catch (UserDisabled) {
            throw new DomainException('A conta esta desativada.');
        } catch (FailedToSignIn|UserNotFound) {
            // Une as duas exceções mais comuns de erro de acesso para evitar técnicas de enumeração de e-mails de usuários por invasores
            throw new DomainException('E-mail ou senha incorretos.');
        } catch (DomainException $exception) {
            // Re-dispara a exceção de negócio sem modificação
            throw $exception;
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao autenticar no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            throw new RuntimeException('Nao foi possivel verificar a conta no Firebase Auth.', 0, $exception);
        }
    }

    /**
     * Verifica rapidamente se um e-mail já está em uso na base do Firebase Auth.
     */
    public function emailExists(string $email): bool
    {
        try {
            $this->auth()->getUserByEmail(mb_strtolower(trim($email)));

            return true; // Se achou o usuário pelo e-mail, ele existe
        } catch (UserNotFound) {
            return false; // Se caiu na exceção UserNotFound, o e-mail está livre para cadastro
        } catch (\Throwable $exception) {
            log_message('warning', 'Nao foi possivel conferir e-mail no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            // Em caso de erro técnico na API, assume que o e-mail existe de forma defensiva para evitar duplicidade acidental
            return true;
        }
    }

    /**
     * Exclui permanentemente um usuário do Firebase Auth.
     * Muito útil para a mecânica de rollback (estorno) caso o banco Firestore falhe na criação do perfil.
     */
    public function deleteUser(string $uid): void
    {
        if ($uid === '') {
            return;
        }

        try {
            $this->auth()->deleteUser($uid);
        } catch (\Throwable) {
            // Silencia o erro intencionalmente. No fluxo de compensação pós-falha,
            // o erro crítico que travou a requisição principal deve ser preservado.
        }
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Recupera a instância pré-configurada do serviço do Firebase injetada no CodeIgniter 4.
     */
    private function auth(): object
    {
        return service('firebase')->auth;
    }

    /**
     * MÉTODO AUXILIAR INTERNO (PRIVADO)
     * Mapeia e traduz o objeto bruto de resposta do Firebase `UserRecord` para um formato de 
     * array nativo do PHP, removendo campos vazios ou não definidos.
     */
    private function recordPayload(UserRecord $record): array
    {
        return array_filter([
            'id'            => $record->uid,
            'uid'           => $record->uid,
            'email'         => $record->email,
            'nome'          => $record->displayName,
            'telefone'      => $record->phoneNumber,
            'emailVerified' => $record->emailVerified,
            'disabled'      => $record->disabled,
        ], static fn ($value): bool => $value !== null && $value !== '');
    }
}