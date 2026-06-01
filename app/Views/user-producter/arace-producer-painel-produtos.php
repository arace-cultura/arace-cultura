﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Painel</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="../assets/style/style-base.css" rel="stylesheet" />
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
  <a class="nav-item" href="../main/index.html"><i data-lucide="house"></i> Home page</a>
  <a class="nav-item active" href="./arace-producer-painel.html"><i data-lucide="layout-dashboard"></i> Painel</a>
  <a class="nav-item" href="./arace-producer-painel-produtos.html"><i data-lucide="shopping-bag"></i> Meus produtos</a>
  <a class="nav-item" href="./arace-producer-pedidos.html"><i data-lucide="package"></i> Pedidos</a>
  <a class="nav-item" href="./arace-producer-profile-loja.html"><i data-lucide="store"></i> Minha loja</a>
  <div class="nav-divider"></div>
  <a class="nav-item" href="./arace-producer-profile.html"><i data-lucide="user"></i> Perfil</a>
  <a class="nav-item" href="../main/configuracoes.html"><i data-lucide="settings"></i> Configurações</a>
  <div class="nav-section">Suporte</div>
  <a class="nav-item" href="../main/configuracoes.html#pagamento"><i data-lucide="hand-coins"></i> Pagamentos</a>
</aside>

<main>

  <!-- BOAS VINDAS -->
  <div class="painel-welcome">
    <div>
      <h1>Bom dia! </h1>
      <p>Aqui está um resumo da sua loja hoje — <span id="dataHoje"></span></p>
    </div>
    <a href="./arace-producer-painel-produtos.html" class="btn-novo-produto">
      <i data-lucide="plus"></i> Novo produto
    </a>
  </div>

  <!-- MÉTRICAS PRINCIPAIS -->
  <div class="metricas-grid">
    <div class="metrica-card">
      <div class="metrica-icon verde"><i data-lucide="circle-dollar-sign"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Faturamento (mês)</span>
        <span class="metrica-value">R$ 3.840,00</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +12% vs mês anterior</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon azul"><i data-lucide="package"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos (mês)</span>
        <span class="metrica-value">27</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +5 vs mês anterior</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon laranja"><i data-lucide="clock"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Pedidos pendentes</span>
        <span class="metrica-value">3</span>
        <span class="metrica-delta neutro">Aguardando ação</span>
      </div>
    </div>
    <div class="metrica-card">
      <div class="metrica-icon amarelo"><i data-lucide="star"></i></div>
      <div class="metrica-info">
        <span class="metrica-label">Avaliação média</span>
        <span class="metrica-value">4,8</span>
        <span class="metrica-delta positivo"><i data-lucide="trending-up"></i> +0,2 este mês</span>
      </div>
    </div>
  </div>

  <!-- GRID CENTRAL -->
  <div class="painel-grid">

    <!-- PEDIDOS RECENTES -->
    <div class="painel-card pedidos-recentes">
      <div class="card-head">
        <h2>Pedidos recentes</h2>
        <a href="./arace-producer-pedidos.html" class="link-ver-todos">Ver todos <i data-lucide="arrow-right"></i></a>
      </div>
      <div class="pedidos-mini-list">

        <div class="pedido-mini" data-href="./arace-producer-pedidos.html">
          <div class="pedido-mini-id">#4821</div>
          <div class="pedido-mini-info">
            <span class="pedido-mini-cliente">Ana Clara Silva</span>
            <span class="pedido-mini-produto">Kit Panela de Barro</span>
          </div>
          <span class="status-badge pendente">Pendente</span>
          <span class="pedido-mini-valor">R$200</span>
        </div>

        <div class="pedido-mini" data-href="./arace-producer-pedidos.html">
          <div class="pedido-mini-id">#4820</div>
          <div class="pedido-mini-info">
            <span class="pedido-mini-cliente">Marcos Oliveira</span>
            <span class="pedido-mini-produto">Panela Trad. M ×2</span>
          </div>
          <span class="status-badge producao">Em produção</span>
          <span class="pedido-mini-valor">R$170</span>
        </div>

        <div class="pedido-mini" data-href="./arace-producer-pedidos.html">
          <div class="pedido-mini-id">#4819</div>
          <div class="pedido-mini-info">
            <span class="pedido-mini-cliente">Fernanda Costa</span>
            <span class="pedido-mini-produto">Prato de Cerâmica ×3</span>
          </div>
          <span class="status-badge enviado">Enviado</span>
          <span class="pedido-mini-valor">R$135</span>
        </div>

        <div class="pedido-mini" data-href="./arace-producer-pedidos.html">
          <div class="pedido-mini-id">#4818</div>
          <div class="pedido-mini-info">
            <span class="pedido-mini-cliente">João Pedro Matos</span>
            <span class="pedido-mini-produto">Kit Panela de Barro</span>
          </div>
          <span class="status-badge entregue">Entregue</span>
          <span class="pedido-mini-valor">R$200</span>
        </div>

        <div class="pedido-mini" data-href="./arace-producer-pedidos.html">
          <div class="pedido-mini-id">#4817</div>
          <div class="pedido-mini-info">
            <span class="pedido-mini-cliente">Luciana Ferreira</span>
            <span class="pedido-mini-produto">Vaso Artesanal Grande</span>
          </div>
          <span class="status-badge pendente">Pendente</span>
          <span class="pedido-mini-valor">R$95</span>
        </div>

      </div>
    </div>

    <!-- COLUNA DIREITA -->
    <div class="painel-col-right">

      <!-- PRODUTOS DA LOJA -->
      <div class="painel-card">
        <div class="card-head">
          <h2>Meus produtos</h2>
          <a href="./arace-producer-painel-produtos.html" class="link-ver-todos">Gerenciar <i data-lucide="arrow-right"></i></a>
        </div>
        <div class="produtos-mini-list">

          <div class="produto-mini">
            <div class="produto-mini-img" style="background:#b5a898"></div>
            <div class="produto-mini-info">
              <span class="produto-mini-nome">Kit Panela de Barro</span>
              <span class="produto-mini-preco">R$ 200,00</span>
            </div>
            <span class="stock-badge">Em estoque</span>
            <div class="produto-mini-actions">
              <button type="button" data-href="./arace-producer-painel-produtos.html" title="Editar"><i data-lucide="pencil"></i></button>
              <button class="del" title="Excluir"><i data-lucide="trash-2"></i></button>
            </div>
          </div>

          <div class="produto-mini">
            <div class="produto-mini-img" style="background:#a09880"></div>
            <div class="produto-mini-info">
              <span class="produto-mini-nome">Panela Tradicional M</span>
              <span class="produto-mini-preco">R$ 85,00</span>
            </div>
            <span class="stock-badge">Em estoque</span>
            <div class="produto-mini-actions">
              <button type="button" data-href="./arace-producer-painel-produtos.html" title="Editar"><i data-lucide="pencil"></i></button>
              <button class="del" title="Excluir"><i data-lucide="trash-2"></i></button>
            </div>
          </div>

          <div class="produto-mini">
            <div class="produto-mini-img" style="background:#c4b49a"></div>
            <div class="produto-mini-info">
              <span class="produto-mini-nome">Prato de Cerâmica</span>
              <span class="produto-mini-preco">R$ 45,00</span>
            </div>
            <span class="stock-badge esgotado">Esgotado</span>
            <div class="produto-mini-actions">
              <button type="button" data-href="./arace-producer-painel-produtos.html" title="Editar"><i data-lucide="pencil"></i></button>
              <button class="del" title="Excluir"><i data-lucide="trash-2"></i></button>
            </div>
          </div>

        </div>

        <a href="./arace-producer-painel-produtos.html" class="btn-add-produto">
          <i data-lucide="plus"></i> Adicionar produto
        </a>
      </div>

      <!-- AVALIAÇÕES RECENTES -->
      <div class="painel-card">
        <div class="card-head">
          <h2>Avaliações recentes</h2>
        </div>
        <div class="avaliacoes-list">

          <div class="avaliacao-mini">
            <div class="avaliacao-header">
              <span class="avaliacao-user">Ana Clara Silva</span>
              <div class="mini-stars">
                <i data-lucide="star"></i><i data-lucide="star"></i><i data-lucide="star"></i>
                <i data-lucide="star"></i><i data-lucide="star"></i>
              </div>
            </div>
            <p class="avaliacao-text">Panela maravilhosa! Já fiz moqueca duas vezes, ficou perfeita.</p>
            <span class="avaliacao-produto">Kit Panela de Barro · há 2 dias</span>
          </div>

          <div class="avaliacao-mini">
            <div class="avaliacao-header">
              <span class="avaliacao-user">João Pedro Matos</span>
              <div class="mini-stars">
                <i data-lucide="star"></i><i data-lucide="star"></i><i data-lucide="star"></i>
                <i data-lucide="star"></i><i data-lucide="star" style="opacity:.3"></i>
              </div>
            </div>
            <p class="avaliacao-text">Produto de excelente qualidade, bem embalado. Recomendo!</p>
            <span class="avaliacao-produto">Panela Tradicional M · há 5 dias</span>
          </div>

        </div>
      </div>

    </div>
  </div>

</main>

<script src="../assets/js/arace-state.js"></script>
<script src="../assets/js/producer-painel-produtos.js"></script>
</body>
</html>