<?php
$produtos  = $produtos ?? [];
$q         = $q ?? '';
$categoria = $categoria ?? '';
$usuario   = $usuario ?? session()->get('arace_user') ?? [];
$avatar    = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));

if (! function_exists('araceStars')) {
    function araceStars(float $nota): string
    {
        $html = '';

        for ($indice = 1; $indice <= 5; $indice++) {
            $icon    = $indice <= floor($nota) ? 'star' : ($indice - $nota <= 0.5 ? 'star-half' : 'star');
            $apagada = $indice > ceil($nota) ? ' style="opacity:.3"' : '';
            $html .= '<i data-lucide="' . esc($icon) . '"' . $apagada . '></i>';
        }

        return $html;
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Arace - Busca</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/search.css') ?>" rel="stylesheet" />
</head>
<body>
<header>
  <a href="<?= url_to('home') ?>" class="logo">arace</a>
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

<div class="chat-bubble">
  <a href="<?= url_to('user_chat') ?>"><i data-lucide="message-circle-more"></i></a>
</div>

<div class="page-body">
  <aside>
    <a class="nav-item" href="<?= url_to('home') ?>"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item active" href="<?= url_to('arace_produtos') ?>"><i data-lucide="shopping-bag"></i> Produtos</a>
    <a class="nav-item" href="<?= url_to('main_arace_carrinho') ?>"><i data-lucide="shopping-cart"></i> Carrinho</a>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>"><i data-lucide="settings"></i> Configuracoes</a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="<?= url_to('auth_cadastro_produtor') ?>"><i data-lucide="box"></i> Quero ser produtor</a>
  </aside>

  <div class="search-main">
    <div class="breadcrumb">
      <a href="<?= url_to('home') ?>">Inicio</a>
      <i data-lucide="chevron-right"></i>
      <span>Produtos</span>
    </div>

    <div class="search-bar-wrap">
      <form class="search-bar" action="<?= url_to('main_pesquisa') ?>" method="get">
        <i data-lucide="search"></i>
        <input type="text" name="q" id="searchPageInput" placeholder="pesquise um produto..." value="<?= esc($q) ?>" />
      </form>
    </div>

    <div class="cat-grid">
      <a class="cat-card" data-category-card="tudo" href="<?= url_to('main_pesquisa') ?>"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gallery-horizontal-end"></i></div><span>Tudo</span></a>
      <a class="cat-card" data-category-card="pinturas" href="<?= url_to('main_pesquisa') ?>?categoria=pinturas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="palette"></i></div><span>Pinturas</span></a>
      <a class="cat-card" data-category-card="casa-e-vida" href="<?= url_to('main_pesquisa') ?>?categoria=casa-e-vida"><div class="cat-icon" style="background:#25518f"><i data-lucide="house"></i></div><span>Casa & Vida</span></a>
      <a class="cat-card" data-category-card="retro" href="<?= url_to('main_pesquisa') ?>?categoria=retro"><div class="cat-icon" style="background:#478632"><i data-lucide="videotape"></i></div><span>Retro</span></a>
      <a class="cat-card" data-category-card="joias" href="<?= url_to('main_pesquisa') ?>?categoria=joias"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gem"></i></div><span>Joias</span></a>
      <a class="cat-card" data-category-card="roupas" href="<?= url_to('main_pesquisa') ?>?categoria=roupas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="shirt"></i></div><span>Roupas</span></a>
      <a class="cat-card" data-category-card="ceramica" href="<?= url_to('main_pesquisa') ?>?categoria=ceramica"><div class="cat-icon" style="background:#25518f"><i data-lucide="amphora"></i></div><span>Ceramica</span></a>
      <a class="cat-card" data-category-card="artesanato" href="<?= url_to('main_pesquisa') ?>?categoria=artesanato"><div class="cat-icon" style="background:#478632"><i data-lucide="paintbrush"></i></div><span>Artesanato</span></a>
    </div>

    <div class="results-head">
      <h2 class="results-title" id="resultsTitle"><?= esc($q !== '' ? 'Busca por "' . $q . '"' : 'Produtos') ?></h2>
      <div class="results-meta">
        <span>Mostrando <?= count($produtos) ?> produtos</span>
        <span>Filtro: <strong id="activeFilterLabel"><?= esc($categoria !== '' ? $categoria : 'Todos') ?></strong></span>
      </div>
    </div>

    <div class="produtos-grid" id="produtosGrid">
      <?php if ($produtos === []): ?>
        <p class="empty-products">Nenhum produto encontrado.</p>
      <?php endif; ?>
      <?php foreach ($produtos as $produto): ?>
        <?php
          $id         = (string) ($produto['id'] ?? url_title($produto['nome'] ?? 'produto', '-', true));
          $nome       = (string) ($produto['nome'] ?? 'Produto Arace');
          $preco      = (float) ($produto['preco'] ?? 0);
          $estrelas   = (float) ($produto['estrelas'] ?? 0);
          $avaliacoes = (int) ($produto['quantidadeAvaliacoes'] ?? $produto['avaliacoes'] ?? 0);
          $cor        = (string) ($produto['cor'] ?? '#b5a898');
          $imagens    = $produto['imagens'] ?? [];
          $imagem     = (string) ($imagens[0] ?? $produto['img'] ?? $produto['imagem'] ?? '');
          $cat        = (string) ($produto['categoria'] ?? 'artesanato');
        ?>
        <div
          class="produto"
          data-produto-id="<?= esc($id) ?>"
          data-nome="<?= esc($nome) ?>"
          data-preco="<?= esc((string) $preco) ?>"
          data-categoria="<?= esc($cat) ?>"
          data-cor="<?= esc($cor) ?>"
          data-img="<?= esc($imagem) ?>"
        >
          <div class="produto-img" style="background:<?= esc($cor) ?>">
            <?php if ($imagem !== ''): ?>
              <img src="<?= esc($imagem) ?>" alt="<?= esc($nome) ?>" loading="lazy" />
            <?php endif; ?>
            <button class="fav" type="button" aria-label="Favoritar produto"><i data-lucide="heart"></i></button>
          </div>
          <div class="produto-info">
            <a href="<?= url_to('main_produto_detalhes') ?>?id=<?= urlencode($id) ?>" class="nome"><?= esc($nome) ?></a>
            <div class="stars">
              <?= araceStars($estrelas) ?>
              <span>(<?= esc((string) $avaliacoes) ?>)</span>
            </div>
            <div class="preco"><strong>R$ <?= number_format($preco, 2, ',', '.') ?></strong></div>
          </div>
        </div>
      <?php endforeach; ?>
    </div>

    <div class="paginacao">
      <button class="pg-btn"><i data-lucide="chevron-left"></i> Antes</button>
      <button class="pg-num active">1</button>
      <button class="pg-num">2</button>
      <button class="pg-num">3</button>
      <span class="pg-sep">...</span>
      <button class="pg-num">6</button>
      <button class="pg-num">12</button>
      <button class="pg-btn">Proxima <i data-lucide="chevron-right"></i></button>
    </div>
  </div>

  <aside class="filtros-aside">
    <div class="aside-head">
      <span>Filtros</span>
      <button class="btn-limpar" type="button"><i data-lucide="sliders-horizontal"></i></button>
    </div>

    <div class="filter-group">
      <p class="filter-group-label">categorias</p>
      <label class="filter-check"><input type="checkbox" name="categoria" value="pinturas" /> Pinturas</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="casa-e-vida" /> Casa & Vida</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="retro" /> Retro</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="joias" /> Joias</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="ceramica" /> Ceramica</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="roupas" /> Roupas</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="acessorios" /> Acessorios</label>
    </div>

    

    <div class="filter-group">
      <p class="filter-group-label">Filtrar por Cidades</p>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="municipio">Municipio <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="interior">Interior <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="litoraneas">Litoraneas <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="montanhas">Montanhas <i data-lucide="chevron-right"></i></button>
    </div>

    <div class="filter-group">
      <p class="filter-group-label">Filtrar por Tags</p>
      <div class="tags-wrap">
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="pequeno">Pequeno</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="casual">Casual</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="artesanal">Artesanal</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="rustico">Rustico</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="barro">Barro</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="peca">Peca</button>
      </div>
    </div>

    <button class="btn-aplicar" type="button" id="btnAplicarFiltro">Aplicar Filtro</button>
  </aside>
</div>

<script src="/js/arace-state.js"></script>
<script src="/js/search.js"></script>
</body>
</html>
