<?php

namespace App\Entities;

use Tatter\Firebase\Firestore\Entity;

final class Customer extends Entity
{
    protected $casts = [
        'termosAceitos' => 'boolean',
    ];
}
