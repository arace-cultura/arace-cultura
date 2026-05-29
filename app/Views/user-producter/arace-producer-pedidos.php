<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Pedidos</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="../assets/style/pedidos.css" rel="stylesheet" />
</head>
<body>

  <header>
    <span class="logo">aracê</span>
    <div class="header-right">
      <button class="cart-btn"><i data-lucide="shopping-cart"></i><span class="cart-count">2 itens</span></button>
      <button class="cart-btn"><i data-lucide="heart"></i><span class="cart-count">5 itens</span></button>
      <div class="avatar-btn"><i data-lucide="user"></i></div>
    </div>
  </header>

  <aside>
    <a class="nav-item" href="../main/index.php"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="./arace-producer-painel.php"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item" href="./arace-producer-painel-produtos.php"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item active" href="./arace-producer-pedidos.php"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item" href="./arace-producer-profile-loja.php"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="./arace-producer-profile.php"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="../main/configuracoes.php"><i data-lucide="settings"></i> Configurações</a>
    <div class="nav-section">Suporte</div>
    <a class="nav-item" href="../main/configuracoes.php#pagamento"><i data-lucide="hand-coins"></i> Pagamentos</a>
  </aside>

  <main>
    <!-- conteúdo de Pedidos aqui -->
  </main>

  <script>lucide.createIcons();</script>
</body>
</html>
