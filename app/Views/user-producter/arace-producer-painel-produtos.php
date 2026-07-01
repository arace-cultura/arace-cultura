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
      <span class="cart-count">0 itens</span>
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
    <button type="button" class="btn-novo-produto" data-open-product-modal>
      <i data-lucide="plus"></i> Novo produto
    </button>
  </div>

  <!-- MÉTRICAS PRINCIPAIS -->
  <div class="metricas-grid">
    <div class="metrica-card">
      <div class="metrica-icon verde"><i data-lucide="circle-dollar-sign"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Faturamento (mês)</span>
        <span class="metrica-value">R$ <?= number_format((float) ($metricas['faturamento'] ?? 0), 2, ',', '.') ?></span>
        <span class="metrica-delta neutro">Dados do Firestore</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon azul"><i data-lucide="package"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos (mês)</span>
        <span class="metrica-value"><?= (int) ($metricas['pedidos'] ?? 0) ?></span>
        <span class="metrica-delta neutro">Total registrado</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon laranja"><i data-lucide="clock"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos pendentes</span>
        <span class="metrica-value"><?= (int) ($metricas['pendentes'] ?? 0) ?></span>
        <span class="metrica-delta neutro">Aguardando ação</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon amarelo"><i data-lucide="star"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Avaliação média</span>
        <span class="metrica-value"><?= number_format((float) ($metricas['avaliacao'] ?? 0), 1, ',', '.') ?></span>
        <span class="metrica-delta neutro">Media dos produtos</span>
      </div>
    </div>
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

        <button type="button" class="btn-add-produto" data-open-product-modal>
          <i data-lucide="plus"></i> Adicionar produto
        </button>
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

<div class="modal-overlay" id="produtoModal" aria-hidden="true">
  <div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="produtoModalTitulo">
    <form id="produtoForm" enctype="multipart/form-data">
      <div class="modal-header">
        <div>
          <h2 id="produtoModalTitulo">Novo produto</h2>
          <p class="modal-sub">Os dados serao salvos no Firestore e a imagem no Supabase.</p>
        </div>
        <button class="modal-close" type="button" data-close-product-modal aria-label="Fechar">
          <i data-lucide="x"></i>
        </button>
      </div>

      <div class="modal-body">
        <label class="modal-label" for="produtoNome">Nome do produto</label>
        <input class="input-field" type="text" id="produtoNome" name="nome" required maxlength="140" />

        <label class="modal-label" for="produtoDescricao">Descricao</label>
        <textarea class="input-field" id="produtoDescricao" name="descricao" rows="4" maxlength="1200"></textarea>

        <div class="field-row">
          <div class="field-group">
            <label class="modal-label" for="produtoPreco">Preco</label>
            <input class="input-field" type="number" id="produtoPreco" name="preco" min="0" step="0.01" required />
          </div>
          <div class="field-group">
            <label class="modal-label" for="produtoEstoque">Estoque</label>
            <input class="input-field" type="number" id="produtoEstoque" name="estoque" min="0" step="1" value="1" />
          </div>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="modal-label" for="produtoCategoria">Categoria</label>
            <input class="input-field" type="text" id="produtoCategoria" name="categoria" />
          </div>
          <div class="field-group">
            <label class="modal-label" for="produtoCor">Cor do placeholder</label>
            <input class="input-field" type="color" id="produtoCor" name="cor" value="#b5a898" />
          </div>
        </div>

        <label class="modal-label" for="produtoImagemArquivo">Imagem do produto</label>
        <input class="input-field" type="file" id="produtoImagemArquivo" name="imagemArquivo" accept="image/*" />

        <label class="modal-label" for="produtoImagemUrl">Ou URL publica da imagem</label>
        <input class="input-field" type="url" id="produtoImagemUrl" name="imagemUrl" placeholder="https://..." />

        <p class="modal-sub" id="produtoFormFeedback" aria-live="polite"></p>
      </div>

      <div class="modal-footer">
        <button class="btn-status-action" type="button" data-close-product-modal>Cancelar</button>
        <button class="btn-status-action" type="submit">
          <i data-lucide="save"></i> Salvar produto
        </button>
      </div>
    </form>
  </div>
</div>

<script src="/js/arace-state.js"></script>
<script src="/js/producer-painel-produtos.js"></script>
</body>
</html>
