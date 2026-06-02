<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */

// Rota Raiz (Ex: aponta para a landing page dentro da pasta main)
$routes->view('/', 'main/arace-landing', ['as' => 'landing-page']);

// Demais rotas do seu projeto com seus respectivos apelidos
$routes->view('entrada', 'entrada', ['as' => 'rota_entrada']);
$routes->view("login", "authentication/login-arace", ['as' => 'rota_login']);
$routes->view("cadastro", "authentication/cadastro-arace", ['as' => 'rota_cadastro']);
$routes->view("produtos", "main/produtos", ['as' => 'rota_produtos']);
$routes->view("carrinho", "main/carrinho", ['as' => 'rota_carrinho']);