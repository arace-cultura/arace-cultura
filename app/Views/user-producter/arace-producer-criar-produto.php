<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$produtor = $produtor ?? [];
$lojaNome = (string) ($produtor['nomeLoja'] ?? $produtor['nome_loja'] ?? $produtor['nome'] ?? '');
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Criar produto</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/style-base.css') ?>" rel="stylesheet" />
  <link href="<?= base_url('css/painel-produtos.css?v=20260701-create-product') ?>" rel="stylesheet" />
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
  <a class="nav-item" href="<?= url_to('produtor_painel') ?>">
    <i data-lucide="layout-dashboard"></i> Painel
  </a>
  <a class="nav-item active" href="<?= url_to('produtor_produto_novo') ?>" aria-current="page">
    <i data-lucide="plus"></i> Criar produto
  </a>
  <a class="nav-item" href="<?= url_to('produtor_painel') ?>">
    <i data-lucide="shopping-bag"></i> Meus produtos
  </a>
  <a class="nav-item" href="<?= url_to('produtor_pedidos') ?>">
    <i data-lucide="package"></i> Pedidos
  </a>
  <a class="nav-item" href="<?= url_to('produtor_perfil_loja') ?>">
    <i data-lucide="store"></i> Minha loja
  </a>
  <a class="nav-item" href="<?= url_to('produtor_perfil') ?>">
    <i data-lucide="user"></i> Perfil
  </a>
  <div class="nav-divider"></div>
  <div class="nav-section">Suporte</div>
  <a class="nav-item" href="<?= url_to('produtor_config_loja') ?>">
    <i data-lucide="settings"></i> Configurações da loja
  </a>
  <form class="logout-form" action="<?= site_url('sair') ?>" method="post">
    <button class="nav-item logout-button" type="submit">
      <i data-lucide="log-out"></i> Sair da conta
    </button>
  </form>
</aside>

<main>
  <nav class="breadcrumb create-product-breadcrumb" aria-label="Caminho de navegação">
    <a href="<?= url_to('produtor_painel') ?>">Painel</a>
    <i data-lucide="chevron-right"></i>
    <span>Criar produto</span>
  </nav>

  <div class="painel-welcome create-product-header">
    <div>
      <h1>Criar produto</h1>
      <p>Cadastre um novo item<?= $lojaNome !== '' ? ' para ' . esc($lojaNome) : '' ?>.</p>
    </div>
    <a class="btn-novo-produto" href="<?= url_to('produtor_painel') ?>">
      <i data-lucide="arrow-left"></i> Voltar ao painel
    </a>
  </div>

  <div class="create-product-layout">
    <section class="painel-card create-product-form-card" aria-labelledby="form-produto-titulo">
      <div class="create-card-head">
        <div>
          <h2 id="form-produto-titulo">Dados do produto</h2>
          <p>Preencha as informações que aparecerão na vitrine da loja.</p>
        </div>
        <i data-lucide="package-plus"></i>
      </div>

      <?php if (session('erro')): ?>
        <div class="form-alert" role="alert">
          <strong><?= esc(session('erro')) ?></strong>
          <?php foreach ((array) session('erros') as $mensagemErro): ?>
            <span><?= esc($mensagemErro) ?></span>
          <?php endforeach; ?>
        </div>
      <?php endif; ?>

      <form class="create-product-form" method="post" action="<?= url_to('produtor_produto_store') ?>" enctype="multipart/form-data">
        <?= csrf_field() ?>

        <div class="field-group">
          <label class="modal-label" for="produtoNome">Nome do produto</label>
          <input class="input-field" type="text" id="produtoNome" name="nome" required maxlength="140" value="<?= esc(old('nome'), 'attr') ?>" placeholder="Ex.: Panela de barro média" />
        </div>

        <div class="field-group">
          <label class="modal-label" for="produtoDescricao">Descrição</label>
          <textarea class="input-field" id="produtoDescricao" name="descricao" rows="5" maxlength="1200" placeholder="Conte materiais, técnica, origem e cuidados."><?= esc(old('descricao')) ?></textarea>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="modal-label" for="produtoPreco">Preço</label>
            <input class="input-field" type="number" id="produtoPreco" name="preco" min="0" step="0.01" required value="<?= esc(old('preco'), 'attr') ?>" placeholder="0,00" />
          </div>
          <div class="field-group">
            <label class="modal-label" for="produtoQuantidade">Quantidade</label>
            <input class="input-field" type="number" id="produtoQuantidade" name="quantidade" min="0" step="1" value="<?= esc(old('quantidade') ?? '1', 'attr') ?>" />
          </div>
        </div>

        <div class="field-row">
          <div class="field-group">
            <label class="modal-label" for="produtoCategoria">Categoria</label>
            <input class="input-field" type="text" id="produtoCategoria" name="categoria" maxlength="80" value="<?= esc(old('categoria'), 'attr') ?>" placeholder="Cerâmica, tecido, madeira..." />
          </div>
          <div class="field-group">
            <label class="modal-label" for="produtoCor">Cor do placeholder</label>
            <input class="input-field color-field" type="color" id="produtoCor" name="cor" value="<?= esc(old('cor') ?? '#b5a898', 'attr') ?>" />
          </div>
        </div>

        <div class="upload-field">
          <label class="modal-label" for="produtoImagemArquivo">Imagem do produto</label>
          <div class="upload-box">
            <i data-lucide="image-plus"></i>
            <div>
              <strong>Escolha uma imagem</strong>
              <span>PNG, JPG ou WEBP para destacar o produto.</span>
            </div>
            <input type="file" id="produtoImagemArquivo" name="imagemArquivo" accept="image/*" />
          </div>
        </div>

        <div class="field-group">
          <label class="modal-label" for="produtoImagemUrl">Ou URL pública da imagem</label>
          <input class="input-field" type="url" id="produtoImagemUrl" name="imagemUrl" placeholder="https://..." value="<?= esc(old('imagemUrl'), 'attr') ?>" />
        </div>

        <div class="create-form-actions">
          <a class="btn-status-action btn-cancelar-produto" href="<?= url_to('produtor_painel') ?>">Cancelar</a>
          <button class="btn-status-action btn-salvar-produto" type="submit">
            <i data-lucide="save"></i> Salvar produto
          </button>
        </div>
      </form>
    </section>

    <section class="create-product-preview" aria-label="Prévia do produto">
      <div class="preview-card">
        <div class="preview-image" id="produtoPreviewImage" style="background: <?= esc(old('cor') ?? '#b5a898', 'attr') ?>">
          <i data-lucide="image"></i>
        </div>
        <div class="preview-body">
          <span class="preview-category" id="produtoPreviewCategoria"><?= esc(old('categoria') ?: 'Categoria') ?></span>
          <h2 id="produtoPreviewNome"><?= esc(old('nome') ?: 'Nome do produto') ?></h2>
          <p id="produtoPreviewDescricao"><?= esc(old('descricao') ?: 'A descrição do produto aparecerá aqui enquanto você preenche o cadastro.') ?></p>
          <strong id="produtoPreviewPreco">R$ <?= number_format((float) str_replace(',', '.', (string) (old('preco') ?: 0)), 2, ',', '.') ?></strong>
        </div>
      </div>
    </section>
  </div>
</main>

<script src="<?= base_url('js/arace-state.js') ?>"></script>
<script src="<?= base_url('js/produto-form.js?v=20260701-create-product') ?>"></script>
</body>
</html>
