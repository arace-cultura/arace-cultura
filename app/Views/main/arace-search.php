﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Busca</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/search.css" rel="stylesheet" />
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


<!-- LAYOUT PRINCIPAL -->
<div class="page-body">

  <!-- NAV ASIDE (esquerda) -->
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

    <!-- CONTEÚDO -->
  <div class="search-main">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <a href="/">Início</a>
      <i data-lucide="chevron-right"></i>
      <span>Produtos</span>
    </div>

    <!-- Busca central -->
    <div class="search-bar-wrap">
      <form class="search-bar" action="/pesquisa" method="get">
        <i data-lucide="search"></i>
        <input type="text" name="q" id="searchPageInput" placeholder="pesquise um produto..." />
      </form>
    </div>

    <!-- Categorias -->
    <div class="cat-grid">
      <a class="cat-card" data-category-card="tudo" href="/pesquisa">
        <div class="cat-icon" style="background:#f3a621"><i data-lucide="gallery-horizontal-end"></i></div>
        <span>Tudo</span>
      </a>
      <a class="cat-card" data-category-card="pinturas" href="/pesquisa?categoria=pinturas">
        <div class="cat-icon" style="background:#f2601a"><i data-lucide="palette"></i></div>
        <span>Pinturas</span>
      </a>
      <a class="cat-card" data-category-card="casa-e-vida" href="/pesquisa?categoria=casa-e-vida">
        <div class="cat-icon" style="background:#25518f"><i data-lucide="house"></i></div>
        <span>Casa & Vida</span>
      </a>
      <a class="cat-card" data-category-card="retro" href="/pesquisa?categoria=retro">
        <div class="cat-icon" style="background:#478632"><i data-lucide="videotape"></i></div>
        <span>Retro</span>
      </a>
      <a class="cat-card" data-category-card="joias" href="/pesquisa?categoria=joias">
        <div class="cat-icon" style="background:#f3a621"><i data-lucide="gem"></i></div>
        <span>Joias</span>
      </a>
      <a class="cat-card" data-category-card="roupas" href="/pesquisa?categoria=roupas">
        <div class="cat-icon" style="background:#f2601a"><i data-lucide="shirt"></i></div>
        <span>Roupas</span>
      </a>
      <a class="cat-card" data-category-card="ceramica" href="/pesquisa?categoria=ceramica">
        <div class="cat-icon" style="background:#25518f"><i data-lucide="amphora"></i></div>
        <span>Cerâmica</span>
      </a>
      <a class="cat-card" data-category-card="artesanato" href="/pesquisa?categoria=artesanato">
        <div class="cat-icon" style="background:#478632"><i data-lucide="paintbrush"></i></div>
        <span>Artesanato</span>
      </a>
    </div>

    <!-- Header resultados -->
    <div class="results-head">
      <h2 class="results-title" id="resultsTitle">Produtos</h2>
      <div class="results-meta">
        <span>Mostrando 9 produtos</span>
        <span>Filtro: <strong id="activeFilterLabel">Todos</strong></span>
      </div>
    </div>

    <!-- Grid -->
    <div class="produtos-grid" id="produtosGrid">


    </div>

    <!-- Paginação -->
    <div class="paginacao">
      
    </div>

  </div><!-- /search-main -->

  <!-- SIDEBAR FILTROS (direita) -->
  <aside class="filtros-aside">

    <div class="aside-head">
      <span>Filtros</span>
      <button class="btn-limpar" type="button"><i data-lucide="sliders-horizontal"></i></button>
    </div>
    

    <!-- Categorias -->
    <div class="filter-group">
      <p class="filter-group-label">categorias</p>
      <label class="filter-check"><input type="checkbox" name="categoria" value="pinturas" /> Pinturas</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="casa-e-vida" /> Casa & Vida</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="retro" /> Retro</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="joias" /> Joias</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="ceramica" /> Cerâmica</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="roupas" /> Roupas</label>
      <label class="filter-check"><input type="checkbox" name="categoria" value="acessorios" /> Acessórios</label>
    </div>

    <!-- Preços -->
    
      
    

    <!-- Filtrar por Cidades -->
    <div class="filter-group">
      <p class="filter-group-label">Filtrar por Cidades</p>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="municipio">Município <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="interior">Interior <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="litoraneas">Litorâneas <i data-lucide="chevron-right"></i></button>
      <button class="filter-city" type="button" data-filter-param="cidade" data-filter-value="montanhas">Montanhas <i data-lucide="chevron-right"></i></button>
    </div>

    <!-- Tags -->
    <div class="filter-group">
      <p class="filter-group-label">Filtrar por Tags</p>
      <div class="tags-wrap">
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="pequeno">Pequeno</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="casual">Casual</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="artesanal">Artesanal</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="rustico">Rústico</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="barro">Barro</button>
        <button class="tag-btn" type="button" data-filter-param="tag" data-filter-value="peca">Peça</button>
      </div>
    </div>

    <button class="btn-aplicar" type="button" id="btnAplicarFiltro">Aplicar Filtro</button>

  </aside>
</div><!-- /page-body -->

<script src="/js/arace-state.js"></script>
<script src="/js/search.js"></script>
</body>
</html>
