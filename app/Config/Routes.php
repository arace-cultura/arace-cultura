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

$routes->get('arace-carrinho', 'AccountController::cart', ['as' => 'main_arace_carrinho', 'filter' => 'auth']);
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
    $routes->get('arace-favoritos', 'AccountController::favorites', ['as' => 'user_arace_favoritos']);
});

$routes->get('perfil', $routeRedirect('user_arace_perfil'));
$routes->get('favoritos', $routeRedirect('user_arace_favoritos'));

/*
|--------------------------------------------------------------------------
| Area do produtor
|--------------------------------------------------------------------------
*/
$routes->group('produtor', ['filter' => 'auth'], static function (RouteCollection $routes): void {
    $routes->get('painel', 'AccountController::producerDashboard', ['as' => 'produtor_painel']);
    $routes->get('pedidos', 'AccountController::producerOrders', ['as' => 'produtor_pedidos']);
    $routes->get('perfil', 'AccountController::producerProfile', ['as' => 'produtor_perfil']);
    $routes->get('perfil-loja', 'AccountController::producerStoreProfile', ['as' => 'produtor_perfil_loja']);
    $routes->get('configuracao', 'AccountController::producerConfig', ['as' => 'produtor_config']);
    $routes->get('configuracao-loja', 'AccountController::producerStoreConfig', ['as' => 'produtor_config_loja']);
    $routes->post('configuracao-loja', 'AccountController::updateProducerStore', ['as' => 'produtor_config_loja_update']);
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
    $routes->get('favorites', 'FirestoreController::favorites', ['as' => 'api_favorites', 'filter' => 'auth']);
    $routes->post('favorites', 'FirestoreController::saveFavorite', ['as' => 'api_favorites_store', 'filter' => 'auth']);
    $routes->delete('favorites/(:segment)', 'FirestoreController::removeFavorite/$1', ['as' => 'api_favorites_delete', 'filter' => 'auth']);
    $routes->get('cart', 'FirestoreController::cart', ['as' => 'api_cart', 'filter' => 'auth']);
    $routes->post('cart', 'FirestoreController::addCartItem', ['as' => 'api_cart_store', 'filter' => 'auth']);
    $routes->patch('cart/(:segment)', 'FirestoreController::updateCartItem/$1', ['as' => 'api_cart_update', 'filter' => 'auth']);
    $routes->delete('cart/(:segment)', 'FirestoreController::removeCartItem/$1', ['as' => 'api_cart_delete', 'filter' => 'auth']);
    $routes->get('producer/orders', 'FirestoreController::producerOrders', ['as' => 'api_producer_orders', 'filter' => 'auth']);
    $routes->patch('producer/orders/(:segment)', 'FirestoreController::updateProducerOrder/$1', ['as' => 'api_producer_order_update', 'filter' => 'auth']);
    $routes->post('producer/products', 'FirestoreController::createProducerProduct', ['as' => 'api_producer_products_store', 'filter' => 'auth']);
    $routes->post('user', 'FirestoreController::createUser', ['as' => 'api_user_store']);
    $routes->post('producers', 'FirestoreController::createProducer', ['as' => 'api_producer_store']);
});

$routes->post('api/auth/login', 'AuthController::login', ['as' => 'api_auth_login']);
