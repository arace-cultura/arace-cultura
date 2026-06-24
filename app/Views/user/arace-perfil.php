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

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="/usuario/chat">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

  <!-- SIDEBAR -->
  <aside>
    <a class="nav-item" href="/">
      <i data-lucide="house"></i> Home page
    </a>
    <a class="nav-item" href="/pesquisa">
      <i data-lucide="shopping-bag"></i> Produtos
    </a>
    <a class="nav-item" href="/arace-carrinho">
      <i data-lucide="shopping-cart"></i> Carrinho
    </a>
    <a class="nav-item" href="/usuario/arace-notificacao">
      <i data-lucide="bell"></i> Notificações
    </a>
    <a class="nav-item" href="/arace-config">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item active" href="/usuario/arace-perfil">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="/cadastro/produtor">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="/arace-config#pagamento">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
    <form class="logout-form" action="/sair" method="post">
      <button class="nav-item logout-button" type="submit">
        <i data-lucide="log-out"></i> Sair da conta
      </button>
    </form>
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
          <div class="profile-name"><?= esc($usuario['nome'] ?? 'Usuário') ?></div>
          <div class="profile-email"><?= esc($usuario['email'] ?? '') ?></div>
        </div>
        <a href="/arace-config">
          <button class="btn-edit">
        <i data-lucide="pencil"></i> Editar perfil
          </button>
        </a>
      </div>

      <div class="fields">
        <div class="field">
          <div class="field-label">Nome</div>
          <div class="field-value"><?= esc($usuario['nome'] ?? 'Usuário') ?></div>
        </div>
        <div class="field">
          <div class="field-label">E-mail</div>
          <div class="field-value"><?= esc($usuario['email'] ?? '') ?></div>
        </div>
        <div class="field">
          <div class="field-label">Número</div>
          <div class="field-value missing"><?= esc($usuario['telefone'] ?? '') ?></div>
        </div>
        <div class="field">
          <div class="field-label">Localização</div>
          <div class="field-value"><?= esc(implode(' – ', array_filter([$usuario['cidade'] ?? null, $usuario['estado'] ?? null]))) ?></div>
        </div>
        <div class="field">
          <div class="field-label">Membro desde</div>
          <div class="field-value">Janeiro de 2024</div>
        </div>
        <div class="field">
          <div class="field-label">CPF</div>
          <div class="field-value missing"><?= esc($usuario['cpf'] ?? '') ?></div>
        </div>
      </div>
    </div>

  </main>
  <script>window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;</script>
  <script src="/js/arace-state.js"></script>
  <script src="/js/perfil.js"></script>
</body>
</html>
