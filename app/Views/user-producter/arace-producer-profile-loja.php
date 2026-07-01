<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$produtor = $produtor ?? [];
$produtos = $produtos ?? [];
$lojaNome = (string) ($produtor['lojaNome'] ?? $produtor['nome_loja'] ?? $produtor['nome'] ?? '');
$lojaBio = (string) ($produtor['lojaBio'] ?? $produtor['bio'] ?? '');
$lojaCidade = (string) ($produtor['lojaCidade'] ?? $produtor['cidade'] ?? '');
$lojaEstado = (string) ($produtor['lojaEstado'] ?? $produtor['estado'] ?? '');
$lojaCategoria = (string) ($produtor['lojaCategoria'] ?? $produtor['categoria'] ?? '');
$lojaLocal = trim($lojaCidade . ($lojaEstado !== '' ? ', ' . $lojaEstado : ''), ', ');
$lojaAvatar = trim((string) ($produtor['fotoUrl'] ?? $produtor['lojaAvatar'] ?? $produtor['avatar'] ?? ''));
$bannerUrl = trim((string) ($produtor['bannerUrl'] ?? $produtor['banner'] ?? ''));
$lojaPix = trim((string) ($produtor['pix'] ?? ''));
$fotosHistoria = array_values(array_filter(array_map('strval', is_array($produtor['fotosHistoria'] ?? null) ? $produtor['fotosHistoria'] : [])));
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê - Minha Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/loja.css?v=20260701-logout-style') ?>" rel="stylesheet" />
</head>
<body>
<header>
  <a href="<?= url_to('home') ?>" class="logo">aracê</a>

  <form class="search-wrap" action="<?= url_to('main_pesquisa') ?>" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('main_arace_carrinho') ?>'">
      <i data-lucide="shopping-cart"></i>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'" aria-label="Abrir perfil">
      <?php if ($avatar !== ''): ?>
        <img src="<?= esc($avatar, 'attr') ?>" alt="Avatar do usuário" />
      <?php else: ?>
        <i data-lucide="user"></i>
      <?php endif; ?>
    </button>
  </div>
</header>

<aside aria-label="Navegação principal">
  <a class="nav-item" href="<?= url_to('home') ?>">
    <i data-lucide="house"></i> Home page
  </a>
  <a class="nav-item" href="<?= url_to('produtor_painel') ?>">
    <i data-lucide="layout-dashboard"></i> Painel
  </a>
  <a class="nav-item" href="<?= url_to('produtor_produto_novo') ?>">
    <i data-lucide="plus"></i> Criar produto
  </a>
  <a class="nav-item" href="<?= url_to('produtor_painel') ?>">
    <i data-lucide="shopping-bag"></i> Meus produtos
  </a>
  <a class="nav-item" href="<?= url_to('produtor_pedidos') ?>">
    <i data-lucide="package"></i> Pedidos
  </a>
  <a class="nav-item active" href="<?= url_to('produtor_perfil_loja') ?>" aria-current="page">
    <i data-lucide="store"></i> Minha loja
  </a>
  <a class="nav-item" href="<?= url_to('produtor_perfil') ?>">
    <i data-lucide="user"></i> Perfil
  </a>
  <div class="nav-divider"></div>
  <div class="nav-section">Suporte</div>
  <a class="nav-item" href="<?= url_to('produtor_config_loja') ?>">
    <i data-lucide="settings"></i> Configurações da loja
  </a>
  <form class="logout-form" action="<?= site_url('sair') ?>" method="post">
    <button class="nav-item logout-button" type="submit">
      <i data-lucide="log-out"></i> Sair da conta
    </button>
  </form>
</aside>

<main>
  <nav class="breadcrumb" aria-label="Caminho de navegação">
    <a href="<?= url_to('home') ?>">Home</a>
    <i data-lucide="chevron-right"></i>
    <span>Minha loja</span>
  </nav>

  <section class="store-banner-wrapper item-animado atraso-1" aria-label="Banner da loja">
    <div class="store-banner">
      <?php if ($bannerUrl !== ''): ?>
        <img src="<?= esc($bannerUrl, 'attr') ?>" alt="Capa da loja <?= esc($lojaNome, 'attr') ?>" class="banner-img" />
      <?php endif; ?>
      <div class="banner-overlay" aria-hidden="true"></div>
      <div class="banner-actions">
        <a class="banner-action-btn" id="btn-config-loja" href="<?= site_url('arace-config-producer-loja') ?>">
          <i data-lucide="settings"></i> Configurações da loja
        </a>
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
        <?php if ($lojaNome !== ''): ?>
          <h1 class="store-title"><?= esc($lojaNome) ?></h1>
        <?php endif; ?>
        <?php if ($lojaLocal !== ''): ?>
          <p class="store-subtitle">
            <i data-lucide="map-pin"></i>
            <?= esc(implode(' - ', array_filter([$lojaLocal, $lojaCategoria], static fn (string $valor): bool => $valor !== ''))) ?>
          </p>
        <?php endif; ?>
      </div>
      <?php if (isset($produtor['vendas'])): ?>
        <div class="store-stats" aria-label="Estatísticas da loja">
          <div class="stat-item">
            <span class="stat-number"><?= (int) $produtor['vendas'] ?></span>
            <span class="stat-label">Vendas</span>
          </div>
        </div>
      <?php endif; ?>
    </div>
  </section>

  <section class="store-section item-animado atraso-2" aria-labelledby="produtos-titulo">
    <div class="section-header">
      <h2 id="produtos-titulo">Nossos produtos</h2>
      <a href="<?= url_to('produtor_produto_novo') ?>" class="add-product-link" aria-label="Criar produto">
        <i data-lucide="plus"></i>
      </a>
    </div>

    <div class="products-grid" id="products-grid">
      <?php if ($produtos === []): ?>
        <div class="fav-empty">
          <i data-lucide="package-open"></i>
          <h2>Nenhum produto cadastrado</h2>
          <p>Os produtos da loja aparecerão aqui quando estiverem no Firestore.</p>
        </div>
      <?php endif; ?>
      <?php foreach ($produtos as $produto): ?>
        <?php
          $produtoId = (string) ($produto['id'] ?? '');
          $nomeProduto = (string) ($produto['nome'] ?? '');
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
          </div>
          <div class="product-info">
            <h3 class="product-name"><?= esc($nomeProduto) ?></h3>
            <div class="product-footer">
              <span class="product-price">R$ <?= number_format($precoProduto, 2, ',', '.') ?></span>
            </div>
          </div>
        </div>
      <?php endforeach; ?>
    </div>
  </section>

  <?php if ($lojaPix !== ''): ?>
    <section class="store-section store-payment-section item-animado atraso-3" aria-labelledby="pagamento-titulo">
      <div class="section-header">
        <h2 id="pagamento-titulo">Pagamento</h2>
      </div>
      <div class="payment-info">
        <span>Chave Pix</span>
        <strong><?= esc($lojaPix) ?></strong>
      </div>
    </section>
  <?php endif; ?>

  <?php if ($lojaBio !== '' || $fotosHistoria !== []): ?>
    <section class="store-section history-section item-animado atraso-3" aria-labelledby="historia-titulo">
      <div class="history-container">
        <?php if ($fotosHistoria !== []): ?>
          <div class="history-images">
            <?php foreach ($fotosHistoria as $fotoHistoria): ?>
              <img src="<?= esc($fotoHistoria, 'attr') ?>" alt="Foto da história da loja <?= esc($lojaNome, 'attr') ?>" loading="lazy" />
            <?php endforeach; ?>
          </div>
        <?php endif; ?>
        <?php if ($lojaBio !== ''): ?>
          <div class="history-text">
            <h2 id="historia-titulo">Nossa história</h2>
            <p><?= esc($lojaBio) ?></p>
          </div>
        <?php endif; ?>
      </div>
    </section>
  <?php endif; ?>
</main>

<script>
  window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
  window.ARACE_PRODUCER = <?= json_encode($produtor, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
  window.ARACE_STORE_PRODUCTS = <?= json_encode($produtos, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
</script>
<script src="<?= base_url('js/arace-state.js') ?>"></script>
<script src="<?= base_url('js/loja.js?v=20260701-produtor-aside') ?>"></script>
</body>
</html>
