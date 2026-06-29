﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Painel</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/style-base.css" rel="stylesheet" />
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

  <!-- BOAS VINDAS -->
  <div class="painel-welcome">
    <div>
      <h1>Bom dia! </h1>
      <p>Aqui está um resumo da sua loja hoje — <span id="dataHoje"></span></p>
    </div>
    <a href="/produtor/painel" class="btn-novo-produto">
      <i data-lucide="plus"></i> Novo produto
    </a>
  </div>

  <!-- MÉTRICAS PRINCIPAIS -->
  <div class="metricas-grid">
    <div class="metrica-card">
      <div class="metrica-icon verde"><i data-lucide="circle-dollar-sign"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Faturamento (mês)</span>
        <span class="metrica-value">R$ 3.840,00</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +12% vs mês anterior</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon azul"><i data-lucide="package"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos (mês)</span>
        <span class="metrica-value">27</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +5 vs mês anterior</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon laranja"><i data-lucide="clock"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos pendentes</span>
        <span class="metrica-value">3</span>
        <span class="metrica-delta neutro">Aguardando ação</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon amarelo"><i data-lucide="star"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Avaliação média</span>
        <span class="metrica-value">4,8</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +0,2 este mês</span>
      </div>
    </div>
  </div>

  <!-- GRID CENTRAL -->
  <div class="painel-grid">

    <!-- PEDIDOS RECENTES -->
    <div class="painel-card pedidos-recentes">
      <div class="card-head">
        <h2>Pedidos recentes</h2>
        <a href="/produtor/pedidos" class="link-ver-todos">Ver todos <i data-lucide="arrow-right"></i></a>
      </div>
      <div class="pedidos-mini-list">

        
    <!-- COLUNA DIREITA -->
    <div class="painel-col-right">

      <!-- PRODUTOS DA LOJA -->
      <div class="painel-card">
        <div class="card-head">
          <h2>Meus produtos</h2>
          <a href="/produtor/painel" class="link-ver-todos">Gerenciar <i data-lucide="arrow-right"></i></a>
        </div>
        <div class="produtos-mini-list">

          

        </div>

        <a href="/produtor/painel" class="btn-add-produto">
          <i data-lucide="plus"></i> Adicionar produto
        </a>
      </div>

      <!-- AVALIAÇÕES RECENTES -->
      <div class="painel-card">
        <div class="card-head">
          <h2>Avaliações recentes</h2>
        </div>
        <div class="avaliacoes-list">

          

        </div>
      </div>

    </div>
  </div>

</main>

<script src="/js/arace-state.js"></script>
<script src="/js/producer-painel-produtos.js"></script>
</body>
</html>