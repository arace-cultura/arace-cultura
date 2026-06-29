<!DOCTYPE html>
<?php
$usuario = $usuario ?? [];
$nomeCompleto = trim((string) ($usuario['nome'] ?? ''));
$nome = $nomeCompleto;
$username = (string) ($usuario['username'] ?? '');
$genero = (string) ($usuario['genero'] ?? '');
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Configuração Perfil Produtor</title>
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
        <i data-lucide="shield"></i> Conta & Segurança
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'notificacoes')">
        <i data-lucide="bell"></i> Notificações
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'pagamento')">
        <i data-lucide="credit-card"></i> Pagamento
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'enderecos')">
        <i data-lucide="map-pin"></i> Endereços
      </button>
      <div class="config-nav-divider"></div>
      <button class="config-nav-item" onclick="trocarAba(this,'aparencia')">
        <i data-lucide="palette"></i> Aparência
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'privacidade')">
        <i data-lucide="lock"></i> Privacidade
      </button>
    </nav>

    <!-- CONTEÚDO -->
    <div>

      <!-- ── PERFIL ── -->
      <section class="config-section active" id="sec-perfil">
        <form action="<?= url_to('user_profile_update') ?>" method="post" enctype="multipart/form-data">

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Foto de perfil</h2></div>
          </div>
          <div class="config-card-body">
            <div class="avatar-upload-area">
              <div class="avatar-preview" id="avatarPreview">
                <?php if (! empty($usuario['avatar'])): ?>
                  <img src="<?= esc($usuario['avatar'], 'attr') ?>" alt="Avatar do usuario" />
                <?php else: ?>
                  <i data-lucide="user"></i>
                <?php endif; ?>
              </div>
              <div class="avatar-upload-btns">
                <label for="avatarInput" class="btn-primary" style="cursor:pointer">
                  <i data-lucide="upload"></i> Alterar foto
                </label>
                <input type="file" id="avatarInput" name="avatar" accept="image/*" style="display:none" onchange="previewAvatar(this)" />
              </div>
            </div>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Informações pessoais</h2><p>Seus dados básicos</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-row">
              <div class="field-group">
                <label>Nome</label>
                <input class="input-field" type="text" id="nome" name="nome" placeholder="Seu nome" value="<?= esc($nome, 'attr') ?>" />
              </div>
            </div>
            <div class="field-group">
              <label>Nome de usuário</label>
              <input class="input-field" type="text" id="username" name="username" placeholder="@usuario" value="<?= esc($username, 'attr') ?>" />
              <small>Visível publicamente no seu perfil</small>
            </div>
            <div class="field-group">
              <label>Bio</label>
              <textarea class="input-field" id="bio" name="bio" rows="3" placeholder="Uma breve descrição sobre você…" style="resize:vertical;line-height:1.5"><?= esc($usuario['bio'] ?? '') ?></textarea>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Data de nascimento</label>
                <input class="input-field" type="date" id="nascimento" name="nascimento" value="<?= esc($usuario['nascimento'] ?? '', 'attr') ?>" />
              </div>
              <div class="field-group">
                <label>Gênero</label>
                <select class="input-field" id="genero" name="genero">
                  <option value="">Prefiro não informar</option>
                  <option value="f" <?= $genero === 'f' ? 'selected' : '' ?>>Feminino</option>
                  <option value="m" <?= $genero === 'm' ? 'selected' : '' ?>>Masculino</option>
                  <option value="nb" <?= $genero === 'nb' ? 'selected' : '' ?>>Não-binário</option>
                </select>
              </div>
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Contato</h2><p>E-mail e telefone da conta</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>E-mail</label>
              <input class="input-field" type="email" id="email" name="email" value="<?= esc($usuario['email'] ?? '', 'attr') ?>" />
            </div>
            <div class="field-group">
              <label>Telefone</label>
              <input class="input-field" type="tel" id="tel" name="telefone" placeholder="(27) 99999-9999" value="<?= esc($usuario['telefone'] ?? '', 'attr') ?>" />
            </div>
          </div>
          <div class="config-card-footer">
            <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
          </div>
        </div>
        </form>
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
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Verificação em duas etapas</h2><p>Adiciona uma camada extra de segurança</p></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info">
                <span>SMS</span>
                <small>Receber código por mensagem de texto</small>
              </div>
              <label class="toggle-switch">
                <input type="checkbox" checked />
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info">
                <span>E-mail</span>
                <small>Receber código por e-mail</small>
              </div>
              <label class="toggle-switch">
                <input type="checkbox" />
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info">
                <span>App autenticador</span>
                <small>Google Authenticator, Authy, etc.</small>
              </div>
              <label class="toggle-switch">
                <input type="checkbox" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>

        <div class="config-card danger-card">
          <div class="config-card-header">
            <div><h2>Zona de perigo</h2><p>Ações irreversíveis</p></div>
          </div>
          <div class="config-card-body">
            <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:.75rem">
              <div>
                <div style="font-size:14px;font-weight:500;color:var(--text)">Desativar conta</div>
                <div style="font-size:12px;color:var(--muted);margin-top:2px">Sua conta ficará invisível temporariamente</div>
              </div>
            </div>
            <div style="height:.5px;background:rgba(220,38,38,.15);margin:.25rem 0"></div>
            <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:.75rem">
              <div>
                <div style="font-size:14px;font-weight:500;color:#dc2626">Excluir conta</div>
                <div style="font-size:12px;color:var(--muted);margin-top:2px">Remove permanentemente todos os seus dados</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ── NOTIFICAÇÕES ── -->
      <section class="config-section" id="sec-notificacoes">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Notificações por e-mail</h2></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info"><span>Novos pedidos</span><small>Quando um pedido for feito ou atualizado</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Promoções</span><small>Ofertas exclusivas e cupons</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Newsletter</span><small>Novidades da cultura capixaba</small></div>
              <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Respostas de avaliações</span><small>Quando alguém responder sua avaliação</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
          </div>
        </div>
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Notificações push</h2></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info"><span>Chat</span><small>Novas mensagens de artesãos</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Status do pedido</span><small>Atualizações de envio e entrega</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Novos produtos favoritos</span><small>Produtos novos de artesãos que você segue</small></div>
              <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
            </div>
          </div>
        </div>
      </section>

      <!-- ── PAGAMENTO ── -->
      <section class="config-section" id="sec-pagamento">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Métodos de pagamento</h2><p>Cartões e formas de pagamento salvos</p></div>
          </div>
          <div class="config-card-body" id="cartoesList">
            <div style="display:flex;align-items:center;justify-content:space-between;padding:.5rem 0">
              <div style="display:flex;align-items:center;gap:12px">
                <div style="width:44px;height:28px;background:var(--bg);border:.5px solid var(--border);border-radius:6px;display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700;color:var(--azul)">VISA</div>
                <div>
                  <div style="font-size:14px;color:var(--text)">•••• •••• •••• 4242</div>
                  <div style="font-size:12px;color:var(--muted)">Expira 12/27</div>
                </div>
              </div>
              <div style="display:flex;align-items:center;gap:8px">
                <span style="font-size:11px;background:var(--verde-l);color:var(--verde);border-radius:99px;padding:2px 10px;font-weight:500">Principal</span>
              </div>
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
        </div>
      </section>

      <!-- ── ENDEREÇOS ── -->
      <section class="config-section" id="sec-enderecos">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Endereços salvos</h2></div>
          </div>
          <div class="config-card-body">
            <div style="display:flex;align-items:flex-start;justify-content:space-between;padding:.25rem 0">
              <div>
                <div style="font-size:14px;font-weight:500;color:var(--text)">Casa</div>
                <div style="font-size:13px;color:var(--muted);margin-top:3px;line-height:1.5">Rua das Palmeiras, 123 — Jardim da Penha<br/>Vitória, ES — 29060-000</div>
              </div>
              <div style="display:flex;align-items:center;gap:8px">
                <span style="font-size:11px;background:var(--laranja-l);color:var(--laranja-d);border-radius:99px;padding:2px 10px;font-weight:500">Principal</span>
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
        </div>
      </section>

      <!-- ── APARÊNCIA ── -->
      <section class="config-section" id="sec-aparencia">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Tema</h2><p>Escolha a aparência da interface</p></div>
          </div>
          <div class="config-card-body">
            <div class="theme-options">
              <div class="theme-option active" onclick="selecionarTema(this)">
                <div class="theme-swatch" style="background:linear-gradient(135deg,#f4f2ed,#fff)"></div>
                <span class="theme-label">Claro</span>
              </div>
              <div class="theme-option" onclick="selecionarTema(this)">
                <div class="theme-swatch" style="background:linear-gradient(135deg,#1a1a18,#2a2a25)"></div>
                <span class="theme-label">Escuro</span>
              </div>
              <div class="theme-option" onclick="selecionarTema(this)">
                <div class="theme-swatch" style="background:linear-gradient(135deg,#f4f2ed 50%,#1a1a18 50%)"></div>
                <span class="theme-label">Sistema</span>
              </div>
            </div>
          </div>
        </div>
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Idioma e moeda</h2></div>
          </div>
          <div class="config-card-body">
            <div class="field-row">
              <div class="field-group">
                <label>Idioma</label>
                <div class="select-with-icon">
                  <span class="flag">🇧🇷</span>
                  <select class="input-field" id="idioma">
                    <option value="pt-BR" selected>Português (Brasil)</option>
                    <option value="en">English</option>
                    <option value="es">Español</option>
                  </select>
                </div>
              </div>
              <div class="field-group">
                <label>Moeda</label>
                <select class="input-field">
                  <option value="BRL" selected>BRL — Real brasileiro</option>
                  <option value="USD">USD — Dólar americano</option>
                  <option value="EUR">EUR — Euro</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ── PRIVACIDADE ── -->
      <section class="config-section" id="sec-privacidade">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Visibilidade do perfil</h2></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info"><span>Perfil público</span><small>Outros usuários podem ver seu perfil</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Mostrar lista de favoritos</span><small>Seus produtos favoritos ficam visíveis</small></div>
              <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Aparecer nas buscas</span><small>Seu perfil aparece nos resultados de pesquisa</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
          </div>
        </div>
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Dados e privacidade</h2></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info"><span>Cookies de análise</span><small>Ajuda a melhorar a plataforma</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Personalização de anúncios</span><small>Recomendações baseadas no seu histórico</small></div>
              <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
            </div>
          </div>
        </div>
      </section>
<!-- Toast de confirmação -->
<div class="toast" id="toast">
  <i data-lucide="check-circle"></i>
  <span id="toastMsg">Salvo com sucesso</span>
</div>


<script src="/js/arace-state.js"></script>
<script src="/js/config.js"></script>

    </div><!-- /conteúdo -->
  </div><!-- /config-layout -->
</main>

</body>
