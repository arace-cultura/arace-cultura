<!DOCTYPE html>
<?php
$produtos = $produtos ?? [];
$produtores = $produtores ?? [];

if (! function_exists('araceStars')) {
function araceStars(float $nota): string
{
    $html = '';

    for ($indice = 1; $indice <= 5; $indice++) {
        $icon = $indice <= floor($nota)
            ? 'star'
            : ($indice - $nota <= 0.5 ? 'star-half' : 'star');
        $apagada = $indice > ceil($nota) ? ' style="opacity:.3"' : '';
        $html .= '<i data-lucide="' . esc($icon) . '"' . $apagada . '></i>';
    }

    return $html;
}
}
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê — Cultura Capixaba</title>
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:ital,wght@0,700;1,400&display=swap" rel="stylesheet"/>
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link rel="stylesheet" href="<?= base_url('css/landing.css') ?>"/>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
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
    <!-- Deveriam ser <a></a> -->
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('main_arace_carrinho') ?>'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">0 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('auth_login') ?>'">
      <i data-lucide="user"></i>
    </button>
  </div>
</header>
<!-- HERO -->
<section class="hero">
  <div class="hero-slides">
    <div class="hero-slide active" style="background-image: url('/images/bahia-vitoria.jpg')"></div>
    <div class="hero-slide" style="background-image: url('/images/baiavitoria2.jpg')"></div>
    <div class="hero-slide" style="background-image: url('/images/convento1.jpg')"></div>
  </div>
  <div class="hero-overlay"></div>
  <div class="hero-content">
    <h1>Um espaço da cultura capixaba.<br/>Leve parte desse lugar.</h1>

    <div class="hero-dots" id="heroDots" aria-label="Slides em destaque"></div>
  </div>
</section>
<!-- CATEGORIAS -->
<section class="section" id="categorias">
  <div class="wrap">
    <div class="section-head">
      <h2>Categorias</h2>
    </div>
    <div class="cat-grid">
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gallery-horizontal-end"></i></div><span>Tudo</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=pinturas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="palette"></i></div><span>Pinturas</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=casa-e-vida"><div class="cat-icon" style="background:#25518f"><i data-lucide="house"></i></div><span>Casa & Vida</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=retro"><div class="cat-icon" style="background:#478632"><i data-lucide="videotape"></i></div><span>Retro</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=joias"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gem"></i></div><span>Joias</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=roupas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="shirt"></i></div><span>Roupas</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=ceramica"><div class="cat-icon" style="background:#25518f"><i data-lucide="amphora"></i></div><span>Cerâmica</span></a>
      <a class="cat-card" href="<?= url_to('main_pesquisa') ?>?categoria=artesanato"><div class="cat-icon" style="background:#478632"><i data-lucide="paintbrush"></i></div><span>Artesanato</span></a>
    </div>
  </div>
</section>

<<!-- PRODUTOS 
<section class="section" id="produtos">
  <div class="wrap">
    <div class="section-head">
      <h2>Produtos em destaque</h2>
      <div class="filters">
        <button class="filter active" data-filter="all">Todos</button>
        <button class="filter" data-filter="ceramica">Cerâmica</button>
        <button class="filter" data-filter="pintura">Pinturas</button>
        <button class="filter" data-filter="madeira">Madeira</button>
      </div>
    </div>

    <div class="produtos-grid" id="produtosGrid">
      <?php if ($produtos === []): ?>
        <p class="empty-products">Nenhum produto encontrado.</p>
      <?php endif; ?>
      <?php foreach ($produtos as $produto): ?>
        <?php
          $id = (string) ($produto['id'] ?? url_title($produto['nome'] ?? 'produto', '-', true));
          $nome = (string) ($produto['nome'] ?? 'Produto Arace');
          $artesao = (string) ($produto['artesao'] ?? 'Produtor Arace');
          $categoria = (string) ($produto['categoria'] ?? 'artesanato');
          $preco = (float) ($produto['preco'] ?? 0);
          $avaliacoes = (int) ($produto['avaliacoes'] ?? 0);
          $estrelas = (float) ($produto['estrelas'] ?? 4);
          $cor = (string) ($produto['cor'] ?? '#b5a898');
          $imagens = $produto['imagens'] ?? [];
          $imagem = (string) ($imagens[0] ?? $produto['img'] ?? $produto['imagem'] ?? '');
        ?>
        <article
          class="produto"
          data-cat="<?= esc($categoria) ?>"
          data-produto-id="<?= esc($id) ?>"
          data-nome="<?= esc($nome) ?>"
          data-artesao="<?= esc($artesao) ?>"
          data-preco="<?= esc((string) $preco) ?>"
          data-categoria="<?= esc($categoria) ?>"
          data-cor="<?= esc($cor) ?>"
          data-img="<?= esc($imagem) ?>"
        >
          <div class="produto-img" style="background:<?= esc($cor) ?>">
            <?php if ($imagem !== ''): ?>
              <img src="<?= esc($imagem) ?>" alt="<?= esc($nome) ?>" loading="lazy" />
            <?php endif; ?>
          </div>
          <div class="produto-info">
            <span class="artesao"><?= esc($artesao) ?></span>
            <a href="<?= url_to('main_produto_detalhes') ?>?id=<?= urlencode($id) ?>" class="nome"><?= esc($nome) ?></a>
            <div class="stars">
              <?= araceStars($estrelas) ?>
              <span>(<?= esc((string) $avaliacoes) ?>)</span>
            </div>
            <div class="preco"><strong>R$ <?= number_format($preco, 2, ',', '.') ?></strong></div>
            <button class="add-cart" type="button" data-produto-id="<?= esc($id) ?>">
              <i data-lucide="shopping-cart"></i> Adicionar ao carrinho
            </button>
          </div>
        </article>
      <?php endforeach; ?>
    </div>

    <div class="paginacao">
      <button class="pg active">1</button>
      <button class="pg">2</button>
      <button class="pg">3</button>
      <span>…</span>
      <button class="pg">12</button>
      <button class="pg"><i data-lucide="chevron-right"></i></button>
    </div>
  </div>
</section>

-->

<!-- PRODUTORES -->
<section class="section produtores-bg">
  <div class="wrap">
    <div class="section-head">
      <h2>Destaques Produtores</h2>
      <a href="<?= url_to('main_pesquisa') ?>" class="link-ver">Ver todos <i data-lucide="arrow-right"></i></a>
    </div>
    <div class="produtores-grid" id="produtoresGrid">
      <?php foreach (array_slice($produtores, 0, 4) as $produtor): ?>
        <article class="produtor">
          <div class="avatar"><?= esc($produtor['iniciais'] ?? 'AR') ?></div>
          <span class="p-nome"><?= esc($produtor['nome'] ?? 'Produtor Arace') ?></span>
          <span class="p-qtd"><?= esc((string) ($produtor['produtos'] ?? 0)) ?> produtos</span>
        </article>
      <?php endforeach; ?>
    </div>
  </div>
</section>


<!-- LOJAS com MAPA -->
<section class="section">
  <div class="wrap">
    <div class="section-head">
      <h2>Sugestão de Loja</h2>
      <a href="<?= url_to('main_pesquisa') ?>" class="link-ver">Ver todas <i data-lucide="arrow-right"></i></a>
    </div>
    <div class="lojas-mapa-grid">

      <!-- Lojas (esquerda) -->
      <div class="lojas-col">
        <?php foreach (array_slice($produtores, 0, 3) as $indice => $produtor): ?>
          <?php $classeLoja = ['loja-azul', 'loja-laranja', 'loja-amarelo'][$indice] ?? 'loja-azul'; ?>
          <div class="loja <?= esc($classeLoja, 'attr') ?>">
            <div class="loja-info">
              <span class="loja-tag"><?= esc($produtor['categoria'] ?? 'Artesanato') ?></span>
              <strong><?= esc($produtor['nome'] ?? 'Produtor Arace') ?></strong>
              <a href="<?= url_to('main_pesquisa') ?>" class="loja-ver">Visitar loja <i data-lucide="arrow-right"></i></a>
            </div>
          </div>
        <?php endforeach; ?>
      </div>

      <!-- Mapa (direita) -->
      <div class="mapa">
        <div id="mapa"></div>
      </div>

    </div>
  </div>
</section>



<script src="/js/arace-state.js"></script>
<script src="/js/landing.js"></script>
<script src="/js/search-navigation.js"></script>
</body>
</html>
