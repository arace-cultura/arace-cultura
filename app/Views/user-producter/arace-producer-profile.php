<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['avatar'] ?? ''));
$localizacao = implode(' - ', array_filter([$usuario['cidade'] ?? null, $usuario['estado'] ?? null]));
$membroDesde = $usuario['createdAt'] ?? '';
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Perfil Produtor</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/perfil.css') ?>" rel="stylesheet" />
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

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="<?= url_to('user_chat') ?>">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

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
    <a class="nav-item" href="<?= url_to('user_arace_notificacao') ?>">
      <i data-lucide="bell"></i> Notificações
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
    <!-- Stats -->
    <div class="stats-row item-animado atraso-1">
      <div class="stat-card">
        <div class="stat-label">Vendas realizadas</div>
        <div class="stat-value">120</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Produtos para entrega</div>
        <div class="stat-value">10</div>
      </div>
      
      <div class="stat-card">
        <div class="stat-label">Pendentes</div>
        <div class="stat-value">5</div>
      </div>
    </div>

    <!-- Profile Card -->
    <div class="profile-card item-animado atraso-2">
      <div class="profile-header">
        <div class="avatar-wrap">
          <div class="avatar">
            <?php if (! empty($usuario['avatar'])): ?>
              <img src="<?= esc($usuario['avatar'], 'attr') ?>" alt="Avatar do usuario" />
            <?php else: ?>
              <i data-lucide="user"></i>
            <?php endif; ?>
          </div>
          </div>
        </div>
        <div class="profile-meta">
          <div class="profile-name"><?= esc($usuario['nome'] ?? 'Usuário') ?></div>
          <div class="profile-email"><?= esc($usuario['email'] ?? '') ?></div>
        </div>
        <a href="<?= url_to('produtor_config') ?>">
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
          <div class="field-value <?= empty($usuario['telefone']) ? 'missing' : '' ?>">
            <?= empty($usuario['telefone']) ? '<i data-lucide="alert-circle"></i> Não informado' : esc($usuario['telefone']) ?>
          </div>
        </div>
        <div class="field">
          <div class="field-label">Localização</div>
          <div class="field-value <?= $localizacao === '' ? 'missing' : '' ?>">
            <?= $localizacao === '' ? '<i data-lucide="alert-circle"></i> Não informado' : esc($localizacao) ?>
          </div>
        </div>
        <div class="field">
          <div class="field-label">Membro desde</div>
          <div class="field-value"><?= esc($membroDesde) ?></div>
        </div>
        <div class="field">
          <div class="field-label">CPF</div>
          <div class="field-value <?= empty($usuario['cpf']) ? 'missing' : '' ?>">
            <?= empty($usuario['cpf']) ? '<i data-lucide="alert-circle"></i> Não informado' : esc($usuario['cpf']) ?>
          </div>
        </div>
      </div>
    </div>

  </main>

  <script>lucide.createIcons();</script>
</body>
</html>
