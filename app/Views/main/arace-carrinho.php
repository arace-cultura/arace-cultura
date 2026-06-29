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
  <link rel="stylesheet" href="/css/carrinho.css" />
</head>

<body>

  <!-- HEADER -->
  <header>
    <a href="/" class="logo">aracê</a>
    <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='/arace-carrinho'">
        <i data-lucide="shopping-cart"></i>
        <span class="cart-count">2 itens</span>
      </button>
    <button class="cart-btn" type="button" onclick="window.location.href='/usuario/arace-favoritos'">
        <i data-lucide="heart"></i>
        <span class="cart-count">5 itens</span>
      </button>
      <button class="avatar-btn" type="button" onclick="window.location.href='/usuario/arace-perfil'" aria-label="Abrir perfil">
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
    <a class="nav-item" href="/cadastro/produtor-arace">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    
  </aside>

  <!-- MAIN -->
  <main>

    <div class="page-header">
      <h1 class="page-title">Seu carrinho</h1>
      <a href="/arace-produtos" class="btn-keep-shopping">
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
      <a href="/arace-produtos" class="btn-primary-arace">Ver produtos</a>
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
            <span id="summaryDiscount">−R$30,00</span>
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
