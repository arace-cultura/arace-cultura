﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Minha Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="../assets/style/loja.css" rel="stylesheet" />
</head>
<body>

  
  <header>
    <span class="logo">aracê</span>
    <div class="header-right">
      <button class="icon-btn" id="btn-cart" aria-label="Carrinho">
        <i data-lucide="shopping-cart"></i>
        <span id="cart-label">2 itens</span>
      </button>
      <button class="icon-btn" id="btn-fav" aria-label="Favoritos">
        <i data-lucide="heart"></i>
        <span id="fav-label">5 itens</span>
      </button>
      <div class="avatar-btn" role="button" aria-label="Perfil">
        <i data-lucide="user"></i>
      </div>
    </div>
  </header>

  
  <aside aria-label="Navegação principal">
    <a class="nav-item" href="../main/index.html"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="./arace-producer-painel.html"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item" href="./arace-producer-painel-produtos.html"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item" href="./arace-producer-pedidos.html"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item active" href="./arace-producer-profile-loja.html" aria-current="page"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="./arace-producer-profile.html"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="../main/configuracoes.html"><i data-lucide="settings"></i> Configurações</a>
    <div class="nav-section">Suporte</div>
    <a class="nav-item" href="../main/configuracoes.html#pagamento"><i data-lucide="hand-coins"></i> Pagamentos</a>
  </aside>

  
  <main>

    <!-- Breadcrumb -->
    <nav class="breadcrumb" aria-label="Caminho de navegação">
      <a href="../main/index.html">Home</a>
      <i data-lucide="chevron-right"></i>
      <span>Minha loja</span>
    </nav>

   
    <section class="store-banner-wrapper item-animado atraso-1" aria-label="Banner da loja">
      <div class="store-banner">
        <img src="../assets/imgs/background-paneleiras.jpg" alt="Paneleiras capixabas trabalhando" class="banner-img" />
        <div class="banner-overlay" aria-hidden="true"></div>
        <div class="banner-actions">
          <button class="banner-action-btn" id="btn-share">
            <i data-lucide="share-2"></i> Compartilhar
          </button>
          <button class="banner-action-btn" id="btn-edit-banner">
            <i data-lucide="image"></i> Editar capa
          </button>
        </div>
      </div>

      <div class="store-profile-header">
        <div class="store-avatar">
          <img src="../assets/imgs/avatar-produtor.jpg" alt="Foto de perfil da loja Paneleiras Capixabas" />
        </div>
        <div class="store-meta">
          <h1 class="store-title">Paneleiras Capixabas</h1>
          <p class="store-subtitle">
            <i data-lucide="map-pin"></i>
            Vitória, ES · Artesanato tradicional
          </p>
        </div>
        <div class="store-stats" aria-label="Estatísticas da loja">
          <div class="stat-item">
            <span class="stat-number">42</span>
            <span class="stat-label">Produtos</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">4.9</span>
            <span class="stat-label">Avaliação</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">320</span>
            <span class="stat-label">Vendas</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Produtos -->
    <section class="store-section item-animado atraso-2" aria-labelledby="produtos-titulo">
      <div class="section-header">
        <h2 id="produtos-titulo">Nossos produtos</h2>
        <a href="#" class="view-all-link">
          Todos os produtos <i data-lucide="arrow-right"></i>
        </a>
      </div>

      <div class="products-grid" id="products-grid">

        <div class="product-card" data-id="1">
          <div class="product-image">
            <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" loading="lazy" />
            <span class="product-badge">Destaque</span>
            <button class="product-favorite" aria-label="Favoritar Kit Panela de Barro">
              <i data-lucide="heart"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-name">Kit Panela de Barro</h3>
            <div class="product-footer">
              <span class="product-price">R$ 200</span>
              <button class="add-cart-btn" aria-label="Adicionar Kit Panela de Barro ao carrinho">
                <i data-lucide="plus"></i> Adicionar
              </button>
            </div>
          </div>
        </div>

        <div class="product-card" data-id="2">
          <div class="product-image">
            <img src="../assets/imgs/kit-panela.jpg" alt="Panela Individual de Barro" loading="lazy" />
            <button class="product-favorite" aria-label="Favoritar Panela Individual">
              <i data-lucide="heart"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-name">Panela Individual</h3>
            <div class="product-footer">
              <span class="product-price">R$ 90</span>
              <button class="add-cart-btn" aria-label="Adicionar Panela Individual ao carrinho">
                <i data-lucide="plus"></i> Adicionar
              </button>
            </div>
          </div>
        </div>

        <div class="product-card" data-id="3">
          <div class="product-image">
            <img src="../assets/imgs/kit-panela.jpg" alt="Travessa de Barro" loading="lazy" />
            <span class="product-badge">Novo</span>
            <button class="product-favorite" aria-label="Favoritar Travessa de Barro">
              <i data-lucide="heart"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-name">Travessa de Barro</h3>
            <div class="product-footer">
              <span class="product-price">R$ 140</span>
              <button class="add-cart-btn" aria-label="Adicionar Travessa de Barro ao carrinho">
                <i data-lucide="plus"></i> Adicionar
              </button>
            </div>
          </div>
        </div>

        <div class="product-card" data-id="4">
          <div class="product-image">
            <img src="../assets/imgs/kit-panela.jpg" alt="Tigela Artesanal" loading="lazy" />
            <button class="product-favorite" aria-label="Favoritar Tigela Artesanal">
              <i data-lucide="heart"></i>
            </button>
          </div>
          <div class="product-info">
            <h3 class="product-name">Tigela Artesanal</h3>
            <div class="product-footer">
              <span class="product-price">R$ 75</span>
              <button class="add-cart-btn" aria-label="Adicionar Tigela Artesanal ao carrinho">
                <i data-lucide="plus"></i> Adicionar
              </button>
            </div>
          </div>
        </div>

      </div>
    </section>

    <!-- Nossa história -->
    <section class="store-section history-section item-animado atraso-3" aria-labelledby="historia-titulo">
      <div class="history-container">
        <div class="history-image">
          <img src="../assets/imgs/historia-paneleira.jpg" alt="Artesã modelando panela de barro" loading="lazy" />
        </div>
        <div class="history-text">
          <h2 id="historia-titulo">Nossa história</h2>
          <p>
            Preservamos uma tradição centenária de produção artesanal de panelas de barro,
            símbolo da cultura capixaba. Cada peça carrega a identidade, o suor e o amor
            passado de geração em geração.
          </p>
          <span class="history-tag">Artesanato certificado</span>
        </div>
      </div>
    </section>

  </main>

  <!-- Toast de feedback -->
  <div class="toast" id="toast" role="status" aria-live="polite">
    <i data-lucide="check-circle"></i>
    <span id="toast-msg">Adicionado ao carrinho!</span>
  </div>

  <script src="../assets/js/arace-state.js"></script>
  <script src="../assets/js/loja.js"></script>
</body>
</html>