<?php

namespace App\Libraries;

use DomainException;
use Kreait\Firebase\Auth\UserRecord;
use Kreait\Firebase\Exception\Auth\EmailExists;
use Kreait\Firebase\Auth\SignIn\FailedToSignIn;
use Kreait\Firebase\Exception\Auth\UserDisabled;
use Kreait\Firebase\Exception\Auth\UserNotFound;
use Kreait\Firebase\Exception\Auth\WeakPassword;
use RuntimeException;

final class AraceFirebaseAuth
{
    public function createUser(array $payload): array
    {
        $email    = mb_strtolower(trim((string) ($payload['email'] ?? '')));
        $password = (string) ($payload['senha'] ?? $payload['password'] ?? '');
        $name     = trim((string) ($payload['nome'] ?? ''));

        try {
            $record = $this->auth()->createUser(array_filter([
                'email'         => $email,
                'password'      => $password,
                'displayName'   => $name,
                'emailVerified' => false,
                'disabled'      => false,
            ], static fn ($value): bool => $value !== null && $value !== ''));

            return $this->recordPayload($record);
        } catch (EmailExists) {
            throw new DomainException('Este e-mail ja esta cadastrado.');
        } catch (WeakPassword) {
            throw new DomainException('A senha precisa ter pelo menos 6 caracteres.');
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao criar usuario no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            throw new RuntimeException('Nao foi possivel criar o usuario no Firebase Auth.', 0, $exception);
        }
    }

    public function signIn(string $email, string $password): array
    {
        $email = mb_strtolower(trim($email));

        try {
            $result = $this->auth()->signInWithEmailAndPassword($email, $password);
            $uid    = (string) ($result->firebaseUserId() ?? '');

            if ($uid === '') {
                throw new RuntimeException('Firebase Auth nao retornou o UID.');
            }

            $record = $this->auth()->getUser($uid);

            if ($record->disabled) {
                throw new DomainException('A conta esta desativada.');
            }

            $data = $result->data();

            return [
                ...$this->recordPayload($record),
                'id'           => $uid,
                'uid'          => $uid,
                'email'        => mb_strtolower(trim((string) ($data['email'] ?? $record->email ?? $email))),
                'nome'         => $record->displayName ?: ($data['displayName'] ?? null),
                'idToken'      => $result->idToken(),
                'refreshToken' => $result->refreshToken(),
            ];
        } catch (UserDisabled) {
            throw new DomainException('A conta esta desativada.');
        } catch (FailedToSignIn|UserNotFound) {
            throw new DomainException('E-mail ou senha incorretos.');
        } catch (DomainException $exception) {
            throw $exception;
        } catch (\Throwable $exception) {
            log_message('error', 'Falha ao autenticar no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            throw new RuntimeException('Nao foi possivel verificar a conta no Firebase Auth.', 0, $exception);
        }
    }

    public function emailExists(string $email): bool
    {
        try {
            $this->auth()->getUserByEmail(mb_strtolower(trim($email)));

            return true;
        } catch (UserNotFound) {
            return false;
        } catch (\Throwable $exception) {
            log_message('warning', 'Nao foi possivel conferir e-mail no Firebase Auth: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return true;
        }
    }

    public function deleteUser(string $uid): void
    {
        if ($uid === '') {
            return;
        }

        try {
            $this->auth()->deleteUser($uid);
        } catch (\Throwable) {
            // Se a compensacao falhar, o erro original do fluxo deve prevalecer.
        }
    }

    private function auth(): object
    {
        return service('firebase')->auth;
    }

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
