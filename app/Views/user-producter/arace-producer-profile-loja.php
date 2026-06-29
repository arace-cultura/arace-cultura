<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Minha Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/loja.css') ?>" rel="stylesheet" />
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
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'">
      <i data-lucide="user"></i>
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

    <!-- Breadcrumb -->
    <nav class="breadcrumb" aria-label="Caminho de navegação">
      <a href="<?= url_to('home') ?>">Home</a>
      <i data-lucide="chevron-right"></i>
      <span>Minha loja</span>
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
        <a href="<?= url_to('arace_produtos') ?>" class="view-all-link">
          Todos os produtos <i data-lucide="arrow-right"></i>
        </a>
      </div>

      <div class="products-grid" id="products-grid">

        <div class="product-card" data-id="1">
          <div class="product-image">
            <img src="/images/produtos/panela_convento.png" alt="Kit Panela de Barro" loading="lazy" />
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
            <img src="/images/produtos/panela_tres.png" alt="Panela Individual de Barro" loading="lazy" />
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
            <img src="/images/produtos/panela_convento.png" alt="Travessa de Barro" loading="lazy" />
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
            <img src="/images/produtos/panela_tres.png" alt="Tigela Artesanal" loading="lazy" />
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
          <img src="/images/convento1.jpg" alt="Artesã modelando panela de barro" loading="lazy" />
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

  <script src="/js/arace-state.js"></script>
  <script src="/js/loja.js"></script>
</body>
</html>
