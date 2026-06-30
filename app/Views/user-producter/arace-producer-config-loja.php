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
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/config.css') ?>" rel="stylesheet" />
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
      <span class="cart-count">2 itens</span>
    </button>
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_favoritos') ?>'">
      <i data-lucide="heart"></i>
      <span class="cart-count">5 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'">
      <i data-lucide="user"></i>
    </button>
  </div>
</header>

<!--Icone de chat-->
<div class="chat-bubble">
  <a href="<?= url_to('user_chat') ?>">
    <i data-lucide="message-circle-more"></i>
  </a>
</div>

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
    <a class="nav-item" href="<?= url_to('auth_cadastro_produtor') ?>">
      <i data-lucide="box"></i> Quero ser produtor
    </a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>#pagamento">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
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
        <button class="config-nav-item" onclick="trocarAba(this,'logistica')">
          <i data-lucide="truck"></i> Frete & Retirada
        </button>
        <button class="config-nav-item" onclick="trocarAba(this,'financeiro')">
          <i data-lucide="landmark"></i> Conta Bancária
        </button>
        <div class="config-nav-divider"></div>
        <button class="config-nav-item" onclick="trocarAba(this,'horarios')">
          <i data-lucide="clock"></i> Horários de Funcionamento
        </button>
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
          </div>

          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Apresentação</h2><p>Como os clientes visualizam seu negócio</p></div>
            </div>
            <div class="config-card-body">
              <div class="field-group">
                <label>Nome Comercial da Loja</label>
                <input class="input-field" type="text" id="lojaNome" value="Paneleiras Capixabas" />
              </div>
              <div class="field-group">
                <label>História / Biografia da Loja</label>
                <textarea class="input-field" id="lojaBio" rows="4" style="resize:vertical;line-height:1.5">Preservamos uma tradição centenária de produção artesanal de panelas de barro, símbolo da cultura capixaba. Cada peça carrega a identidade, o suor e o amor passado de geração em geração.</textarea>
              </div>
            </div>
          </div>
        </section>

        <section class="config-section" id="sec-dados-comerciais">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Documentação Jurídica</h2><p>Informações de registro da loja</p></div>
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
          </div>
        </section>

        <section class="config-section" id="sec-logistica">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Métodos de Entrega Ativos</h2><p>Configure como seus produtos chegam aos clientes</p></div>
            </div>
            <div class="config-card-body">
              <div class="toggle-row">
                <div class="toggle-info"><span>Permitir Retirada no Local</span><small>Os clientes buscam no endereço da sua oficina/loja</small></div>
                <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
              </div>
              <div class="toggle-row">
                <div class="toggle-info"><span>Envio via Correios (PAC/Sedex)</span><small>Cálculo de peso baseado na tabela oficial dos Correios</small></div>
                <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
              </div>
              <div class="toggle-row">
                <div class="toggle-info"><span>Entrega Local / Motoboy</span><small>Taxa fixa para distritos vizinhos ou mesma cidade</small></div>
                <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
              </div>
            </div>
          </div>

          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Endereço de Postagem / Origem</h2></div>
            </div>
            <div class="config-card-body">
              <div class="field-row">
                <div class="field-group">
                  <label>CEP de Origem</label>
                  <input class="input-field" type="text" value="29023-010" />
                </div>
                <div class="field-group">
                  <label>Distrito / Município</label>
                  <input class="input-field" type="text" id="lojaCidade" value="Vitória - ES" readonly style="background:var(--bg)" />
                </div>
              </div>
              <div class="field-group">
                <label>Endereço Completo da Oficina</label>
                <input class="input-field" type="text" value="Rua das Paneleiras, Nº 50" />
              </div>
            </div>
          </div>
        </section>

        <section class="config-section" id="sec-financeiro">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Conta para Repasse</h2><p>Onde você receberá o saldo das vendas realizadas na Aracê</p></div>
            </div>
            <div class="config-card-body">
              <div class="field-row">
                <div class="field-group">
                  <label>Banco</label>
                  <select class="input-field">
                    <option value="banestes">Banestes — 021</option>
                    <option value="brasil">Banco do Brasil — 001</option>
                    <option value="caixa">Caixa Econômica — 104</option>
                    <option value="nubank">Nu Pagamentos — 260</option>
                  </select>
                </div>
                <div class="field-group">
                  <label>Tipo de Conta</label>
                  <select class="input-field">
                    <option value="cc">Conta Corrente</option>
                    <option value="cp">Conta Poupança</option>
                  </select>
                </div>
              </div>
              <div class="field-row">
                <div class="field-group" style="flex:2">
                  <label>Agência</label>
                  <input class="input-field" type="text" placeholder="0000" />
                </div>
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
          </div>
        </section>

        <section class="config-section" id="sec-horarios">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Horário de Funcionamento</h2><p>Períodos em que a loja responde ao chat ou aceita retiradas</p></div>
            </div>
            <div class="config-card-body">
              <div class="field-row" style="align-items: center; margin-bottom: 12px;">
                <div style="width: 120px; font-weight: 500;">Segunda a Sexta</div>
                <input class="input-field" type="time" value="08:00" style="max-width: 100px;" />
                <span>às</span>
                <input class="input-field" type="time" value="18:00" style="max-width: 100px;" />
              </div>
              <div class="field-row" style="align-items: center; margin-bottom: 12px;">
                <div style="width: 120px; font-weight: 500;">Sábados</div>
                <input class="input-field" type="time" value="08:00" style="max-width: 100px;" />
                <span>às</span>
                <input class="input-field" type="time" value="12:00" style="max-width: 100px;" />
              </div>
              <div class="field-row" style="align-items: center;">
                <div style="width: 120px; font-weight: 500;">Domingos</div>
                <span style="color:var(--muted); font-size:14px; font-style:italic;">Fechado</span>
              </div>
            </div>
          </div>
        </section>

        <div class="toast" id="toast">
          <i data-lucide="check-circle"></i>
          <span id="toastMsg">Salvo com sucesso</span>
        </div>

        <script src="/js/arace-state.js"></script>
        <script src="/js/brasil-api-validacao.js?v=20260630-fix"></script>
        <script src="/js/config.js?v=20260630-fix"></script>

      </div></div></main>

</body>
</html>
