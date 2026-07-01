<?php
$produto      = $produto ?? [];
$recomendados = array_slice($recomendados ?? [], 0, 4);
$usuario      = $usuario ?? session()->get('arace_user') ?? [];
$avatar       = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$isProdutor   = in_array($usuario['isProdutor'] ?? false, [true, 1, '1', 'true'], true);

if (! function_exists('araceStars')) {
    function araceStars(float $nota): string
    {
        $html = '';

        for ($indice = 1; $indice <= 5; $indice++) {
            $icon    = $indice <= floor($nota) ? 'star' : ($indice - $nota <= 0.5 ? 'star-half' : 'star');
            $apagada = $indice > ceil($nota) ? ' style="opacity:.3"' : '';
            $html .= '<i data-lucide="' . esc($icon) . '"' . $apagada . '></i>';
        }

        return '<div class="stars">' . $html . '</div>';
    }
}

$id          = (string) ($produto['id'] ?? '');
$nome        = (string) ($produto['nome'] ?? 'Produto Arace');
$descricao   = (string) ($produto['descricao'] ?? '');
$preco       = (float) ($produto['preco'] ?? 0);
$produtoImagens = $produto['imagens'] ?? [];
$imagem      = (string) ($produtoImagens[0] ?? $produto['img'] ?? $produto['imagem'] ?? '');
$categoria   = (string) ($produto['categoria'] ?? 'artesanato');
$produtorId  = (string) ($produto['produtorId'] ?? '');
$avaliacoes  = (int) ($produto['quantidadeAvaliacoes'] ?? $produto['avaliacoes'] ?? 0);
$estrelas    = (float) ($produto['estrelas'] ?? 0);
$imagens     = array_values(array_filter(array_map('strval', $produtoImagens ?: [$imagem])));

if ($imagens === []) {
    $imagens = ['/images/produtos/panela_convento.png'];
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Arace - <?= esc($nome) ?></title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:ital,wght@0,700;1,400&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/produto.css') ?>" rel="stylesheet" />
</head>
<body>
<header>
  <a href="<?= url_to('home') ?>" class="logo">arace</a>
  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('main_arace_carrinho') ?>'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
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

<aside>
  <a class="nav-item" href="<?= url_to('home') ?>"><i data-lucide="house"></i> Home page</a>
  <a class="nav-item active" href="<?= url_to('arace_produtos') ?>"><i data-lucide="shopping-bag"></i> Produtos</a>
  <a class="nav-item" href="<?= url_to('main_arace_carrinho') ?>"><i data-lucide="shopping-cart"></i> Carrinho</a>
  <a class="nav-item" href="<?= url_to('main_arace_config') ?>"><i data-lucide="settings"></i> Configuracoes</a>
  <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>"><i data-lucide="user"></i> Perfil</a>
  <?php if (! $isProdutor): ?>
    <a class="nav-item" href="<?= url_to('auth_cadastro_produtor') ?>"><i data-lucide="box"></i> Quero ser produtor</a>
  <?php endif; ?>
</aside>

<main id="main-content">
  <nav class="breadcrumb">
    <a href="<?= url_to('home') ?>">Inicio</a>
    <i data-lucide="chevron-right"></i>
    <a href="<?= url_to('arace_produtos') ?>">Produtos</a>
    <i data-lucide="chevron-right"></i>
    <a href="<?= url_to('main_pesquisa') ?>?categoria=<?= urlencode($categoria) ?>"><?= esc($categoria) ?></a>
    <i data-lucide="chevron-right"></i>
    <span><?= esc($nome) ?></span>
  </nav>

  <div class="product-hero">
    <div class="gallery">
      <div class="gallery-main">
        <img id="mainImg" src="<?= esc($imagens[0]) ?>" alt="<?= esc($nome) ?>" />
      </div>
      <div class="gallery-thumbs">
        <?php foreach ($imagens as $indice => $img): ?>
          <button class="gallery-thumb <?= $indice === 0 ? 'active' : '' ?>" type="button" data-image="<?= esc($img) ?>">
            <img src="<?= esc($img) ?>" alt="<?= esc($nome) ?> foto <?= $indice + 1 ?>" loading="lazy" />
          </button>
        <?php endforeach; ?>
      </div>
    </div>

    <div class="product-info">
      <h1 class="product-name"><?= esc($nome) ?></h1>
      <div class="rating-row">
        <?= araceStars($estrelas) ?>
        <span class="rating-count"><?= number_format($estrelas, 1, ',', '.') ?> (<?= esc((string) $avaliacoes) ?>)</span>
      </div>

      <div class="price-row">
        <span class="price-current">R$ <?= number_format($preco, 2, ',', '.') ?></span>
      </div>

      <div class="variant-imgs">
        <?php foreach ($imagens as $indice => $img): ?>
          <button class="variant-img-btn <?= $indice === 0 ? 'active' : '' ?>" type="button" data-image="<?= esc($img) ?>">
            <img src="<?= esc($img) ?>" alt="<?= esc($nome) ?> variante <?= $indice + 1 ?>" loading="lazy" />
          </button>
        <?php endforeach; ?>
      </div>

      <div class="product-desc-block">
        <strong>Descricao</strong>
        <?php if ($descricao !== ''): ?>
          <?php foreach (preg_split('/\R/', $descricao) ?: [] as $linha): ?>
            <p><?= esc($linha) ?></p>
          <?php endforeach; ?>
        <?php else: ?>
          <p>Produto da categoria <?= esc($categoria) ?>.</p>
        <?php endif; ?>
        <?php if ($produtorId !== ''): ?>
          <p>Produtor: <?= esc($produtorId) ?></p>
        <?php endif; ?>
      </div>

      <div class="action-row">
        <button class="btn-add" type="button" data-produto-id="<?= esc($id) ?>"><i data-lucide="shopping-cart"></i> Adicionar</button>
        <button class="btn-buy" type="button"><i data-lucide="zap"></i> Comprar</button>
      </div>
    </div>
  </div>

  <h2 class="section-title">Detalhes do Produto</h2>

  <div class="product-details-grid">
    <div class="avaliacoes-col">
      <div class="reviews-header">
        <div class="reviews-meta">
          <span class="big-rating"><?= number_format($estrelas, 1, ',', '.') ?></span>
          <?= araceStars($estrelas) ?>
          <span style="font-size:13px;color:var(--muted)">(<?= esc((string) $avaliacoes) ?>)</span>
        </div>
      </div>
      <h3 style="font-size:15px;font-weight:500;margin-bottom:.75rem;">Avaliacoes</h3>
      <div class="review-list">
        <div class="review-card">
          <div class="review-card-header">
            <div><?= araceStars($estrelas) ?><span class="review-user">Media dos clientes</span></div>
            <button class="review-more" type="button"><i data-lucide="more-horizontal"></i></button>
          </div>
          <p class="review-text">Este produto soma <?= esc((string) ($produto['somaAvaliacoes'] ?? 0)) ?> pontos em <?= esc((string) $avaliacoes) ?> avaliacoes.</p>
        </div>
      </div>
    </div>

    <div class="comentarios-col">
      <div class="reviews-header">
        <span style="font-size:15px;font-weight:500;">Comentarios</span>
        <div class="reviews-filters">
          <select class="select-filter">
            <option>Recentes</option>
            <option>Melhores</option>
            <option>Piores</option>
          </select>
          <button class="btn-escrever" type="button"><i data-lucide="pen-line"></i> Escrever</button>
        </div>
      </div>
      <div class="review-list" style="margin-top:.75rem">
        <div class="review-card">
          <p class="review-text">Ainda nao ha comentarios cadastrados para este produto.</p>
        </div>
      </div>
    </div>
  </div>

  <?php if ($recomendados !== []): ?>
    <div class="rec-section">
      <h2 class="section-title">Voce pode gostar</h2>
      <div class="rec-grid">
        <?php foreach ($recomendados as $recomendado): ?>
          <?php
            $recId = (string) ($recomendado['id'] ?? '');
            $recNome = (string) ($recomendado['nome'] ?? 'Produto Arace');
            $recImagens = $recomendado['imagens'] ?? [];
            $recImg = (string) ($recImagens[0] ?? $recomendado['img'] ?? $recomendado['imagem'] ?? '/images/produtos/panela_convento.png');
            $recPreco = (float) ($recomendado['preco'] ?? 0);
            $recEstrelas = (float) ($recomendado['estrelas'] ?? 0);
          ?>
          <div class="rec-card">
            <a class="rec-card-img" href="<?= url_to('main_produto_detalhes') ?>?id=<?= urlencode($recId) ?>">
              <img src="<?= esc($recImg) ?>" alt="<?= esc($recNome) ?>" loading="lazy" />
            </a>
            <div class="rec-card-body">
              <div class="rec-card-name"><?= esc($recNome) ?></div>
              <div class="rec-card-rating"><?= araceStars($recEstrelas) ?> <?= number_format($recEstrelas, 1, ',', '.') ?></div>
              <div class="rec-card-price"><strong>R$ <?= number_format($recPreco, 2, ',', '.') ?></strong></div>
              <button class="btn-add-rec" type="button">Adicionar ao carrinho</button>
            </div>
          </div>
        <?php endforeach; ?>
      </div>
    </div>
  <?php endif; ?>
</main>

<script>window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;</script>
<script src="/js/arace-state.js"></script>
<script src="/js/produto.js"></script>
</body>
</html>
