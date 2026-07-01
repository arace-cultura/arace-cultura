<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$nomeCompleto = trim((string) ($usuario['nome'] ?? ''));
$nome = $nomeCompleto;
$username = (string) ($usuario['username'] ?? '');
$sexo = (string) ($usuario['sexo'] ?? $usuario['genero'] ?? '');
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Arac� � Configura��o Perfil Produtor</title>
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
  <a href="<?= url_to('home') ?>" class="logo">arac�</a>

  <form class="search-wrap" action="<?= url_to('main_pesquisa') ?>" method="get">
    <i data-lucide="search"></i>
    <input type="text" name="q" id="searchHeaderInput" placeholder="Pesquisar produtos..." />
  </form>

  <div class="header-right">
    <button class="cart-btn" type="button" onclick="window.location.href='<?= url_to('main_arace_carrinho') ?>'">
      <i data-lucide="shopping-cart"></i>
      <span class="cart-count">0 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'" aria-label="Abrir perfil">
      <?php if ($avatar !== ''): ?>
        <img src="<?= esc($avatar, 'attr') ?>" alt="Avatar do usuario" />
      <?php else: ?>
        <i data-lucide="user"></i>
      <?php endif; ?>
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
    <a class="nav-item" href="<?= url_to('main_arace_config') ?>">
      <i data-lucide="settings"></i> Configura��es
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    
  </aside>
  <main>
  <div class="config-header">
    <div>
      <h1>Configura��es</h1>
      <p>Gerencie suas prefer�ncias e dados da conta</p>
    </div>
  </div>

  <div class="config-layout">

    <!-- NAV LATERAL -->
    <nav class="config-nav">
      <button class="config-nav-item active" onclick="trocarAba(this,'perfil')">
        <i data-lucide="user"></i> Perfil
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'conta')">
        <i data-lucide="shield"></i> Conta & Seguran�a
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'pagamento')">
        <i data-lucide="key-round"></i> Pix
      </button>
      <div class="config-nav-divider"></div>
      <button class="config-nav-item" onclick="trocarAba(this,'aparencia')">
        <i data-lucide="palette"></i> Apar�ncia
      </button>
      <button class="config-nav-item" onclick="trocarAba(this,'privacidade')">
        <i data-lucide="lock"></i> Privacidade
      </button>
    </nav>

    <!-- CONTE�DO -->
    <div>

      <!-- -- PERFIL -- -->
      <section class="config-section active" id="sec-perfil">
        <form action="<?= url_to('user_profile_update') ?>" method="post" enctype="multipart/form-data">

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Foto de perfil</h2></div>
          </div>
          <div class="config-card-body">
            <div class="avatar-upload-area">
              <div class="avatar-preview" id="avatarPreview">
                <?php if ($avatar !== ''): ?>
                  <img src="<?= esc($avatar, 'attr') ?>" alt="Avatar do usuario" />
                <?php else: ?>
                  <i data-lucide="user"></i>
                <?php endif; ?>
              </div>
              <div class="avatar-upload-btns">
                <label for="avatarInput" class="btn-primary" style="cursor:pointer">
                  <i data-lucide="upload"></i> Alterar foto
                </label>
                <input type="file" id="avatarInput" name="fotoUrl" accept="image/*" style="display:none" onchange="previewAvatar(this)" />
              </div>
            </div>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Informa��es pessoais</h2><p>Seus dados b�sicos</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-row">
              <div class="field-group">
                <label>Nome</label>
                <input class="input-field" type="text" id="nome" name="nome" placeholder="Seu nome" value="<?= esc($nome, 'attr') ?>" />
              </div>
            </div>
            <div class="field-group">
              <label>Nome de usu�rio</label>
              <input class="input-field" type="text" id="username" name="username" placeholder="@usuario" value="<?= esc($username, 'attr') ?>" />
              <small>Vis�vel publicamente no seu perfil</small>
            </div>
            <div class="field-group">
              <label>Bio</label>
              <textarea class="input-field" id="bio" name="bio" rows="3" placeholder="Uma breve descri��o sobre voc�" style="resize:vertical;line-height:1.5"><?= esc($usuario['bio'] ?? '') ?></textarea>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Data de nascimento</label>
                <input class="input-field" type="date" id="nascimento" name="nascimento" value="<?= esc($usuario['nascimento'] ?? '', 'attr') ?>" />
              </div>
              <div class="field-group">
                <label>Sexo</label>
                <select class="input-field" id="sexo" name="sexo">
                  <option value="">Prefiro n�o informar</option>
                  <option value="f" <?= $sexo === 'f' ? 'selected' : '' ?>>Feminino</option>
                  <option value="m" <?= $sexo === 'm' ? 'selected' : '' ?>>Masculino</option>
                  <option value="nb" <?= $sexo === 'nb' ? 'selected' : '' ?>>N�o-bin�rio</option>
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
              <input class="input-field" type="email" id="email" value="<?= esc($usuario['email'] ?? '', 'attr') ?>" readonly />
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

      <!-- -- CONTA & SEGURAN�A -- -->
      <section class="config-section" id="sec-conta">

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Alterar senha</h2></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>Senha atual</label>
              <input class="input-field" type="password" placeholder="��������" />
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Nova senha</label>
                <input class="input-field" type="password" id="novaSenha" placeholder="��������" />
              </div>
              <div class="field-group">
                <label>Confirmar nova senha</label>
                <input class="input-field" type="password" id="confirmarSenha" placeholder="��������" />
              </div>
            </div>
            <small style="font-size:12px;color:var(--muted)">M�nimo 8 caracteres, com letras e n�meros.</small>
          </div>
        </div>

        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Verifica��o em duas etapas</h2><p>Adiciona uma camada extra de seguran�a</p></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info">
                <span>SMS</span>
                <small>Receber c�digo por mensagem de texto</small>
              </div>
              <label class="toggle-switch">
                <input type="checkbox" checked />
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info">
                <span>E-mail</span>
                <small>Receber c�digo por e-mail</small>
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
            <div><h2>Zona de perigo</h2><p>A��es irrevers�veis</p></div>
          </div>
          <div class="config-card-body">
            <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:.75rem">
              <div>
                <div style="font-size:14px;font-weight:500;color:var(--text)">Desativar conta</div>
                <div style="font-size:12px;color:var(--muted);margin-top:2px">Sua conta ficar� invis�vel temporariamente</div>
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
      <!-- -- PIX -- -->
      <section class="config-section" id="sec-pagamento">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Pix</h2><p>Chave Pix cadastrada</p></div>
          </div>
          <div class="config-card-body">
            <div class="field-group">
              <label>Chave Pix</label>
              <input class="input-field" type="text" name="pix" placeholder="CPF, e-mail, telefone ou chave aleatoria" />
            </div>
          </div>
        </div>
      </section>

      <!-- -- APAR�NCIA -- -->
      <section class="config-section" id="sec-aparencia">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Tema</h2><p>Escolha a apar�ncia da interface</p></div>
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
                  <span class="flag">????</span>
                  <select class="input-field" id="idioma">
                    <option value="pt-BR" selected>Portugu�s (Brasil)</option>
                    <option value="en">English</option>
                    <option value="es">Espa�ol</option>
                  </select>
                </div>
              </div>
              <div class="field-group">
                <label>Moeda</label>
                <select class="input-field">
                  <option value="BRL" selected>BRL � Real brasileiro</option>
                  <option value="USD">USD � D�lar americano</option>
                  <option value="EUR">EUR � Euro</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- -- PRIVACIDADE -- -->
      <section class="config-section" id="sec-privacidade">
        <div class="config-card">
          <div class="config-card-header">
            <div><h2>Visibilidade do perfil</h2></div>
          </div>
          <div class="config-card-body">
            <div class="toggle-row">
              <div class="toggle-info"><span>Perfil p�blico</span><small>Outros usu�rios podem ver seu perfil</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
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
              <div class="toggle-info"><span>Cookies de an�lise</span><small>Ajuda a melhorar a plataforma</small></div>
              <label class="toggle-switch"><input type="checkbox" checked /><span class="toggle-slider"></span></label>
            </div>
            <div class="toggle-row">
              <div class="toggle-info"><span>Personaliza��o de an�ncios</span><small>Recomenda��es baseadas no seu hist�rico</small></div>
              <label class="toggle-switch"><input type="checkbox" /><span class="toggle-slider"></span></label>
            </div>
          </div>
        </div>
      </section>
<!-- Toast de confirma��o -->
<div class="toast" id="toast">
  <i data-lucide="check-circle"></i>
  <span id="toastMsg">Salvo com sucesso</span>
</div>


<script>window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;</script>
<script src="/js/arace-state.js"></script>
<script src="/js/brasil-api-validacao.js?v=20260630-fix"></script>
<script src="/js/config.js?v=20260630-fix"></script>

    </div><!-- /conte�do -->
  </div><!-- /config-layout -->
</main>

</body>
