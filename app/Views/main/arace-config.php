﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Configurações</title>
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
    <a class="nav-item" href="/usuario/arace-notificacao">
      <i data-lucide="bell"></i> Notificações
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
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="/arace-config">
      <i data-lucide="hand-coins"></i> Detalhes de pagamento
    </a>
  </aside>

<main>
  <div class="config-header">
    <div>
      <h1>Configurações</h1>
      <p>Gerencie suas preferências e dados da conta</p>
    </div>
  </div>

  <div class="config-layout">

    <!-- NAV LATERAL -->
    <nav class="config-nav">
      <button class="config-nav-item active" onclick="trocarAba(this,'perfil')">
        <i data-lucide="user"></i> Perfil
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'conta')">
        <i data-lucide="shield"></i> Conta
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'notificacoes')">
        <i data-lucide="bell"></i> Notificações
      </button>
      
      <button class="config-nav-item" onclick="trocarAba(this,'enderecos')">
        <i data-lucide="map-pin"></i> Endereços
      </button>
      <div class="config-nav-divider"></div>
      
    </nav>

    <!-- CONTEÚDO -->
    <div>

      <!-- ── PERFIL ── -->
      <section class="config-section active" id="sec-perfil">

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Foto de perfil</h2></div>
          </div>
          <div class="config-card-body">
            <div class="avatar-upload-area">
              <div class="avatar-preview" id="avatarPreview">
                <i data-lucide="user"></i>
              </div>
              <div class="avatar-upload-btns">
                <label for="avatarInput" class="btn-primary" style="cursor:pointer">
                  <i data-lucide="upload"></i> Alterar foto
                </label>
                <input type="file" id="avatarInput" accept="image/*" style="display:none" onchange="previewAvatar(this)" />
                <button class="btn-secondary" onclick="removerAvatar()">Remover</button>
              </div>
            </div>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Informações pessoais</h2></div>
          </div>
          <div class="config-card-body">
            <div class="field-row">
              <div class="field-group">
                <label>Nome</label>
                <input class="input-field" type="text" id="nome" placeholder="Seu nome" value="Maria" />
              </div>
            </div>
            <div class="field-group">
              <label>Nome de usuário</label>
              <input class="input-field" type="text" id="username" placeholder="@usuario" value="@mariasilva" />
              
            </div>
            <div class="field-group">
              <label>Bio</label>
              <textarea class="input-field" id="bio" rows="3" placeholder="Uma breve descrição sobre você…" style="resize:vertical;line-height:1.5">Amante da cultura capixaba 🍃</textarea>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Data de nascimento</label>
                <input class="input-field" type="date" id="nascimento" value="1995-06-12" />
              </div>
              
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-secondary">Cancelar</button>
            <button class="btn-primary" onclick="salvar('Perfil salvo com sucesso')"><i data-lucide="check"></i> Salvar</button>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Contato</h2><p>E-mail e telefone da conta</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>E-mail</label>
              <input class="input-field" type="email" id="email" value="maria@email.com" />
            </div>
            <div class="field-group">
              <label>Telefone</label>
              <input class="input-field" type="tel" id="tel" placeholder="(27) 99999-9999" />
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-secondary">Cancelar</button>
            <button class="btn-primary" onclick="salvar('Contato atualizado')"><i data-lucide="check"></i> Salvar</button>
          </div>
        </div>
      </section>

      <!-- ── CONTA & SEGURANÇA ── -->
      <section class="config-section" id="sec-conta">

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Alterar senha</h2></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>Senha atual</label>
              <input class="input-field" type="password" placeholder="••••••••" />
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Nova senha</label>
                <input class="input-field" type="password" id="novaSenha" placeholder="••••••••" />
              </div>
              <div class="field-group">
                <label>Confirmar nova senha</label>
                <input class="input-field" type="password" id="confirmarSenha" placeholder="••••••••" />
              </div>
            </div>
            <small style="font-size:12px;color:var(--muted)">Mínimo 8 caracteres, com letras e números.</small>
          </div>
          <div class="config-card-footer">
            <button class="btn-primary" onclick="salvar('Senha alterada com sucesso')"><i data-lucide="lock"></i> Atualizar senha</button>
          </div>
        </div>

        <div class="config-card danger-card">
          <div class="config-card-header">
            
          </div>
          <div class="config-card-body">
            <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:.75rem">
              <div>
                <div style="font-size:14px;font-weight:500;color:#dc2626">Excluir conta</div>
                <div style="font-size:12px;color:var(--muted);margin-top:2px">Remove permanentemente todos os seus dados</div>
              </div>
              <button class="btn-danger"><i data-lucide="trash-2"></i> Excluir conta</button>
            </div>
          </div>
        </div>
      </section>

      

      <!-- ── PAGAMENTO ── -->
      <section class="config-section" id="sec-pagamento">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Métodos de pagamento</h2>
            <button class="btn-primary"><i data-lucide="plus"></i> Adicionar cartão</button>
          </div>
          
          </div>
        </div>
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Pix</h2><p>Chave Pix cadastrada</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>Chave Pix</label>
              <input class="input-field" type="text" placeholder="CPF, e-mail, telefone ou chave aleatória" />
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-primary" onclick="salvar('Chave Pix salva')"><i data-lucide="check"></i> Salvar</button>
          </div>
        </div>
      </section>

      <!-- ── ENDEREÇOS ── -->
      <section class="config-section" id="sec-enderecos">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Endereços salvos</h2></div>
            <button class="btn-primary"><i data-lucide="plus"></i> Novo endereço</button>
          </div>
          <div class="config-card-body">
            <div style="display:flex;align-items:flex-start;justify-content:space-between;padding:.25rem 0">
              <div>
                <div style="font-size:14px;font-weight:500;color:var(--text)">Casa</div>
                <div style="font-size:13px;color:var(--muted);margin-top:3px;line-height:1.5">Rua das Palmeiras, 123 — Jardim da Penha<br/>Vitória, ES — 29060-000</div>
              </div>
              <div style="display:flex;align-items:center;gap:8px">
                <span style="font-size:11px;background:var(--laranja-l);color:var(--laranja-d);border-radius:99px;padding:2px 10px;font-weight:500">Principal</span>
                <button class="btn-secondary" style="padding:6px 10px;font-size:12px"><i data-lucide="pencil" style="width:13px;height:13px"></i></button>
                <button class="btn-secondary" style="padding:6px 10px;font-size:12px"><i data-lucide="trash-2" style="width:13px;height:13px"></i></button>
              </div>
            </div>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Adicionar endereço</h2></div>
          </div>
          <div class="config-card-body">
            <div class="field-row">
              <div class="field-group">
                <label>CEP</label>
                <input class="input-field" type="text" id="cep" placeholder="00000-000" maxlength="9" oninput="mascaraCEP(this)" />
              </div>
              <div class="field-group">
                <label>Estado</label>
                <select class="input-field" id="estado">
                  <option value="">Selecione</option>
                  <option value="ES" selected>Espírito Santo</option>
                  <option value="SP">São Paulo</option>
                  <option value="RJ">Rio de Janeiro</option>
                  <option value="MG">Minas Gerais</option>
                  <option value="BA">Bahia</option>
                </select>
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Cidade</label>
                <input class="input-field" type="text" id="cidade" placeholder="Vitória" />
              </div>
              <div class="field-group">
                <label>Bairro</label>
                <input class="input-field" type="text" placeholder="Bairro" />
              </div>
            </div>
            <div class="field-group">
              <label>Rua</label>
              <input class="input-field" type="text" placeholder="Nome da rua" />
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Número</label>
                <input class="input-field" type="text" placeholder="Nº" />
              </div>
              <div class="field-group">
                <label>Complemento</label>
                <input class="input-field" type="text" placeholder="Apto, bloco…" />
              </div>
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-secondary">Cancelar</button>
            <button class="btn-primary" onclick="salvar('Endereço salvo')"><i data-lucide="check"></i> Salvar endereço</button>
          </div>
        </div>
      </section>

      

    </div>
  </div>
</main>

<!-- Toast de confirmação -->
<div class="toast" id="toast">
  <i data-lucide="check-circle"></i>
  <span id="toastMsg">Salvo com sucesso</span>
</div>

<script src="/js/arace-state.js"></script>
<script src="/js/config.js"></script>
</body>
</html>