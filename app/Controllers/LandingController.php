<?php

namespace App\Controllers;

use App\Libraries\AraceFirestore;

final class LandingController extends BaseController
{
    public function index(): string
    {
        $firestore = new AraceFirestore();

        return view('main/arace-landing', [
            'produtos'   => $firestore->products(),
            'produtores' => $firestore->producers(),
        ]);
    }
}
