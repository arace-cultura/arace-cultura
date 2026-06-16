<?php

namespace App\Controllers\Api;

use App\Libraries\AraceFirestore;
use CodeIgniter\HTTP\ResponseInterface;
use CodeIgniter\RESTful\ResourceController;

final class FirestoreController extends ResourceController
{
    public function products(): ResponseInterface
    {
        return $this->respond([
            'source' => 'firestore',
            'data'   => (new AraceFirestore())->products((bool) $this->request->getGet('featured')),
        ]);
    }

    public function product(string $id): ResponseInterface
    {
        $product = (new AraceFirestore())->product($id);

        return $product === null
            ? $this->failNotFound('Produto nao encontrado.')
            : $this->respond(['source' => 'firestore', 'data' => $product]);
    }

    public function producers(): ResponseInterface
    {
        return $this->respond([
            'source' => 'firestore',
            'data'   => (new AraceFirestore())->producers(),
        ]);
    }

    public function createCustomer(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        $payload = $this->cleanPayload($payload, ['nome', 'email', 'telefone', 'termosAceitos']);

        if (! $this->validateData($payload, [
            'nome'  => 'required|min_length[2]|max_length[120]',
            'email' => 'required|valid_email',
        ])) {
            return $this->failValidationErrors($this->validator->getErrors());
        }

        try {
            return $this->respondCreated([
                'source' => 'firestore',
                'data'   => (new AraceFirestore())->createCustomer($payload),
            ]);
        } catch (\Throwable) {
            return $this->failServerError('Nao foi possivel salvar o cliente no Firestore.');
        }
    }

    public function createProducer(): ResponseInterface
    {
        $payload = $this->request->getJSON(true) ?? $this->request->getPost();
        $payload = $this->cleanPayload($payload, [
            'nome',
            'nomeLoja',
            'email',
            'telefone',
            'cpf',
            'cnpj',
            'categoria',
            'distritoId',
            'termosAceitos',
        ]);

        if (! $this->validateData($payload, [
            'nome'  => 'required|min_length[2]|max_length[120]',
            'email' => 'required|valid_email',
        ])) {
            return $this->failValidationErrors($this->validator->getErrors());
        }

        try {
            return $this->respondCreated([
                'source' => 'firestore',
                'data'   => (new AraceFirestore())->createProducer($payload),
            ]);
        } catch (\Throwable) {
            return $this->failServerError('Nao foi possivel salvar o produtor no Firestore.');
        }
    }

    private function cleanPayload(array $payload, array $allowed): array
    {
        return array_filter(
            array_intersect_key($payload, array_flip($allowed)),
            static fn ($value): bool => $value !== null && $value !== ''
        );
    }
}
