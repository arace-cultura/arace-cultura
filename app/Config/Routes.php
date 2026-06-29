<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */

$routes->setDefaultNamespace('App\Controllers');
$routes->setDefaultController('LandingController');
$routes->setDefaultMethod('index');
$routes->setTranslateURIDashes(false);
$routes->set404Override();
$routes->setAutoRoute(false);

$routeRedirect = static fn (string $routeName): Closure => static fn () => redirect()->route($routeName);

/*
|--------------------------------------------------------------------------
| Entrada publica
|--------------------------------------------------------------------------
*/
$routes->get('/', 'LandingController::index', ['as' => 'home']);
$routes->get('landing-page', 'LandingController::index', ['as' => 'landing_page']);
$routes->get('inicio', $routeRedirect('home'));
$routes->get('home', $routeRedirect('home'));

/*
|--------------------------------------------------------------------------
| Autenticacao e cadastro
|--------------------------------------------------------------------------
*/
$routes->get('login', 'AuthController::index', ['as' => 'auth_login']);
$routes->post('login', 'AuthController::login', ['as' => 'auth_login_post']);
$routes->post('sair', 'AuthController::logout', ['as' => 'auth_logout']);

$routes->view('cadastro', 'authentication/cadastro-arace', ['as' => 'auth_cadastro']);
$routes->view('cadastro/produtor', 'authentication/cadastro-produtor', ['as' => 'auth_cadastro_produtor']);
$routes->view('cadastro/produtor-arace', 'authentication/cadastro-producer-arace', ['as' => 'auth_cadastro_producer_arace']);
$routes->view('cadastro/produtora-arace', 'authentication/cadastro-producter-arace', ['as' => 'auth_cadastro_producter_arace']);
$routes->view('cadastro/produtor-loja', 'authentication/cadastro-producer-loja', ['as' => 'auth_cadastro_producer_loja']);

$routes->post('cadastro/clientes', 'RegistrationController::user', ['as' => 'auth_cadastro_cliente_store']);
$routes->post('cadastro/produtor/dono', 'RegistrationController::producerOwner', ['as' => 'auth_cadastro_produtor_owner_store']);
$routes->post('cadastro/produtores', 'RegistrationController::producerStore', ['as' => 'auth_cadastro_produtor_store']);

// Compatibilidade com nomes antigos usados em telas/scripts.
$routes->get('cadastro-produtor', $routeRedirect('auth_cadastro_produtor'));
$routes->get('cadastro-producer', $routeRedirect('auth_cadastro_produtor'));
$routes->get('authentication/cadastro-produtor', $routeRedirect('auth_cadastro_produtor'));
$routes->get('authentication/cadastro-producer-arace', $routeRedirect('auth_cadastro_producter_arace'));
$routes->get('authentication/cadastro-producter-arace', $routeRedirect('auth_cadastro_producter_arace'));
$routes->get('authentication/cadastro-producer-loja', $routeRedirect('auth_cadastro_producer_loja'));

/*
|--------------------------------------------------------------------------
| Loja e produtos
|--------------------------------------------------------------------------
*/
$routes->get('arace-produtos', 'ProductController::index', ['as' => 'arace_produtos']);
$routes->get('pesquisa', 'ProductController::index', ['as' => 'main_pesquisa']);
$routes->get('produto/detalhes', 'ProductController::show', ['as' => 'main_produto_detalhes']);
$routes->get('produto/(:segment)', 'ProductController::show/$1', ['as' => 'main_produto']);

$routes->view('arace-carrinho', 'main/arace-carrinho', ['as' => 'main_arace_carrinho']);
$routes->get('arace-config', 'AccountController::config', ['as' => 'main_arace_config', 'filter' => 'auth']);

// Aliases curtos para URLs antigas ou manuais.
$routes->get('produtos', $routeRedirect('arace_produtos'));
$routes->get('busca', $routeRedirect('main_pesquisa'));
$routes->get('carrinho', $routeRedirect('main_arace_carrinho'));
$routes->get('configuracoes', $routeRedirect('main_arace_config'));

/*
|--------------------------------------------------------------------------
| Area do usuario
|--------------------------------------------------------------------------
*/
$routes->group('usuario', ['filter' => 'auth'], static function (RouteCollection $routes): void {
    $routes->get('arace-perfil', 'AccountController::profile', ['as' => 'user_arace_perfil']);
    $routes->post('perfil', 'AccountController::updateProfile', ['as' => 'user_profile_update']);
    $routes->view('chat', 'user/arace-chat', ['as' => 'user_chat']);
    $routes->view('arace-favoritos', 'user/arace-favoritos', ['as' => 'user_arace_favoritos']);
    $routes->view('arace-notificacao', 'user/arace-notificacao', ['as' => 'user_arace_notificacao']);
});

$routes->get('perfil', $routeRedirect('user_arace_perfil'));
$routes->get('favoritos', $routeRedirect('user_arace_favoritos'));
$routes->get('notificacoes', $routeRedirect('user_arace_notificacao'));
$routes->get('chat', $routeRedirect('user_chat'));

/*
|--------------------------------------------------------------------------
| Area do produtor
|--------------------------------------------------------------------------
*/
$routes->group('produtor', ['filter' => 'auth'], static function (RouteCollection $routes): void {
    $routes->view('painel', 'user-producter/arace-producer-painel-produtos', ['as' => 'produtor_painel']);
    $routes->view('pedidos', 'user-producter/arace-producer-pedidos', ['as' => 'produtor_pedidos']);
    $routes->get('perfil', 'AccountController::producerProfile', ['as' => 'produtor_perfil']);
    $routes->view('perfil-loja', 'user-producter/arace-producer-profile-loja', ['as' => 'produtor_perfil_loja']);
    $routes->get('configuracao', 'AccountController::producerConfig', ['as' => 'produtor_config']);
    $routes->get('configuracao-loja', 'AccountController::producerStoreConfig', ['as' => 'produtor_config_loja']);
});

$routes->get('produtor/produtos', $routeRedirect('produtor_painel'));
$routes->get('produtor/config', $routeRedirect('produtor_config'));
$routes->get('produtor/config-loja', $routeRedirect('produtor_config_loja'));

/*
|--------------------------------------------------------------------------
| API Firestore
|--------------------------------------------------------------------------
*/
$routes->group('api', ['namespace' => 'App\Controllers\Api'], static function (RouteCollection $routes): void {
    $routes->get('products', 'FirestoreController::products', ['as' => 'api_products']);
    $routes->get('products/(:segment)', 'FirestoreController::product/$1', ['as' => 'api_product']);
    $routes->get('producers', 'FirestoreController::producers', ['as' => 'api_producers']);
    $routes->post('user', 'FirestoreController::createUser', ['as' => 'api_user_store']);
    $routes->post('producers', 'FirestoreController::createProducer', ['as' => 'api_producer_store']);
});

$routes->post('api/auth/login', 'AuthController::login', ['as' => 'api_auth_login']);
