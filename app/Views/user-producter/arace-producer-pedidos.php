<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Pedidos</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/style-base.css') ?>" rel="stylesheet" />
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

  <!-- HEADER -->
  <div class="page-header">
    <div>
      <h1>Pedidos</h1>
      <p>Acompanhe e gerencie os pedidos recebidos na sua loja</p>
    </div>
    <div class="page-header-actions">
      <div class="search-inline">
        <i data-lucide="search"></i>
        <input type="text" id="searchPedidos" placeholder="Buscar pedido ou cliente…" />
      </div>
      <button class="btn-export"><i data-lucide="download"></i> Exportar</button>
    </div>
  </div>

  <!-- STATS -->
  <div class="pedidos-stats">
    <div class="stat-card">
      <div class="stat-icon pendente"><i data-lucide="clock"></i></div>
      <div>
        <div class="stat-value" id="cnt-pendente">3</div>
        <div class="stat-label">Pendentes</div>
      </div>
    </div>
    <div class="stat-card">
      <div class="stat-icon producao"><i data-lucide="package"></i></div>
      <div>
        <div class="stat-value" id="cnt-producao">2</div>
        <div class="stat-label">Em produção</div>
      </div>
    </div>
    <div class="stat-card">
      <div class="stat-icon enviado"><i data-lucide="truck"></i></div>
      <div>
        <div class="stat-value" id="cnt-enviado">4</div>
        <div class="stat-label">Enviados</div>
      </div>
    </div>
    <div class="stat-card">
      <div class="stat-icon entregue"><i data-lucide="circle-check"></i></div>
      <div>
        <div class="stat-value" id="cnt-entregue">18</div>
        <div class="stat-label">Entregues</div>
      </div>
    </div>
  </div>

  <!-- FILTROS -->
  <div class="pedidos-filters">
    <button class="filter-chip active" data-status="todos">Todos</button>
    <button class="filter-chip" data-status="pendente">
      <span class="dot pendente"></span> Pendente
    </button>
    <button class="filter-chip" data-status="producao">
      <span class="dot producao"></span> Em produção
    </button>
    <button class="filter-chip" data-status="enviado">
      <span class="dot enviado"></span> Enviado
    </button>
    <button class="filter-chip" data-status="entregue">
      <span class="dot entregue"></span> Entregue
    </button>
    <button class="filter-chip" data-status="cancelado">
      <span class="dot cancelado"></span> Cancelado
    </button>
  </div>

  <!-- TABELA -->
  <div class="pedidos-table-wrap">
    <table class="pedidos-table">
      <thead>
        <tr>
          <th>Pedido</th>
          <th>Cliente</th>
          <th>Produto</th>
          <th>Valor</th>
          <th>Data</th>
          <th>Status</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody id="pedidosBody"></tbody>
    </table>
  </div>

  <!-- PAGINAÇÃO -->
  <div class="pedidos-pagination">
    <span id="paginacaoInfo">Mostrando 1–7 de 27 pedidos</span>
    <div class="pagination-btns">
      <button class="pg-btn"><i data-lucide="chevron-left"></i></button>
      <button class="pg-num active">1</button>
      <button class="pg-num">2</button>
      <button class="pg-num">3</button>
      <button class="pg-btn"><i data-lucide="chevron-right"></i></button>
    </div>
  </div>

</main>

<!-- MODAL DETALHE -->
<div class="modal-overlay" id="modalOverlay">
  <div class="modal-box">
    <div class="modal-header">
      <div>
        <h2 id="modalTitulo">Pedido #4821</h2>
        <p id="modalData" class="modal-sub"></p>
      </div>
      <button class="modal-close" type="button"><i data-lucide="x"></i></button>
    </div>

    <div class="modal-body">
      <div class="modal-section">
        <p class="modal-label">Cliente</p>
        <p id="modalCliente" class="modal-value"></p>
        <p id="modalEndereco" class="modal-sub"></p>
      </div>
      <div class="modal-section">
        <p class="modal-label">Produto</p>
        <p id="modalProduto" class="modal-value"></p>
        <p id="modalQtd" class="modal-sub"></p>
      </div>
      <div class="modal-section">
        <p class="modal-label">Valor total</p>
        <p id="modalValor" class="modal-value modal-valor-destaque"></p>
      </div>
      <div class="modal-section">
        <p class="modal-label">Status atual</p>
        <div id="modalStatus"></div>
      </div>
    </div>

    <div class="modal-footer">
      <p class="modal-label">Atualizar status</p>
      <div class="status-actions" id="statusActions"></div>
    </div>
  </div>
</div>

<script src="/js/arace-state.js"></script>
<script src="/js/producer-pedidos.js"></script>
</body>
</html>
