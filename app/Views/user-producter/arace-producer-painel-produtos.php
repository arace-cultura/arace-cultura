<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$produtos = $produtos ?? [];
$pedidos = $pedidos ?? [];
$metricas = $metricas ?? ['faturamento' => 0, 'pedidos' => 0, 'pendentes' => 0, 'avaliacao' => 0];
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Painel</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/style-base.css') ?>" rel="stylesheet" />
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
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>

<main>

  <!-- BOAS VINDAS -->
  <div class="painel-welcome">
    <div>
      <h1>Bom dia! </h1>
      <p>Aqui está um resumo da sua loja hoje — <span id="dataHoje"></span></p>
    </div>
    <a class="btn-novo-produto" href="<?= url_to('produtor_produto_novo') ?>">
      <i data-lucide="plus"></i> Novo produto
    </a>
  </div>

  <!-- GRID CENTRAL -->
  <div class="painel-grid">
    <div class="painel-card pedidos-recentes">
      <div class="card-head">
        <h2>Pedidos recentes</h2>
        <a href="<?= url_to('produtor_pedidos') ?>" class="link-ver-todos">Ver todos <i data-lucide="arrow-right"></i></a>
      </div>
      <div class="pedidos-mini-list">
        <?php if ($pedidos === []): ?>
          <div class="pedido-mini">
            <div class="pedido-mini-info">
              <span class="pedido-mini-cliente">Nenhum pedido recebido</span>
              <span class="pedido-mini-produto">Os pedidos da loja aparecerao aqui quando estiverem no Firestore.</span>
            </div>
          </div>
        <?php endif; ?>
        <?php foreach (array_slice($pedidos, 0, 5) as $pedido): ?>
          <div class="pedido-mini" data-href="<?= url_to('produtor_pedidos') ?>">
            <div class="pedido-mini-id">#<?= esc((string) ($pedido['id'] ?? '')) ?></div>
            <div class="pedido-mini-info">
              <span class="pedido-mini-cliente"><?= esc($pedido['cliente'] ?? 'Cliente Arace') ?></span>
              <span class="pedido-mini-produto"><?= esc($pedido['produto'] ?? 'Pedido Arace') ?> x<?= (int) ($pedido['qtd'] ?? 1) ?></span>
            </div>
            <span class="status-badge <?= esc($pedido['status'] ?? 'pendente', 'attr') ?>"><?= esc($pedido['status'] ?? 'pendente') ?></span>
            <span class="pedido-mini-valor">R$<?= number_format((float) ($pedido['valor'] ?? 0), 2, ',', '.') ?></span>
          </div>
        <?php endforeach; ?>
      </div>
    </div>

    <div class="painel-col-right">
      <div class="painel-card">
        <div class="card-head">
          <h2>Meus produtos</h2>
          <a href="<?= url_to('produtor_painel') ?>" class="link-ver-todos">Gerenciar <i data-lucide="arrow-right"></i></a>
        </div>
        <div class="produtos-mini-list">
          <?php if ($produtos === []): ?>
            <div class="produto-mini">
              <div class="produto-mini-info">
                <span class="produto-mini-nome">Nenhum produto cadastrado</span>
                <span class="produto-mini-preco">Cadastre produtos no Firestore para listar aqui.</span>
              </div>
            </div>
          <?php endif; ?>
          <?php foreach (array_slice($produtos, 0, 3) as $produto): ?>
            <div class="produto-mini">
              <div class="produto-mini-img" style="background:<?= esc($produto['cor'] ?? '#b5a898', 'attr') ?>"></div>
              <div class="produto-mini-info">
                <span class="produto-mini-nome"><?= esc($produto['nome'] ?? 'Produto Arace') ?></span>
                <span class="produto-mini-preco">R$ <?= number_format((float) ($produto['preco'] ?? 0), 2, ',', '.') ?></span>
              </div>
              <span class="stock-badge"><?= ($produto['disponivel'] ?? true) ? 'Em estoque' : 'Indisponivel' ?></span>
              <div class="produto-mini-actions">
                <button type="button" data-href="<?= url_to('produtor_painel') ?>" title="Editar"><i data-lucide="pencil"></i></button>
              </div>
            </div>
          <?php endforeach; ?>
        </div>

        <a class="btn-add-produto" href="<?= url_to('produtor_produto_novo') ?>">
          <i data-lucide="plus"></i> Adicionar produto
        </a>
      </div>

      <div class="painel-card">
        <div class="card-head">
          <h2>Avaliacoes recentes</h2>
        </div>
        <div class="avaliacoes-list">
          <div class="avaliacao-mini">
            <p>Ainda nao ha avaliacoes registradas no Firestore.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</main>

<script src="/js/arace-state.js"></script>
<script src="/js/producer-painel-produtos.js"></script>
</body>
</html>
