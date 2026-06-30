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

    public function producerStoreProfile()
    {
        $data = $this->accountData();
        $data['produtor'] = service('araceFirestore')->producerFromSession($data['usuario'] ?? []);

        return view('user-producter/arace-producer-profile-loja', $data);
    }

    public function producerStoreConfig()
    {
        $data = $this->accountData();
        $data['produtor'] = service('araceFirestore')->producerFromSession($data['usuario'] ?? []);

        return view('user-producter/arace-producer-config-loja', $data);
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
        $brasilApi = new BrasilApiValidator();

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
            unset($payload['email']);

            $avatar = $this->uploadedFile('fotoUrl', 'avatar');
            if ($avatar !== null) {
                $payload['fotoUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $avatar,
                    (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'usuario')
                );
            }

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
        } catch (\Throwable $exception) {
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

    public function updateProducerStore()
    {
        $sessionUser = session()->get('arace_user') ?? [];
        if (! is_array($sessionUser) || $sessionUser === []) {
            return redirect()->route('auth_login')->with('erro', 'Sessao expirada. Entre novamente.');
        }

        $payload = $this->request->getPost();

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
            $logo = $this->uploadedFile('fotoUrl');
            if ($logo !== null) {
                $payload['fotoUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $logo,
                    'loja-' . (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'produtor')
                );
            }

            $banner = $this->uploadedFile('bannerUrl');
            if ($banner !== null) {
                $payload['bannerUrl'] = (new SupabaseStorage())->uploadAvatar(
                    $banner,
                    'banner-loja-' . (string) ($sessionUser['id'] ?? $sessionUser['email'] ?? 'produtor')
                );
            }

            $produtor = service('araceFirestore')->updateProducerFromSession($sessionUser, $payload);

            return redirect()->back()->with('sucesso', 'Configuracoes da loja atualizadas.')->with('produtor', $produtor);
        } catch (\Throwable $exception) {
            log_message('error', 'Nao foi possivel salvar configuracoes da loja: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar a loja agora. Confira o envio da imagem para o Supabase.');
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

    private function wantsJson(): bool
    {
        return $this->request->isAJAX()
            || str_contains(strtolower($this->request->getHeaderLine('Accept')), 'application/json');
    }
}
