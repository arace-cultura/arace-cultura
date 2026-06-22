<?php

namespace App\Collections;

use App\Entities\User; 
use Tatter\Firebase\Firestore\Collection;

final class UsuarioCollection extends Collection
{
    public const NAME   = 'Usuarios';
    public const ENTITY = User::class;

    // Mantendo a proteção dos campos necessária
    protected array $allowedFields = ['nome', 'email', 'senha'];

    protected array $validationRules = [
        'nome'  => 'required|min_length[2]|max_length[120]',
        'email' => 'required|valid_email',
        'senha' => 'required',
    ];
}