<?php

namespace App\Controllers;

use App\Libraries\AraceFirestore;
use DomainException;

final class AuthController extends BaseController
{
    public function index()
    {
        if (session()->get('arace_authenticated') === true && is_array(session()->get('arace_user'))) {
            return redirect()->to('/usuario/arace-perfil');
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
            $user = (new AraceFirestore())->authenticateUser($email, $senha);

            if ($user === null) {
                return $this->loginFailure('E-mail ou senha incorretos.', 401);
            }

            session()->regenerate(true);
            session()->set([
                'arace_authenticated' => true,
                'arace_user'          => $user,
                'arace_remember'      => $lembrar,
            ]);

            if ($this->wantsJson()) {
                return $this->response->setJSON([
                    'success'  => true,
                    'redirect' => '/usuario/arace-perfil',
                    'user'     => $user,
                ]);
            }

            return redirect()->to('/usuario/arace-perfil');
        } catch (DomainException $e) {
            return $this->loginFailure($e->getMessage(), 403);
        } catch (\Throwable) {
            return $this->loginFailure('Nao foi possivel verificar a conta agora. Tente novamente.', 503);
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

        return redirect()->to('/login')->with('sucesso', 'Voce saiu da sua conta.');
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
