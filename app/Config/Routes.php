<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */
$routes->view("/", "main/index");
$routes->view("arace-landing.php", "main/arace-landing");

$routes->view('entrada', 'entrada');
$routes->view("login", "authentication/login-arace");
$routes->view("cadastro", "authentication/cadastro-arace");


$routes->view("produtos", "main/produtos");

$routes->view("perfil_usuario", "user/perfil");

$routes->view("carrinho", "main/carrinho");