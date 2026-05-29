<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Chat</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="../assets/style/chat.css" rel="stylesheet" />
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
    <a class="nav-item" href="../main/produtos.php"><i data-lucide="shopping-bag"></i> Produtos</a>
    <a class="nav-item" href="../main/carrinho.php"><i data-lucide="shopping-cart"></i> Carrinho</a>
    <a class="nav-item" href="./notificacoes.php"><i data-lucide="bell"></i> Notificações</a>
    <a class="nav-item" href="../main/configuracoes.php"><i data-lucide="settings"></i> Configurações</a>
    <a class="nav-item" href="./perfil.php"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="../authentication/cadastro-produtor.php"><i data-lucide="box"></i> Quero ser produtor</a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item active" href="arace-chat.php"><i data-lucide="message-circle"></i> Chat</a>
  </aside>

  <main>
    <!-- conteúdo do chat aqui -->
  </main>

  <script>lucide.createIcons();</script>
</body>
</html>
