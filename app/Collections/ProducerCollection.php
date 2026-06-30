<?php

namespace App\Collections;

use App\Entities\Producer;
use Tatter\Firebase\Firestore\Collection;

final class ProducerCollection extends Collection
{
    public const NAME   = 'produtores';
    public const ENTITY = Producer::class;

    protected bool $skipValidation = true;

    protected array $allowedFields = [
        'nome',
        'nomeLoja',
        'nome_loja',
        'lojaBio',
        'bio',
        'email',
        'email_comercial',
        'telefone',
        'telefone_comercial',
        'cpf',
        'cnpj',
        'categoria',
        'categoria_principal',
        'distritoId',
        'distrito_id',
        'fotoUrl',
        'bannerUrl',
        'cepOrigem',
        'cidade',
        'estado',
        'endereco',
        'retiradaLocal',
        'envioCorreios',
        'entregaLocal',
        'banco',
        'tipoConta',
        'agencia',
        'conta',
        'pix',
        'horarioSemanaInicio',
        'horarioSemanaFim',
        'horarioSabadoInicio',
        'horarioSabadoFim',
        'produtos',
        'iniciais',
        'tipo',
        'cadastrado',
        'createdAt',
    ];

    protected array $validationRules = [
        'nome'     => 'required|min_length[2]|max_length[120]',
        'email'    => 'required|valid_email',
        'telefone' => 'permit_empty|max_length[30]',
    ];
}
