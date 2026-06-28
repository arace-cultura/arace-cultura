<?php

namespace App\Entities;

use Tatter\Firebase\Firestore\Entity;

final class Product extends Entity
{
    protected $casts = [
        'preco'       => 'float',
        'precoAntigo' => '?float',
        'estrelas'   => 'float',
        'avaliacoes' => 'integer',
        'favorito'   => 'boolean',
        'disponivel' => 'boolean',
        'destaque'   => 'boolean',
        'desconto'   => 'integer',
    ];
}
