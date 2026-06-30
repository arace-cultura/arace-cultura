<?php

namespace App\Collections;

use App\Entities\Product;
use Tatter\Firebase\Firestore\Collection;

final class ProductCollection extends Collection
{
    public const NAME   = 'Produtos';
    public const ENTITY = Product::class;

    protected array $validationRules = [];
}
