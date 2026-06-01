﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Minha Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/loja.css" rel="stylesheet" />
</head>
<body>

  <header>
    <span class="logo">aracê</span>
    <div class="header-right">
      <button class="cart-btn"><i data-lucide="shopping-cart"></i><span class="cart-count">2 itens</span></button>
      <button class="cart-btn"><i data-lucide="heart"></i><span class="cart-count">5 itens</span></button>
      <div class="avatar-btn"><i data-lucide="user"></i></div>
    </div>
  </header>

  <aside>
    <a class="nav-item" href="../main/index.html"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="./arace-producer-painel.html"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item" href="./arace-producer-painel-produtos.html"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item" href="./arace-producer-pedidos.html"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item active" href="./arace-producer-profile-loja.html"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="./arace-producer-profile.html"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="../main/configuracoes.html"><i data-lucide="settings"></i> Configurações</a>
    <div class="nav-section">Suporte</div>
    <a class="nav-item" href="../main/configuracoes.html#pagamento"><i data-lucide="hand-coins"></i> Pagamentos</a>
  </aside>

  <main>
  <section class="store-banner-wrapper item-animado atraso-1">
    <div class="store-banner">
      <img src="../assets/imgs/background-paneleiras.jpg" alt="Fundo Paneleiras" class="banner-img" />
    </div>
    
    <div class="store-profile-header">
      <div class="store-avatar">
        <img src="../assets/imgs/avatar-produtor.jpg" alt="Foto de perfil do produtor" />
      </div>
      <h1 class="store-title">Paneleiras Capixabas</h1>
    </div>
  </section>

  <section class="store-section item-animado atraso-2">
    <div class="section-header">
      <h2>Nossos produtos</h2>
      <a href="#" class="view-all-link">Todos os produtos</a>
    </div>
    
    <!--Produtos-->
    <div class="products-grid">
      <div class="product-card">
        <div class="product-image">
          <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" />
        </div>
        <div class="product-info">
          <h3 class="product-name">Kit Panela de barro</h3>
          <span class="product-price">R$ 200</span>
        </div>
      </div>

      <div class="product-card">
        <div class="product-image">
          <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" />
        </div>
        <div class="product-info">
          <h3 class="product-name">Kit Panela de barro</h3>
          <span class="product-price">R$ 200</span>
        </div>
      </div>

      <div class="product-card">
        <div class="product-image">
          <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" />
        </div>
        <div class="product-info">
          <h3 class="product-name">Kit Panela de barro</h3>
          <span class="product-price">R$ 200</span>
        </div>
      </div>

      <div class="product-card">
        <div class="product-image">
          <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" />
        </div>
        <div class="product-info">
          <h3 class="product-name">Kit Panela de barro</h3>
          <span class="product-price">R$ 200</span>
        </div>
      </div>
    </div>
  </section>

  <section class="store-section history-section item-animado atraso-3">
    <h2>Nossa história</h2>
    <div class="history-container">
      <div class="history-image">
        <img src="../assets/imgs/historia-paneleira.jpg" alt="Produção de panela de barro" />
      </div>
      <div class="history-text">
        <p>
          Preservamos uma tradição centenária de produção artesanal de panelas de barro, 
          símbolo da cultura capixaba. Cada peça carrega a identidade, o suor e o amor 
          passado de geração em geração.
        </p>
      </div>
    </div>
  </section>
</main>

  <script>lucide.createIcons();</script>
</body>
</html>