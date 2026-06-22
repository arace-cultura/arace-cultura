<?php

namespace App\Controllers;

use App\Libraries\AraceFirestore;

final class RegistrationController extends BaseController
{
    public function user()
    {
        $payload = $this->request->getPost(['nome', 'email', 'senha']);

        if (! $this->validateData($payload, [
            'nome'  => 'required|min_length[2]|max_length[120]',
            'email' => 'required|valid_email',
            'senha' => 'required|min_length[6]',
        ])) {
            return redirect()
                ->back()
                ->withInput()
                ->with('erro', 'Confira os dados do cadastro.')
                ->with('erros', $this->validator->getErrors());
        }

        try {
            (new AraceFirestore())->createUser($payload);

            return redirect()->to('/usuario/arace-perfil')->with('sucesso', 'Conta criada com sucesso.');
        } catch (\Throwable $e) {
            dd($e->getMessage(), $e->getTraceAsString());
            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar o cadastro no Firestore.');
        }
    }

    public function producerOwner()
    {
        $payload = $this->request->getPost(['nome', 'cpf', 'email', 'telefone']);
        $payload['termosAceitos'] = (bool) $this->request->getPost('termosAceitos');

        if (! $this->validateData($payload, [
            'nome'     => 'required|min_length[2]|max_length[120]',
            'cpf'      => 'required',
            'email'    => 'required|valid_email',
            'telefone' => 'required',
        ]) || ! $this->validCpf($payload['cpf'])) {
            return redirect()->back()->withInput()->with('erro', 'Confira os dados do produtor.');
        }

        session()->set('arace_producer_owner', $payload);

        return redirect()->to('/cadastro/produtor-loja');
    }

    public function producerStore()
    {
        $owner = session()->get('arace_producer_owner') ?? [];
        $store = $this->request->getPost(['nomeLoja', 'cnpj', 'email', 'telefone', 'categoria', 'distritoId']);
        $store['nome']          = $store['nomeLoja'] ?? '';
        $store['termosAceitos'] = (bool) $this->request->getPost('termosAceitos');
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

            return redirect()->to('/produtor/perfil-loja')->with('sucesso', 'Loja criada com sucesso.');
        } catch (\Throwable) {
            return redirect()->back()->withInput()->with('erro', 'Nao foi possivel salvar a loja no Firestore.');
        }
    }

    private function validCpf(string $cpf): bool
    {
        $numbers = preg_replace('/\D/', '', $cpf);

        if (strlen($numbers) !== 11 || preg_match('/^(\d)\1+$/', $numbers)) {
            return false;
        }

        for ($t = 9; $t < 11; $t++) {
            $sum = 0;
            for ($c = 0; $c < $t; $c++) {
                $sum += (int) $numbers[$c] * (($t + 1) - $c);
            }

            $digit = ((10 * $sum) % 11) % 10;
            if ((int) $numbers[$t] !== $digit) {
                return false;
            }
        }

        return true;
    }
}
