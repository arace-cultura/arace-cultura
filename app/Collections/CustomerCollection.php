<?php

namespace App\Collections;

use App\Entities\Customer;
use Tatter\Firebase\Firestore\Collection;

final class CustomerCollection extends Collection
{
    public const NAME   = 'clientes';
    public const ENTITY = Customer::class;

    protected array $validationRules = [
        'nome'  => 'required|min_length[2]|max_length[120]',
        'email' => 'required|valid_email',
        'senha' => 'required',
    ];
}
