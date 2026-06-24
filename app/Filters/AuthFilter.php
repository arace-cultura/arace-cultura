<?php

namespace App\Filters;

use CodeIgniter\Filters\FilterInterface;
use CodeIgniter\HTTP\RequestInterface;
use CodeIgniter\HTTP\ResponseInterface;

final class AuthFilter implements FilterInterface
{
    public function before(RequestInterface $request, $arguments = null)
    {
        if (session()->get('arace_authenticated') === true && is_array(session()->get('arace_user'))) {
            return null;
        }

        session()->set('arace_intended_url', current_url());

        return redirect()->to('/login')->with('erro', 'Entre na sua conta para acessar esta pagina.');
    }

    public function after(RequestInterface $request, ResponseInterface $response, $arguments = null)
    {
        return null;
    }
}
