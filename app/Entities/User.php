<?php

namespace App\Entities;

use Tatter\Firebase\Firestore\Entity;

final class User extends Entity
{
    protected $casts = [
        'isProdutor' => 'boolean',
    ];
}
