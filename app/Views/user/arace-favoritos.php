<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Favoritos</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/favoritos.css') ?>" rel="stylesheet" />
</head>
<body>

<!-- HEADER -->
<header>
  <a href="<?= url_to('home') ?>" class="logo">aracê</a>

  <form class="search-wrap" action="<?= url_to('main_pesquisa') ?>" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('main_arace_carrinho') ?>'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_favoritos') ?>'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'" aria-label="Abrir perfil">
      <?php if ($avatar !== ''): ?>
        <img src="<?= esc($avatar, 'attr') ?>" alt="Avatar do usuario" />
      <?php else: ?>
        <i data-lucide="user"></i>
      <?php endif; ?>
    </button>
  </div>
</header>

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="<?= url_to('user_chat') ?>">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

<!-- SIDEBAR -->
<aside>
    <a class="nav-item" href="<?= url_to('home') ?>">
      <i data-lucide="house"></i> Home page
    </a>
    <a class="nav-item" href="<?= url_to('arace_produtos') ?>">
      <i data-lucide="shopping-bag"></i> Produtos
    </a>
    <a class="nav-item active" href="<?= url_to('main_arace_carrinho') ?>">
      <i data-lucide="shopping-cart"></i> Carrinho
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_notificacao') ?>">
      <i data-lucide="bell"></i> Notificações
    </a>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="<?= url_to('auth_cadastro_produtor') ?>">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>

<main>

  <div class="fav-page-header">
    <div>
      <h1>Meus Favoritos <span class="fav-count-badge" id="headerCount">5</span></h1>
      <p>Produtos que você salvou para comprar depois</p>
    </div>
  </div>

  <!-- COLEÇÕES -->
  <div class="colecoes-bar" id="colecoesBar">
    <button class="col-chip active" onclick="filtrarColecao(this, 'todos')">
      <i data-lucide="layers"></i> Todos
    </button>
    <button class="col-chip" onclick="filtrarColecao(this, 'ceramica')">
      <i data-lucide="amphora"></i> Cerâmica
    </button>
    <button class="col-chip" onclick="filtrarColecao(this, 'madeira')">
      <i data-lucide="tree-pine"></i> Madeira
    </button>
    <button class="col-chip" onclick="filtrarColecao(this, 'joias')">
      <i data-lucide="gem"></i> Joias
    </button>
    <button class="col-chip-add" onclick="novaColecao()">
      <i data-lucide="plus"></i> Nova coleção
    </button>
  </div>

  <!-- TOOLBAR -->
  <div class="fav-toolbar">
    <div class="search-mini">
      <i data-lucide="search"></i>
      <input type="text" placeholder="Buscar nos favoritos…" oninput="filtrarBusca(this.value)" />
    </div>
    <button class="filter-btn active" onclick="toggleFiltro(this,'todos')">
      Todos
    </button>
    <button class="filter-btn" onclick="toggleFiltro(this,'disponivel')">
      <i data-lucide="check-circle"></i> Disponíveis
    </button>
    <button class="filter-btn" onclick="toggleFiltro(this,'promocao')">
      <i data-lucide="tag"></i> Em promoção
    </button>
    <div class="view-toggle">
      <button class="view-btn active" id="btnGrade" onclick="trocarView('grade')" title="Grade">
        <i data-lucide="layout-grid"></i>
      </button>
      <button class="view-btn" id="btnLista" onclick="trocarView('lista')" title="Lista">
        <i data-lucide="layout-list"></i>
      </button>
    </div>
  </div>

  <!-- GRADE DE FAVORITOS -->
  <div class="fav-grid" id="favGrid"></div>

</main>

<script src="/js/arace-state.js"></script>
<script src="/js/favoritos.js"></script>
</body>
</html>
