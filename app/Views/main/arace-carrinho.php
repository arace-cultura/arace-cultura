<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê — Carrinho</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=DM+Serif+Display&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link rel="stylesheet" href="<?= base_url('css/carrinho.css') ?>" />
</head>

<body>

  <!-- HEADER -->
  <header>
    <a href="<?= url_to('home') ?>" class="logo">aracê</a>
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
        <i data-lucide="user"></i>
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
    <a class="nav-item" href="<?= url_to('user_arace_notificacao') ?>">
      <i data-lucide="bell"></i> Notificações
    </a>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    <a class="nav-item" href="<?= url_to('auth_cadastro_producer_arace') ?>">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>

  <!-- MAIN -->
  <main>

    <div class="page-header">
      <h1 class="page-title">Seu carrinho</h1>
      <a href="<?= url_to('arace_produtos') ?>" class="btn-keep-shopping">
        <i data-lucide="arrow-left"></i> Continuar comprando
      </a>
    </div>

    <!-- Carrinho vazio (oculto por padrão) -->
    <div class="empty-cart hidden" id="emptyCart">
      <div class="empty-icon">
        <i data-lucide="shopping-bag"></i>
      </div>
      <h2>Seu carrinho está vazio</h2>
      <p>Explore nossa coleção e encontre algo especial para você.</p>
      <a href="<?= url_to('arace_produtos') ?>" class="btn-primary-arace">Ver produtos</a>
    </div>

    <!-- Layout principal: itens + resumo -->
    <div class="cart-layout" id="cartContent">

      <!-- Coluna esquerda -->
      <div class="cart-col-items">

        <div class="cart-table-header">
          <span>Produto</span>
          <span>Quantidade</span>
          <span>Subtotal</span>
        </div>

        <!-- Item 1 -->
        <div class="cart-item item-animado atraso-1" data-item-id="1">
          <div class="cart-item-image">
            <div class="img-placeholder" style="background:#C1734A"></div>
          </div>
          <div class="cart-item-info">
            <a href="<?= url_to('main_produto_detalhes') ?>" class="cart-item-name">Item</a>
            <span class="cart-item-brand">Espírito Das Pedras</span>
            <span class="cart-item-sku">Cód: 1001</span>
            <div class="cart-item-variants">
              <span class="variant-tag">Tamanho: M</span>
              <span class="variant-tag">Cor: Terracota</span>
            </div>
            <span class="cart-item-unit-price">R$245,00 / un.</span>
          </div>
          <div class="qty-control">
            <button class="qty-btn qty-minus" data-item-id="1">
              <i data-lucide="minus"></i>
            </button>
            <input class="qty-input" type="number" value="1" min="1" max="99" id="qty-1" />
            <button class="qty-btn qty-plus" data-item-id="1">
              <i data-lucide="plus"></i>
            </button>
          </div>
          <div class="cart-item-subtotal">
            <span class="subtotal-value" id="subtotal-1">R$245,00</span>
            <button class="btn-remove-icon" data-item-id="1">
              <i data-lucide="trash-2"></i>
            </button>
          </div>
        </div>

        <!-- Item 2 -->
        <div class="cart-item item-animado atraso-1" data-item-id="2">
          <div class="cart-item-image">
            <div class="img-placeholder" style="background:#C1734A; opacity:.75"></div>
          </div>
          <div class="cart-item-info">
            <a href="<?= url_to('main_produto_detalhes') ?>" class="cart-item-name">Item</a>
            <span class="cart-item-brand">Sintético</span>
            <span class="cart-item-sku">Cód: 1002</span>
            <div class="cart-item-variants">
              <span class="variant-tag">Tamanho: G</span>
            </div>
            <span class="cart-item-unit-price">R$180,00 / un.</span>
          </div>
          <div class="qty-control">
            <button class="qty-btn qty-minus" data-item-id="2">
              <i data-lucide="minus"></i>
            </button>
            <input class="qty-input" type="number" value="1" min="1" max="99" id="qty-2" />
            <button class="qty-btn qty-plus" data-item-id="2">
              <i data-lucide="plus"></i>
            </button>
          </div>
          <div class="cart-item-subtotal">
            <span class="subtotal-value" id="subtotal-2">R$180,00</span>
            <button class="btn-remove-icon" data-item-id="2">
              <i data-lucide="trash-2"></i>
            </button>
          </div>
        </div>

      

      <!-- Coluna direita: resumo -->
      <div class="cart-col-summary item-animado atraso-3">
        <div class="order-summary">
          <h5 class="summary-title">Detalhes do pedido</h5>

          <div class="summary-row">
            <span>Subtotal</span>
            <span id="summarySubtotal">R$425,00</span>
          </div>
          <div class="summary-row discount">
            <span>Desconto</span>
            <span id="summaryDiscount">-R$30,00</span>
          </div>
          <div class="summary-row">
            <span>Frete</span>
            <span id="summaryShipping">R$0,00</span>
          </div>

          <div class="coupon-area">
            <div class="shipping-row">
              <input type="text" class="coupon-input" placeholder="Código promocional" />
              <button class="btn-outline-arace">Aplicar</button>
            </div>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-row total">
            <span>Total</span>
            <span id="summaryTotal">R$395,00</span>
          </div>

          <button class="btn-checkout" id="checkoutBtn">
            Finalizar compra
            <i data-lucide="arrow-right"></i>
          </button>

          <div class="security-badges">
            <span><i data-lucide="shield-check"></i> Compra segura</span>
            <span><i data-lucide="rotate-ccw"></i> Troca fácil</span>
            <span><i data-lucide="lock"></i> SSL</span>
          </div>

          <div class="payment-methods">
            <img src="https://cdn.jsdelivr.net/npm/payment-icons@1.1.0/min/flat/visa.svg" alt="Visa" />
            <img src="https://cdn.jsdelivr.net/npm/payment-icons@1.1.0/min/flat/mastercard.svg" alt="Mastercard" />
            <span class="pix-badge"><i data-lucide="qr-code"></i> Pix</span>
          </div>
        </div>
      </div>

    </div><!-- /cart-layout -->

  </main>

  <script>
    lucide.createIcons();
  </script>
  <script src="/js/arace-carrinho.js"></script>

</body>
</html>
