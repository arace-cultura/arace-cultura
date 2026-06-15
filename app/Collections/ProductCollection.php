<?php

namespace App\Collections;

use App\Entities\Product;
use Tatter\Firebase\Firestore\Collection;

final class ProductCollection extends Collection
{
    public const NAME   = 'produtos';
    public const ENTITY = Product::class;

    protected $validationRules = [
        'nome'      => 'required|min_length[2]|max_length[120]',
        'artesao'   => 'permit_empty|max_length[120]',
        'categoria' => 'required|max_length[60]',
        'preco'     => 'required|numeric',
    ];
}
