<?php

namespace App\Controllers;

use App\Libraries\AraceFirestore;
use CodeIgniter\Exceptions\PageNotFoundException;

final class ProductController extends BaseController
{
    public function index(): string
    {
        $firestore = new AraceFirestore();
        $produtos  = $firestore->products();
        $q         = trim((string) $this->request->getGet('q'));
        $categoria = trim((string) $this->request->getGet('categoria'));

        if ($q !== '') {
            $produtos = array_values(array_filter($produtos, static function (array $produto) use ($q): bool {
                return stripos((string) ($produto['nome'] ?? ''), $q) !== false
                    || stripos((string) ($produto['descricao'] ?? ''), $q) !== false
                    || stripos((string) ($produto['artesao'] ?? ''), $q) !== false;
            }));
        }

        if ($categoria !== '') {
            $produtos = array_values(array_filter($produtos, static function (array $produto) use ($categoria): bool {
                $categorias = $produto['categorias'] ?? [$produto['categoria'] ?? ''];
                $categorias = array_map(static fn ($item): string => mb_strtolower((string) $item), $categorias);

                return in_array(mb_strtolower($categoria), $categorias, true);
            }));
        }

        return view('main/arace-search', [
            'produtos'  => $produtos,
            'q'         => $q,
            'categoria' => $categoria,
        ]);
    }

    public function show(?string $id = null): string
    {
        $id = trim((string) ($id ?? $this->request->getGet('id')));

        if ($id === '') {
            throw PageNotFoundException::forPageNotFound('Produto nao informado.');
        }

        $firestore = new AraceFirestore();
        $produto   = $firestore->product($id);

        if ($produto === null) {
            throw PageNotFoundException::forPageNotFound('Produto nao encontrado.');
        }

        return view('main/arace-produto', [
            'produto'      => $produto,
            'recomendados' => array_values(array_filter(
                $firestore->products(),
                static fn (array $item): bool => (string) ($item['id'] ?? '') !== $id
            )),
        ]);
    }
}
