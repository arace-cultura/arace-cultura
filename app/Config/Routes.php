<?php

use CodeIgniter\Router\RouteCollection;

/** @var RouteCollection $routes */

// ============================================================================
// 1. HOME / ENTRADA PRINCIPAL
// ============================================================================
// Define que a raiz do site abre o index que redireciona para a landing page
$routes->view('/', 'main/index', ['as' => 'home']);


// ============================================================================
// 2. AUTENTICAÇÃO (authentication/)
// ============================================================================
$routes->view('login', 'authentication/login-arace', ['as' => 'auth_login']);
$routes->view('cadastro', 'authentication/cadastro-arace', ['as' => 'auth_cadastro']);

// Novas rotas de cadastro encontradas na pasta authentication
$routes->view('cadastro/produtor', 'authentication/cadastro-produtor', ['as' => 'auth_cadastro_produtor']);
$routes->view('cadastro/produtor-arace', 'authentication/cadastro-producer-arace', ['as' => 'auth_cadastro_producer_arace']);
$routes->view('cadastro/produtor-loja', 'authentication/cadastro-producer-loja', ['as' => 'auth_cadastro_producer_loja']);
$routes->view('cadastro/produtora-arace', 'authentication/cadastro-producter-arace', ['as' => 'auth_cadastro_producter_arace']);


// ============================================================================
// 3. PÁGINAS PRINCIPAIS DA LOJA (main/)
// ============================================================================
$routes->view('landing-page', 'main/arace-landing', ['as' => 'landing-page']);
$routes->view('produtos', 'main/produtos', ['as' => 'main_produtos']);
$routes->view('produto/detalhes', 'main/arace-produto', ['as' => 'main_produto_detalhes']);
$routes->view('pesquisa', 'main/arace-search', ['as' => 'main_pesquisa']);

// Carrinhos

$routes->view('arace-carrinho', 'main/arace-carrinho', ['as' => 'main_arace_carrinho']);

// Configurações Gerais da Main
$routes->view('arace-config', 'main/arace-config', ['as' => 'main_arace_config']);


// ============================================================================
// 4. ÁREA DO CLIENTE / USUÁRIO (user/)
// ============================================================================
$routes->view('usuario/arace-perfil', 'user/arace-perfil', ['as' => 'user_arace_perfil']);
$routes->view('usuario/chat', 'user/arace-chat', ['as' => 'user_chat']);

// Favoritos
$routes->view('usuario/arace-favoritos', 'user/arace-favoritos', ['as' => 'user_arace_favoritos']);

// Notificações
$routes->view('usuario/arace-notificacao', 'user/arace-notificacao', ['as' => 'user_arace_notificacao']);


// ============================================================================
// 5. ÁREA DO PRODUTOR / LOJISTA (user-producter/)
// ============================================================================
$routes->view('produtor/painel', 'user-producter/arace-producer-painel-produtos', ['as' => 'produtor_painel']);
$routes->view('produtor/pedidos', 'user-producter/arace-producer-pedidos', ['as' => 'produtor_pedidos']);
$routes->view('produtor/perfil', 'user-producter/arace-producer-profile', ['as' => 'produtor_perfil']);
$routes->view('produtor/perfil-loja', 'user-producter/arace-producer-profile-loja', ['as' => 'produtor_perfil_loja']);
$routes->view('produtor/configuracao', 'user-producter/arace-producer-config', ['as' => 'produtor_config']);
$routes->view('produtor/configuracao-loja', 'user-producter/arace-producer-config-loja', ['as' => 'produtor_config_loja']);