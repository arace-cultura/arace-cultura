<?php

namespace App\Entities;

use Tatter\Firebase\Firestore\Entity;

final class Producer extends Entity
{
    protected $casts = [
        'produtos'      => 'integer',
        'cadastrado'    => 'boolean',
    ];
}
