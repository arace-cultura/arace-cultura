<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$isProdutor = in_array($usuario['isProdutor'] ?? false, [true, 1, '1', 'true'], true);
$carrinho = $carrinho ?? [];
$totais = $totais ?? ['subtotal' => 0, 'desconto' => 0, 'frete' => 0, 'total' => 0];
$formatarMoeda = static fn (float $valor): string => 'R$' . number_format($valor, 2, ',', '.');
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Arace - Carrinho</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=DM+Serif+Display&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link rel="stylesheet" href="<?= base_url('css/carrinho.css') ?>" />
</head>

<body>
  <header>
    <a href="<?= url_to('home') ?>" class="logo">arace</a>
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
      <i data-lucide="settings"></i> Configuracoes
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    <?php if (! $isProdutor): ?>
      <a class="nav-item" href="<?= url_to('auth_cadastro_producer_arace') ?>">
        <i data-lucide="box"></i> Quero ser produtor
      </a>
    <?php endif; ?>
  </aside>

  <main>
    <div class="page-header">
      <h1 class="page-title">Seu carrinho</h1>
      <a href="<?= url_to('arace_produtos') ?>" class="btn-keep-shopping">
        <i data-lucide="arrow-left"></i> Continuar comprando
      </a>
    </div>

    <div class="empty-cart <?= $carrinho === [] ? '' : 'hidden' ?>" id="emptyCart">
      <div class="empty-icon">
        <i data-lucide="shopping-bag"></i>
      </div>
      <h2>Seu carrinho esta vazio</h2>
      <p>Explore nossa colecao e encontre algo especial para voce.</p>
      <button
        type="button"
        class="btn-primary-arace"
        data-products-url="<?= url_to('arace_produtos') ?>"
      >
        Ir para produtos
      </button>
    </div>

    <div class="cart-layout <?= $carrinho === [] ? 'hidden' : '' ?>" id="cartContent">
      <div class="cart-col-items">
        <div class="cart-table-header">
          <span>Produto</span>
          <span>Quantidade</span>
          <span>Subtotal</span>
        </div>

        <?php foreach ($carrinho as $item): ?>
          <?php
            $produto = $item['produto'] ?? [];
            $produtoId = (string) ($item['produtoId'] ?? $produto['id'] ?? '');
            $quantidade = (int) ($item['quantidade'] ?? 1);
            $imagem = (string) ($produto['img'] ?? $produto['imagem'] ?? '');
          ?>
          <div class="cart-item item-animado atraso-1" data-item-id="<?= esc($produtoId, 'attr') ?>">
            <div class="cart-item-image">
              <?php if ($imagem !== ''): ?>
                <img src="<?= esc($imagem, 'attr') ?>" alt="<?= esc($produto['nome'] ?? 'Produto') ?>" />
              <?php else: ?>
                <div class="img-placeholder" style="background:<?= esc($produto['cor'] ?? '#C1734A', 'attr') ?>"></div>
              <?php endif; ?>
            </div>
            <div class="cart-item-info">
              <a href="<?= url_to('main_produto', $produtoId) ?>" class="cart-item-name"><?= esc($produto['nome'] ?? 'Produto Arace') ?></a>
              <span class="cart-item-brand"><?= esc($produto['artesao'] ?? 'Produtor Arace') ?></span>
              <span class="cart-item-sku">Cod: <?= esc($produtoId) ?></span>
              <div class="cart-item-variants">
                <span class="variant-tag"><?= esc($produto['categoria'] ?? 'artesanato') ?></span>
              </div>
              <span class="cart-item-unit-price"><?= $formatarMoeda((float) ($produto['preco'] ?? 0)) ?> / un.</span>
            </div>
            <div class="qty-control">
              <button class="qty-btn qty-minus" data-item-id="<?= esc($produtoId, 'attr') ?>">
                <i data-lucide="minus"></i>
              </button>
              <input class="qty-input" type="number" value="<?= $quantidade ?>" min="1" max="99" id="qty-<?= esc($produtoId, 'attr') ?>" />
              <button class="qty-btn qty-plus" data-item-id="<?= esc($produtoId, 'attr') ?>">
                <i data-lucide="plus"></i>
              </button>
            </div>
            <div class="cart-item-subtotal">
              <span class="subtotal-value" id="subtotal-<?= esc($produtoId, 'attr') ?>"><?= $formatarMoeda((float) ($item['subtotal'] ?? 0)) ?></span>
              <button class="btn-remove-icon" data-item-id="<?= esc($produtoId, 'attr') ?>">
                <i data-lucide="trash-2"></i>
              </button>
            </div>
          </div>
        <?php endforeach; ?>
      </div>

      <div class="cart-col-summary item-animado atraso-3">
        <div class="order-summary">
          <h5 class="summary-title">Detalhes do pedido</h5>

          <div class="summary-row">
            <span>Subtotal</span>
            <span id="summarySubtotal"><?= $formatarMoeda((float) ($totais['subtotal'] ?? 0)) ?></span>
          </div>
      


          <div class="summary-divider"></div>

          <div class="summary-row total">
            <span>Total</span>
            <span id="summaryTotal"><?= $formatarMoeda((float) ($totais['total'] ?? 0)) ?></span>
          </div>

          <button class="btn-checkout" id="checkoutBtn">
            Finalizar compra
            <i data-lucide="arrow-right"></i>
          </button>

          <div class="security-badges">
            <span><i data-lucide="shield-check"></i> Compra segura</span>
            <span><i data-lucide="rotate-ccw"></i> Troca facil</span>
            <span><i data-lucide="lock"></i> SSL</span>
          </div>

          <div class="payment-methods">
            <img src="https://cdn.jsdelivr.net/npm/payment-icons@1.1.0/min/flat/visa.svg" alt="Visa" />
            <img src="https://cdn.jsdelivr.net/npm/payment-icons@1.1.0/min/flat/mastercard.svg" alt="Mastercard" />
            <span class="pix-badge"><i data-lucide="qr-code"></i> Pix</span>
          </div>
        </div>
      </div>
    </div>
  </main>

  <script>
    window.ARACE_CART = <?= json_encode(['items' => $carrinho, 'totais' => $totais], JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
    lucide.createIcons();
  </script>
  <script src="<?= base_url('js/arace-state.js') ?>"></script>
  <script src="<?= base_url('js/arace-carrinho.js') ?>"></script>
</body>
</html>
