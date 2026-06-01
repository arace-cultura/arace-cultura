﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Painel de Produtos</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/loja.css" rel="stylesheet" />
  <link href="/css/painel-produtos.css" rel="stylesheet" />
</head>
<body class="bg-topografia">

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
    <a class="nav-item" href="./arace-producer-painel.html"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item active" href="./arace-producer-painel-produtos.html"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item" href="./arace-producer-pedidos.html"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item" href="./arace-producer-profile-loja.html"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="./arace-producer-profile.html"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="../main/configuracoes.html"><i data-lucide="settings"></i> Configurações</a>
    <div class="nav-section">Suporte</div>
    <a class="nav-item" href="../main/configuracoes.html#pagamento"><i data-lucide="hand-coins"></i> Pagamentos</a>
  </aside>

  <main>
    <div class="produtos-page-container">
      
      <section class="produtos-lista-section">
        <div class="panel-header">
          <div>
            <h1 class="titulo-serif">Meus Produtos</h1>
            <p>Gerencie o estoque, valores e detalhes da sua vitrine</p>
          </div>
          <button class="btn-primary mobile-only-btn" onclick="focarFormulario()">
            <i data-lucide="plus"></i> Novo Produto
          </button>
        </div>

        <div class="produtos-management-grid">
          
          <div class="produto-manage-card">
            <div class="card-img-wrapper">
              <img src="../assets/imgs/kit-panela.jpg" alt="Kit Panela de Barro" />
              <div class="card-actions-overlay">
                <button class="action-btn edit" onclick="editarProduto(1)" title="Editar Produto">
                  <i data-lucide="pencil"></i>
                </button>
                <button class="action-btn delete" onclick="excluirProduto(1)" title="Excluir Produto">
                  <i data-lucide="trash-2"></i>
                </button>
              </div>
            </div>
            <div class="card-manage-info">
              <h3>Kit Panela de barro</h3>
              <div class="price-stock-row">
                <span class="price">R$ 200,00</span>
                <span class="stock-badge">Em estoque</span>
              </div>
            </div>
          </div>

          <div class="produto-manage-card">
            <div class="card-img-wrapper">
              <img src="../assets/imgs/kit-panela.jpg" alt="Panela de Barro Individual" />
              <div class="card-actions-overlay">
                <button class="action-btn edit" onclick="editarProduto(2)" title="Editar Produto">
                  <i data-lucide="pencil"></i>
                </button>
                <button class="action-btn delete" onclick="excluirProduto(2)" title="Excluir Produto">
                  <i data-lucide="trash-2"></i>
                </button>
              </div>
            </div>
            <div class="card-manage-info">
              <h3>Panela de Barro Tradicional M</h3>
              <div class="price-stock-row">
                <span class="price">R$ 85,00</span>
                <span class="stock-badge">Em estoque</span>
              </div>
            </div>
          </div>

        </div>
      </section>

      <aside class="produtos-form-sidebar" id="moduloFormulario">
        <div class="form-card-box">
          <h2 class="titulo-serif" id="formTitulo">Adicionar produto</h2>
          
          <form id="productForm" onsubmit="salvarProduto(event)">
            
            <div class="image-upload-container">
              <div class="image-upload-placeholder">
                <i data-lucide="image" class="placeholder-icon"></i>
                <button type="button" class="btn-choose-img">escolher imagem</button>
                <input type="file" accept="image/*" id="produtoImagemInput" style="display: none;" />
              </div>
            </div>

            <div class="field-group-terracota">
              <label for="prodNome">Nome</label>
              <input type="text" id="prodNome" required placeholder="Ex: Kit Panela de Barro" />
            </div>

            <div class="field-group-terracota">
              <label for="prodPreco">Preço (R$)</label>
              <input type="number" step="0.01" id="prodPreco" required placeholder="0,00" />
            </div>

            <div class="field-group-terracota description-box">
              <label for="prodDescricao">Descrição</label>
              <textarea id="prodDescricao" rows="4" placeholder="Descreva os detalhes da sua peça artesanal..."></textarea>
            </div>

            <div class="field-group-terracota">
              <label for="prodOpcional">Descrição adicional (opcional)</label>
              <input type="text" id="prodOpcional" placeholder="Ex: Dimensões, peso ou cuidados de cura" />
            </div>

            <button type="submit" class="btn-terracota-submit" id="btnSubmitForm">
              Adicionar
            </button>
            
            <button type="button" class="btn-cancel-edit" id="btnCancelEdit" onclick="resetarFormulario()" style="display: none;">
              Cancelar Edição
            </button>

          </form>
        </div>
      </aside>

    </div>
  </main>

  <script>
    lucide.createIcons();

    // Lógicas dinâmicas básicas de mock-up para simular o comportamento
    function editarProduto(id) {
      document.getElementById('formTitulo').innerText = "Alterar produto";
      document.getElementById('btnSubmitForm').innerText = "Salvar Alterações";
      document.getElementById('btnCancelEdit').style.display = "block";
      
      // Simulação de preenchimento dos campos para alteração
      if(id === 1) {
        document.getElementById('prodNome').value = "Kit Panela de barro";
        document.getElementById('prodPreco').value = "200.00";
        document.getElementById('prodDescricao').value = "Preservamos uma tradição centenária de produção artesanal.";
      }
      focarFormulario();
    }

    function resetarFormulario() {
      document.getElementById('productForm').reset();
      document.getElementById('formTitulo').innerText = "Adicionar produto";
      document.getElementById('btnSubmitForm').innerText = "Adicionar";
      document.getElementById('btnCancelEdit').style.display = "none";
    }

    function excluirProduto(id) {
      if(confirm("Tem certeza que deseja remover este produto da sua loja?")) {
        alert("Produto removido com sucesso!");
      }
    }

    function salvarProduto(e) {
      e.preventDefault();
      alert("Ação processada com sucesso no banco de dados!");
      resetarFormulario();
    }

    function focarFormulario() {
      document.getElementById('moduloFormulario').scrollIntoView({ behavior: 'smooth' });
      document.getElementById('prodNome').focus();
    }
  </script>
</body>
</html>
