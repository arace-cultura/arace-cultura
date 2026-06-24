﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê — Cultura Capixaba</title>
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:ital,wght@0,700;1,400&display=swap" rel="stylesheet"/>
  <script src="https://unpkg.com/lucide@latest"></script>
  <link rel="stylesheet" href="/css/landing.css"/>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
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
    <!-- Deveriam ser <a></a> -->
    <button class="cart-btn" type="button" onclick="window.location.href='/arace-carrinho'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='/usuario/arace-favoritos'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='login'">
      <i data-lucide="user"></i>
    </button>
  </div>
</header>
<!-- HERO -->
<section class="hero">
  <div class="hero-slides">
    <div class="hero-slide active" style="background-image: url('<?= base_url('images/bahia-vitoria.jpg') ?>')"></div>
    <div class="hero-slide" style="background-image: url('<?= base_url('images/baiavitoria2.jpg') ?>')"></div>
    <div class="hero-slide" style="background-image: url('<?= base_url('images/convento1.jpg') ?>')"></div>
  </div>
  <div class="hero-overlay"></div>
  <div class="hero-content">
    <h1>Um espaço da cultura capixaba.<br/>Leve parte desse lugar.</h1>

    <div class="hero-dots" id="heroDots" aria-label="Slides em destaque"></div>
  </div>
</section>

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="/usuario/chat">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

<!-- CATEGORIAS -->
<section class="section" id="categorias">
  <div class="wrap">
    <div class="section-head">
      <h2>Categorias</h2>
    </div>
    <div class="cat-grid">
      <a class="cat-card" href="/pesquisa"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gallery-horizontal-end"></i></div><span>Tudo</span></a>
      <a class="cat-card" href="/pesquisa?categoria=pinturas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="palette"></i></div><span>Pinturas</span></a>
      <a class="cat-card" href="/pesquisa?categoria=casa-e-vida"><div class="cat-icon" style="background:#25518f"><i data-lucide="house"></i></div><span>Casa & Vida</span></a>
      <a class="cat-card" href="/pesquisa?categoria=retro"><div class="cat-icon" style="background:#478632"><i data-lucide="videotape"></i></div><span>Retro</span></a>
      <a class="cat-card" href="/pesquisa?categoria=joias"><div class="cat-icon" style="background:#f3a621"><i data-lucide="gem"></i></div><span>Joias</span></a>
      <a class="cat-card" href="/pesquisa?categoria=roupas"><div class="cat-icon" style="background:#f2601a"><i data-lucide="shirt"></i></div><span>Roupas</span></a>
      <a class="cat-card" href="/pesquisa?categoria=ceramica"><div class="cat-icon" style="background:#25518f"><i data-lucide="amphora"></i></div><span>Cerâmica</span></a>
      <a class="cat-card" href="/pesquisa?categoria=artesanato"><div class="cat-icon" style="background:#478632"><i data-lucide="paintbrush"></i></div><span>Artesanato</span></a>
    </div>
  </div>
</section>

<!-- PRODUTOS -->
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
      <!-- Firestore/API: os cards serao renderizados por landing.js. -->
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

<!-- PRODUTORES -->
<section class="section produtores-bg">
  <div class="wrap">
    <div class="section-head">
      <h2>Destaques Produtores</h2>
      <a href="/produtor/painel" class="link-ver">Ver todos <i data-lucide="arrow-right"></i></a>
    </div>
    <div class="produtores-grid" id="produtoresGrid">
      <!-- Firestore/API: os produtores serao renderizados por landing.js. -->
    </div>
  </div>
</section>


<!-- LOJAS com MAPA -->
<section class="section">
  <div class="wrap">
    <div class="section-head">
      <h2>Sugestão de Loja</h2>
      <a href="#" class="link-ver">Ver todas <i data-lucide="arrow-right"></i></a>
    </div>
    <div class="lojas-mapa-grid">

      <!-- Lojas (esquerda) -->
      <div class="lojas-col">
        <div class="loja loja-azul">
          <div class="loja-info"><span class="loja-tag">Artesanato</span><strong>Nome da Loja</strong><a href="#" class="loja-ver">Visitar loja <i data-lucide="arrow-right"></i></a></div>
        </div>
        <div class="loja loja-laranja">
          <div class="loja-info"><span class="loja-tag">Estabelecimento</span><strong>Nome da Loja</strong><a href="#" class="loja-ver">Visitar loja <i data-lucide="arrow-right"></i></a></div>
        </div>
        <div class="loja loja-amarelo">
          <div class="loja-info"><span class="loja-tag">Estabelecimento</span><strong>Nome da Loja</strong><a href="#" class="loja-ver">Visitar loja <i data-lucide="arrow-right"></i></a></div>
        </div>
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
