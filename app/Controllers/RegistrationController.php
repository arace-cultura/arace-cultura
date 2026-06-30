<?php

namespace App\Controllers;

use App\Libraries\BrasilApiValidator;
use DomainException;

final class RegistrationController extends BaseController
{
    public function user()
    {
        $payload = $this->request->getPost(['nome', 'email', 'telefone', 'senha', 'confirmarSenha']);

        if (! $this->validateData($payload, [
            'nome'           => 'required|min_length[2]|max_length[120]',
            'email'          => 'required|valid_email',
            'telefone'       => 'permit_empty|max_length[30]',
            'senha'          => 'required|min_length[6]',
            'confirmarSenha' => 'required|matches[senha]',
        ])) {
            return redirect()
                ->back()
                ->withInput()
                ->with('erro', 'Confira os dados do cadastro.')
                ->with('erros', $this->validator->getErrors());
        }

        unset($payload['confirmarSenha']);

        try {
            $authUser = service('araceFirebaseAuth')->createUser($payload);
            $user = service('araceFirestore')->createUser([...$payload, ...$authUser]);
            session()->regenerate(true);
            session()->set([
                'arace_authenticated' => true,
                'arace_user'          => $user,
                'arace_remember'      => false,
            ]);

            return redirect()->route('user_arace_perfil')->with('sucesso', 'Conta criada com sucesso.');
        } catch (DomainException $e) {
            return redirect()->back()->withInput()->with('erro', $e->getMessage());
        } catch (\Throwable $e) {
            if (! empty($authUser['uid'])) {
                service('araceFirebaseAuth')->deleteUser((string) $authUser['uid']);
            }

            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel criar a conta agora.');
        }
    }

    public function producerOwner()
    {
        $payload = $this->request->getPost(['nome', 'cpf', 'email', 'telefone']);
        $brasilApi = new BrasilApiValidator();

        if (! $this->validateData($payload, [
            'nome'     => 'required|min_length[2]|max_length[120]',
            'cpf'      => 'required',
            'email'    => 'required|valid_email',
            'telefone' => 'required',
        ]) || ! $brasilApi->validCpf((string) $payload['cpf'])) {
            return redirect()->back()->withInput()->with('erro', 'Confira os dados do produtor.');
        }

        session()->set('arace_producer_owner', $payload);

        return redirect()->route('auth_cadastro_producer_loja');
    }

    public function producerStore()
    {
        $owner = session()->get('arace_producer_owner') ?? [];
        $store = $this->request->getPost(['nomeLoja', 'cnpj', 'email', 'telefone', 'categoria', 'distritoId']);
        $store['nome']          = $store['nomeLoja'] ?? '';
        $payload = array_filter([...$owner, ...$store], static fn ($value): bool => $value !== null && $value !== '');

        if (! $this->validateData($payload, [
            'nome'      => 'required|min_length[2]|max_length[120]',
            'nomeLoja'  => 'required|min_length[2]|max_length[120]',
            'email'     => 'required|valid_email',
            'telefone'  => 'required',
            'categoria' => 'required',
            'distritoId' => 'required',
        ])) {
            return redirect()->back()->withInput()->with('erro', 'Confira os dados da loja.');
        }
        try {
            (new AraceFirestore())->createProducer($payload);
            session()->remove('arace_producer_owner');

            return redirect()->route('produtor_perfil_loja')->with('sucesso', 'Loja criada com sucesso.');
        } catch (\Throwable) {
            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar a loja no Firestore.');
        }
    }

}
