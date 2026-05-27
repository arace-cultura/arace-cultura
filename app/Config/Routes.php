<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */
$routes->view("/", "index");

$routes->view('entrada', 'entrada');
$routes->view("login", "login");
$routes->view("cadastro", "cadastro");
$routes->view("recuperar_senha", "recuperar_senha");

$routes->view("produtos", "produtos");

$routes->view("perfil_usuario", "perfil_usuario");

$routes->view("carrinho", "carrinho_de_compras");
