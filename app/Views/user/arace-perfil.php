﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Perfil</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=DM+Serif+Display&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/perfil.css" rel="stylesheet" />
</head>
<body>

<!-- HEADER -->
<header>
  <a href="/" class="logo">aracê</a>

  <form class="search-wrap" action="../main/arace-search.html" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='../main/carrinho.html'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='../user/favoritos.html'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='../user/perfil.html'">
      <i data-lucide="user"></i>
    </button>
  </div>
</header>

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="../user/arace-chat.html">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

  <!-- SIDEBAR -->
  <aside>
    <a class="nav-item" href="../main/index.html">
      <i data-lucide="house"></i> Home page
    </a>
    <a class="nav-item" href="../main/arace-search.html">
      <i data-lucide="shopping-bag"></i> Produtos
    </a>
    <a class="nav-item" href="../main/carrinho.html">
      <i data-lucide="shopping-cart"></i> Carrinho
    </a>
    <a class="nav-item" href="notificacoes.html">
      <i data-lucide="bell"></i> Notificações
    </a>
    <a class="nav-item" href="../main/configuracoes.html">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item active" href="perfil.html">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="../authentication/cadastro-produtor.html">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="../main/configuracoes.html#pagamento">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>

  <!-- MAIN -->
  <main>


    <!-- Profile Card -->
    <div class="profile-card item-animado atraso-2">
      <div class="profile-header">
        <div class="avatar-wrap">
          <div class="avatar"><i data-lucide="user"></i></div>
          </div>
        </div>
        <div class="profile-meta">
          <div class="profile-name">Usuário</div>
          <div class="profile-email">usuario@gmail.com</div>
        </div>
        <a href="../main/configuracoes.html">
          <button class="btn-edit">
        <i data-lucide="pencil"></i> Editar perfil
          </button>
        </a>
      </div>

      <div class="fields">
        <div class="field">
          <div class="field-label">Nome</div>
          <div class="field-value">Usuário</div>
        </div>
        <div class="field">
          <div class="field-label">E-mail</div>
          <div class="field-value">usuario@gmail.com</div>
        </div>
        <div class="field">
          <div class="field-label">Número</div>
          <div class="field-value missing">
            <i data-lucide="alert-circle"></i> Não informado
          </div>
        </div>
        <div class="field">
          <div class="field-label">Localização</div>
          <div class="field-value">Cariacica – ES</div>
        </div>
        <div class="field">
          <div class="field-label">Membro desde</div>
          <div class="field-value">Janeiro de 2024</div>
        </div>
        <div class="field">
          <div class="field-label">CPF</div>
          <div class="field-value missing">
            <i data-lucide="alert-circle"></i> Não informado
          </div>
        </div>
      </div>
    </div>

  </main>
  <script src="/js/arace-state.js"></script>
  <script src="/js/perfil.js"></script>
</body>
</html>