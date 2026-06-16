<?php

namespace App\Collections;

use App\Entities\Producer;
use Tatter\Firebase\Firestore\Collection;

final class ProducerCollection extends Collection
{
    public const NAME   = 'produtores';
    public const ENTITY = Producer::class;

    protected $validationRules = [
        'nome'     => 'required|min_length[2]|max_length[120]',
        'email'    => 'required|valid_email',
        'telefone' => 'permit_empty|max_length[30]',
    ];
}
