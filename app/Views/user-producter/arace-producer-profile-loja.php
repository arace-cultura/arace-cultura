﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Minha Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/loja.css" rel="stylesheet" />
</head>
<body>

  
  <!-- HEADER -->
<header>
  <a href="/" class="logo">aracê</a>

  <form class="search-wrap" action="/pesquisa" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='/arace-carrinho'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='/usuario/arace-favoritos'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='/usuario/arace-perfil'">
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

    <a class="nav-item" href="/arace-config">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="/usuario/arace-perfil">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="/cadastro/produtor">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    
  </aside>

  
  <main>

    <!-- Breadcrumb -->
    <nav class="breadcrumb" aria-label="Caminho de navegação">
      <a href="/">Home</a>
      <i data-lucide="chevron-right"></i>
      <span>Loja</span>
    </nav>

   
    <section class="store-banner-wrapper item-animado atraso-1" aria-label="Banner da loja">
      <div class="store-banner">
        <img src="/images/bahia-vitoria.jpg" alt="Paneleiras capixabas trabalhando" class="banner-img" />
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
          <img src="/images/arace.png" alt="Foto de perfil da loja Paneleiras Capixabas" />
        </div>
        <div class="store-meta">
          <h1 class="store-title"></h1>
          <p class="store-subtitle">
            <i data-lucide="map-pin"></i>
            
          </p>
        </div>
        <div class="store-stats" aria-label="Estatísticas da loja">
          <div class="stat-item">
            <span class="stat-number"></span>
            <span class="stat-label">Produtos</span>
          </div>
          <div class="stat-item">
            <span class="stat-number"></span>
            <span class="stat-label">Avaliação</span>
          </div>
          <div class="stat-item">
            <span class="stat-number"></span>
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

      <
        </div>

      </div>
    </section>

    <!-- Nossa história -->
    <section class="store-section history-section item-animado atraso-3" aria-labelledby="historia-titulo">
      <div class="history-container">
        <div class="history-image">
          <img src="/images/convento1.jpg" alt="Artesã modelando panela de barro" loading="lazy" />
        </div>
        <div class="history-text">
          <h2 id="historia-titulo"></h2>
          <p>
            
          </p>
          <span class="history-tag"></span>
        </div>
      </div>
    </section>

  </main>

  <!-- Toast de feedback -->
  <div class="toast" id="toast" role="status" aria-live="polite">
    <i data-lucide="check-circle"></i>
    <span id="toast-msg">Adicionado ao carrinho!</span>
  </div>

  <script src="/js/arace-state.js"></script>
  <script src="/js/loja.js"></script>
</body>
</html>
