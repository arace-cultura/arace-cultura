<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Configuração da Loja</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <link href="/css/config.css" rel="stylesheet" />
</head>
<body>

  <!-- HEADER -->
<header>
  <a href="/" class="logo">aracê</a>

  <form class="search-wrap" action="/pesquisa" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='/arace-carrinho'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='/usuario/arace-favoritos'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='/usuario/arace-perfil'">
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
    <a class="nav-item" href="/cadastro/produtor">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    
  </aside>

  <main>
    <div class="config-header">
      <div>
        <h1>Configurações da Loja</h1>
        <p>Gerencie a identidade, dados comerciais e logística do seu negócio</p>
      </div>
    </div>

    <div class="config-layout">

      <nav class="config-nav">
        <button class="config-nav-item active" onclick="trocarAba(this,'identidade')">
          <i data-lucide="store"></i> Identidade Visual
        </button>
        <button class="config-nav-item" onclick="trocarAba(this,'dados-comerciais')">
          <i data-lucide="briefcase"></i> Dados Comerciais
        </button>
        <button class="config-nav-item" onclick="trocarAba(this,'financeiro')">
          <i data-lucide="landmark"></i> pix
        </button>
        <div class="config-nav-divider"></div>
        
      </nav>

      <div>

        <section class="config-section active" id="sec-identidade">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Logotipo e Capa da Loja</h2><p>Imagens que aparecem na sua vitrine pública</p></div>
            </div>
            <div class="config-card-body">
              <label style="display:block;margin-bottom:8px;font-size:13px;font-weight:500;color:var(--text)">Logotipo da Loja</summary>
              <div class="avatar-upload-area" style="margin-bottom:1.5rem">
                <div class="avatar-preview" id="avatarPreview">
                  <i data-lucide="store"></i>
                </div>
                <div class="avatar-upload-btns">
                  <label for="avatarInput" class="btn-primary" style="cursor:pointer">
                    <i data-lucide="upload"></i> Enviar Logo
                  </label>
                  <input type="file" id="avatarInput" accept="image/*" style="display:none" onchange="previewAvatar(this)" />
                  <button class="btn-secondary" onclick="removerAvatar()">Remover</button>
                </div>
              </div>

              <div class="field-group">
                <label>Imagem de Capa (Banner)</label>
                <div style="border:2px dashed var(--border);border-radius:var(--r);padding:2rem;text-align:center;background:var(--branco)">
                  <i data-lucide="image" style="width:32px;height:32px;color:var(--muted);margin-bottom:8px"></i>
                  <div style="font-size:14px;color:var(--text)">Arraste ou clique para enviar um banner panorâmico</div>
                  <small style="display:block;margin-top:4px;color:var(--faint)">Recomendado: 1200x300px</small>
                </div>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" onclick="salvar('Mídias da loja atualizadas')"><i data-lucide="check"></i> Salvar Identidade</button>
            </div>
          </div>

          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Apresentação</h2></div>
            </div>
            <div class="config-card-body">
              <div class="field-group">
                <label>Nome Comercial da Loja</label>
                <input class="input-field" type="text" id="lojaNome" value="Paneleiras Capixabas" />
              </div>
              <div class="field-group">
                <label>História / Biografia da Loja</label>
                <textarea class="input-field" id="lojaBio" rows="4" style="resize:vertical;line-height:1.5">Descrição</textarea>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" onclick="salvar('Apresentação da loja salva')"><i data-lucide="check"></i> Salvar Alterações</button>
            </div>
          </div>
        </section>

        <section class="config-section" id="sec-dados-comerciais">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Documentação Jurídica</h2></div>
            </div>
            <div class="config-card-body">
              <div class="field-row">
                <div class="field-group">
                  <label>CNPJ (Se houver)</label>
                  <input class="input-field" type="text" placeholder="00.000.000/0001-00" />
                </div>
                <div class="field-group">
                  <label>Categoria Principal</label>
                  <select class="input-field" id="lojaCategoria">
                    <option value="ceramica" selected>Cerâmica & Panelas de Barro</option>
                    <option value="artesanato">Artesanato Geral</option>
                    <option value="alimentos">Doces & Alimentos Caseiros</option>
                  </select>
                </div>
              </div>
              <div class="field-row">
                <div class="field-group">
                  <label>E-mail de Atendimento Comercial</label>
                  <input class="input-field" type="email" id="lojaEmail" value="contato@paneleiras.com" />
                </div>
                <div class="field-group">
                  <label>WhatsApp / Telefone da Loja</label>
                  <input class="input-field" type="tel" id="lojaTelefone" value="(27) 99999-1234" />
                </div>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" onclick="salvar('Dados comerciais salvos')"><i data-lucide="check"></i> Salvar Dados</button>
            </div>
          </div>
        </section>


        <section class="config-section" id="sec-financeiro">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Conta para Repasse</h2></p></div>
            </div>
            <div class="config-card-body">
              <div class="field-row">
                <div class="field-group">
                  <label>Banco</label>
                  <select class="input-field">
                    <option value="nubank">Pix </option>
                  </select>
                </div>
                
              </div>
              <div class="field-row">
                
                <div class="field-group" style="flex:3">
                  <label>Número da Conta</label>
                  <input class="input-field" type="text" placeholder="000000-0" />
                </div>
              </div>
              <div class="field-group">
                <label>Chave Pix de Backup</label>
                <input class="input-field" type="text" placeholder="CNPJ, CPF ou e-mail" />
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" onclick="salvar('Dados bancários atualizados')"><i data-lucide="check"></i> Salvar Conta Bancária</button>
            </div>
          </div>
        </section>


        <div class="toast" id="toast">
          <i data-lucide="check-circle"></i>
          <span id="toastMsg">Salvo com sucesso</span>
        </div>

        <script src="/js/arace-state.js"></script>
        <script src="/js/config.js"></script>

      </div></div></main>

</body>
</html>