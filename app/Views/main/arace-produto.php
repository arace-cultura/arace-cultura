<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Produto</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:ital,wght@0,700;1,400&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/produto.css" rel="stylesheet" />
</head>
<body>


<!-- HEADER -->
<header>
    <a href="/" class="logo">aracê</a>
    <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='/arace-carrinho'">
        <i data-lucide="shopping-cart"></i>
        <span class="cart-count">2 itens</span>
      </button>
    <button class="cart-btn" type="button" onclick="window.location.href='/usuario/arace-favoritos'">
        <i data-lucide="heart"></i>
        <span class="cart-count">5 itens</span>
      </button>
      <button class="avatar-btn" type="button" onclick="window.location.href='/usuario/arace-perfil'" aria-label="Abrir perfil">
        <i data-lucide="user"></i>
      </button>
    </div>
  </header>

<!-- SIDEBAR -->
<aside>
    <a class="nav-item" href="/">
      <i data-lucide="house"></i> Home page
    </a>
    <a class="nav-item" href="/arace-produtos">
      <i data-lucide="shopping-bag"></i> Produtos
    </a>
    <a class="nav-item active" href="/arace-carrinho">
      <i data-lucide="shopping-cart"></i> Carrinho
    </a>
    <a class="nav-item" href="/usuario/arace-notificacao">
      <i data-lucide="bell"></i> Notificações
    </a>
    <a class="nav-item" href="/arace-config">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="/usuario/arace-perfil">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="/auth/cadastro/produtor">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="/arace-config">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>



<main id="main-content">
  <!-- preenchido via JS -->
  <div style="display:flex;align-items:center;justify-content:center;height:60vh;color:var(--faint);font-size:14px;">
    <span>Carregando produto…</span>
  </div>
</main>

<script src="/js/produto.js"></script>
</body>
</html>
