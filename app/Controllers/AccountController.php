<?php

namespace App\Controllers;

use App\Libraries\BrasilApiValidator;
use App\Libraries\SupabaseStorage;

final class AccountController extends BaseController
{
    public function profile()
    {
        return view('user/arace-perfil', $this->accountData());
    }

    public function config()
    {
        return view('main/arace-config', $this->accountData());
    }

    public function producerProfile()
    {
        return view('user-producter/arace-producer-profile', $this->accountData());
    }

    public function producerConfig()
    {
        return view('user-producter/arace-producer-config', $this->accountData());
    }

    public function producerStoreConfig()
    {
        return view('user-producter/arace-producer-config-loja', $this->accountData());
    }

    public function updateProfile()
    {
        $sessionUser = session()->get('arace_user') ?? [];
        if (! is_array($sessionUser) || $sessionUser === []) {
            return $this->response->setStatusCode(401)->setJSON([
                'success' => false,
                'message' => 'Sessao expirada. Entre novamente.',
            ]);
        }

        $payload = $this->requestPayload();
        $avatar  = $this->request->getFile('avatar');
        $brasilApi = new BrasilApiValidator();

        if ($avatar !== null && $avatar->isValid() && ! $avatar->hasMoved()) {
            $payload['avatar'] = (new SupabaseStorage())->uploadAvatar(
                $avatar,
                (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'usuario')
            );
        }

        if (! $this->validateData($payload, [
            'nome'       => 'permit_empty|min_length[2]|max_length[120]',
            'username'   => 'permit_empty|max_length[60]',
            'bio'        => 'permit_empty|max_length[500]',
            'nascimento' => 'permit_empty|valid_date[Y-m-d]',
            'genero'     => 'permit_empty|in_list[f,m,nb]',
            'email'      => 'permit_empty|valid_email',
            'telefone'   => 'permit_empty|max_length[30]',
            'cidade'     => 'permit_empty|max_length[120]',
            'estado'     => 'permit_empty|max_length[2]',
            'cpf'        => 'permit_empty|max_length[20]',
            'avatar'     => 'permit_empty',
        ]) || (isset($payload['cpf']) && $payload['cpf'] !== '' && ! $brasilApi->validCpf((string) $payload['cpf']))) {
            if (! $this->wantsJson()) {
                return redirect()
                    ->back()
                    ->withInput()
                    ->with('erro', 'Confira os dados do perfil.')
                    ->with('erros', $this->validator->getErrors());
            }

            return $this->response->setStatusCode(422)->setJSON([
                'success' => false,
                'message' => 'Confira os dados do perfil.',
                'errors'  => $this->validator->getErrors(),
            ]);
        }

        try {
            $usuario = service('araceFirestore')->updateUserFromSession($sessionUser, $payload);
            session()->set('arace_user', $usuario);

            if (! $this->wantsJson()) {
                return redirect()->back()->with('sucesso', 'Perfil atualizado.');
            }

            return $this->response->setJSON([
                'success' => true,
                'message' => 'Perfil atualizado.',
                'user'    => $usuario,
            ]);
        } catch (\Throwable) {
            if (! $this->wantsJson()) {
                return redirect()->back()->with('erro', 'Nao foi possivel salvar o perfil agora.');
            }

            return $this->response->setStatusCode(503)->setJSON([
                'success' => false,
                'message' => 'Nao foi possivel salvar o perfil agora.',
            ]);
        }
    }

    private function accountData(): array
    {
        $sessionUser = session()->get('arace_user') ?? [];
        $usuario     = [];

        if (is_array($sessionUser)) {
            $firestore = service('araceFirestore');
            $usuario   = method_exists($firestore, 'userFromSession')
                ? $firestore->userFromSession($sessionUser)
                : $sessionUser;
        }

        if ($usuario !== []) {
            session()->set('arace_user', $usuario);
        }

        return ['usuario' => $usuario];
    }

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

    private function wantsJson(): bool
    {
        return $this->request->isAJAX()
            || str_contains(strtolower($this->request->getHeaderLine('Accept')), 'application/json');
    }
}
