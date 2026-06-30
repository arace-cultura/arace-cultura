<?php

namespace App\Controllers;

use DomainException;

final class AuthController extends BaseController
{
    public function index()
    {
        if (session()->get('arace_authenticated') === true && is_array(session()->get('arace_user'))) {
            return redirect()->route('user_arace_perfil');
        }

        return view('authentication/login-arace');
    }

    public function login()
    {
        $payload = $this->requestPayload();
        $email   = trim((string) ($payload['email'] ?? ''));
        $senha   = (string) ($payload['senha'] ?? '');
        $lembrar = filter_var($payload['lembrar'] ?? false, FILTER_VALIDATE_BOOLEAN);

        if (! $this->validateData(['email' => $email, 'senha' => $senha], [
            'email' => 'required|valid_email',
            'senha' => 'required',
        ])) {
            return $this->loginFailure('Informe um e-mail e uma senha validos.', 422);
        }

        try {
            $authUser = service('araceFirebaseAuth')->signIn($email, $senha);
            $user     = $this->profileForAuthenticatedUser($authUser);

            session()->regenerate(true);
            session()->set([
                'arace_authenticated' => true,
                'arace_user'          => $user,
                'arace_remember'      => $lembrar,
            ]);

            if ($this->wantsJson()) {
                return $this->response->setJSON([
                    'success'  => true,
                    'redirect' => url_to('user_arace_perfil'),
                    'user'     => $user,
                ]);
            }

            return redirect()->route('user_arace_perfil');
        } catch (DomainException $e) {
            $status = $e->getMessage() === 'E-mail ou senha incorretos.' ? 401 : 403;

            return $this->loginFailure($e->getMessage(), $status);
        } catch (\Throwable) {
            return $this->loginFailure('Nao foi possivel verificar a conta agora. Tente novamente.', 503);
        }
    }

    private function profileForAuthenticatedUser(array $authUser): array
    {
        try {
            return service('araceFirestore')->userForAuthenticatedUser($authUser);
        } catch (\Throwable $exception) {
            log_message('warning', 'Login liberado pelo Firebase Auth sem perfil Firestore: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return array_filter([
                'id'       => (string) ($authUser['uid'] ?? $authUser['id'] ?? ''),
                'uid'      => (string) ($authUser['uid'] ?? $authUser['id'] ?? ''),
                'nome'     => (string) ($authUser['nome'] ?? 'Usuario'),
                'email'    => (string) ($authUser['email'] ?? ''),
                'telefone' => $authUser['telefone'] ?? null,
            ], static fn ($value): bool => $value !== null && $value !== '');
        }
    }

    public function profile()
    {
        return view('user/arace-perfil', [
            'usuario' => session()->get('arace_user') ?? [],
        ]);
    }

    public function logout()
    {
        session()->remove(['arace_authenticated', 'arace_user', 'arace_remember']);
        session()->regenerate(true);

        return redirect()->route('auth_login')->with('sucesso', 'Voce saiu da sua conta.');
    }

    private function loginFailure(string $message, int $status)
    {
        if ($this->wantsJson()) {
            return $this->response->setStatusCode($status)->setJSON([
                'success' => false,
                'message' => $message,
            ]);
        }

        return redirect()->back()->withInput()->with('erro', $message);
    }

    private function wantsJson(): bool
    {
        return $this->request->isAJAX()
            || str_contains(strtolower($this->request->getHeaderLine('Accept')), 'application/json');
    }

    private function requestPayload(): array
    {
        $contentType = strtolower($this->request->getHeaderLine('Content-Type'));

        if (! str_contains($contentType, 'application/json')) {
            return $this->request->getPost();
        }

        try {
            return $this->request->getJSON(true) ?? [];
        } catch (\Throwable) {
            return [];
        }
    }
}
