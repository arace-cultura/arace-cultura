<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$produtor = $produtor ?? [];
$produtos = $produtos ?? [];
$lojaNome = (string) ($produtor['lojaNome'] ?? 'Paneleiras Capixabas');
$lojaBio = (string) ($produtor['lojaBio'] ?? 'Preservamos uma tradicao centenaria de producao artesanal.');
$lojaCidade = (string) ($produtor['lojaCidade'] ?? '');
$lojaEstado = (string) ($produtor['lojaEstado'] ?? '');
$lojaCategoria = (string) ($produtor['lojaCategoria'] ?? $produtor['categoria'] ?? 'Artesanato tradicional');
$lojaLocal = trim($lojaCidade . ($lojaEstado !== '' ? ', ' . $lojaEstado : ''), ', ');
$lojaAvatar = trim((string) ($produtor['fotoUrl'] ?? $produtor['lojaAvatar'] ?? $produtor['avatar'] ?? ''));
$bannerUrl = trim((string) ($produtor['bannerUrl'] ?? $produtor['banner'] ?? ''));
$bannerSrc = $bannerUrl !== '' ? $bannerUrl : base_url('images/bahia-vitoria.jpg');
$mediaAvaliacoes = $produtos === [] ? 0 : array_sum(array_map(static fn (array $produto): float => (float) ($produto['estrelas'] ?? 0), $produtos)) / count($produtos);
?>
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
      <span class="cart-count">0 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_favoritos') ?>'">
      <i data-lucide="heart"></i>
      <span class="cart-count">0 itens</span>
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
        <img src="<?= esc($bannerSrc, 'attr') ?>" alt="Capa da loja <?= esc($lojaNome, 'attr') ?>" class="banner-img" />
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
          <?php if ($lojaAvatar !== ''): ?>
            <img src="<?= esc($lojaAvatar, 'attr') ?>" alt="Foto de perfil da loja <?= esc($lojaNome, 'attr') ?>" />
          <?php else: ?>
            <i data-lucide="store"></i>
          <?php endif; ?>
        </div>
        <div class="store-meta">
          <h1 class="store-title"><?= esc($lojaNome) ?></h1>
          <p class="store-subtitle">
            <i data-lucide="map-pin"></i>
            <?= esc($lojaLocal !== '' ? $lojaLocal : 'Local nao informado') ?> · <?= esc($lojaCategoria) ?>
          </p>
        </div>
        <div class="store-stats" aria-label="Estatísticas da loja">
          <div class="stat-item">
            <span class="stat-number"><?= count($produtos) ?></span>
            <span class="stat-label">Produtos</span>
          </div>
          <div class="stat-item">
            <span class="stat-number"><?= number_format($mediaAvaliacoes, 1, ',', '.') ?></span>
            <span class="stat-label">Avaliação</span>
          </div>
          <div class="stat-item">
            <span class="stat-number"><?= (int) ($produtor['vendas'] ?? 0) ?></span>
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
        <?php if ($produtos === []): ?>
          <div class="fav-empty">
            <i data-lucide="package-open"></i>
            <h2>Nenhum produto cadastrado</h2>
            <p>Os produtos da loja aparecerao aqui quando estiverem no Firestore.</p>
          </div>
        <?php endif; ?>
        <?php foreach ($produtos as $produto): ?>
          <?php
            $produtoId = (string) ($produto['id'] ?? '');
            $nomeProduto = (string) ($produto['nome'] ?? 'Produto Arace');
            $precoProduto = (float) ($produto['preco'] ?? 0);
            $imagemProduto = (string) ($produto['img'] ?? $produto['imagem'] ?? '');
          ?>
          <div class="product-card" data-id="<?= esc($produtoId, 'attr') ?>" data-produto-id="<?= esc($produtoId, 'attr') ?>" data-preco="<?= esc((string) $precoProduto, 'attr') ?>">
            <div class="product-image">
              <?php if ($imagemProduto !== ''): ?>
                <img src="<?= esc($imagemProduto, 'attr') ?>" alt="<?= esc($nomeProduto, 'attr') ?>" loading="lazy" />
              <?php endif; ?>
              <?php if (($produto['destaque'] ?? false) || ($produto['desconto'] ?? 0) > 0): ?>
                <span class="product-badge"><?= ($produto['desconto'] ?? 0) > 0 ? '-' . (int) $produto['desconto'] . '%' : 'Destaque' ?></span>
              <?php endif; ?>
              <button class="product-favorite" aria-label="Favoritar <?= esc($nomeProduto, 'attr') ?>">
                <i data-lucide="heart"></i>
              </button>
            </div>
            <div class="product-info">
              <h3 class="product-name"><?= esc($nomeProduto) ?></h3>
              <div class="product-footer">
                <span class="product-price">R$ <?= number_format($precoProduto, 2, ',', '.') ?></span>
                <button class="add-cart-btn" aria-label="Adicionar <?= esc($nomeProduto, 'attr') ?> ao carrinho">
                  <i data-lucide="plus"></i> Adicionar
                </button>
              </div>
            </div>
          </div>
        <?php endforeach; ?>
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
          <p><?= esc($lojaBio) ?></p>
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

  <script>
    window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
    window.ARACE_PRODUCER = <?= json_encode($produtor, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
    window.ARACE_STORE_PRODUCTS = <?= json_encode($produtos, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
  </script>
  <script src="<?= base_url('js/arace-state.js') ?>"></script>
  <script src="<?= base_url('js/loja.js') ?>"></script>
</body>
</html>
