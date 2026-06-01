<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */
$routes->view("/", "main/index");

$routes->view('entrada', 'entrada');
$routes->view("login", "login");
$routes->view("cadastro", "authentication/cadastro-arace");


$routes->view("produtos", "produtos");

$routes->view("perfil_usuario", "perfil_usuario");

$routes->view("carrinho", "carrinho_de_compras");