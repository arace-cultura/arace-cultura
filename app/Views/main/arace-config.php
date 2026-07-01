<!DOCTYPE html>
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$nomeCompleto = trim((string) ($usuario['nome'] ?? ''));
$nome = $nomeCompleto;
$username = (string) ($usuario['username'] ?? '');
$sexo = (string) ($usuario['sexo'] ?? $usuario['genero'] ?? '');
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$isProdutor = in_array($usuario['isProdutor'] ?? false, [true, 1, '1', 'true'], true);
?>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê — Configurações</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&family=Playfair+Display:wght@700&display=swap" rel="stylesheet" />
  <script src="https://unpkg.com/lucide@latest"></script>
  <script src="<?= base_url('js/icons.js') ?>"></script>
  <link href="<?= base_url('css/config.css?v=20260629-full') ?>" rel="stylesheet" />
</head>
<body>

<!-- HEADER -->
  <header>
    <a href="<?= url_to('home') ?>" class="logo">aracê</a>
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
      <i data-lucide="settings"></i> Configurações
    </a>
    <a class="nav-item" href="<?= url_to('user_arace_perfil') ?>">
      <i data-lucide="user"></i> Perfil
    </a>
    <?php if (! $isProdutor): ?>
      <a class="nav-item" href="<?= url_to('auth_cadastro_produtor') ?>">
        <i data-lucide="box"></i> Quero ser produtor
      </a>
    <?php endif; ?>
   
  </aside>

<main>
  <div class="config-header">
    <div>
      <h1>Configurações</h1>
    </div>
  </div>

  <div class="config-layout config-layout-full">

    
    <!-- CONTEÚDO -->
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
                <label>Sexo</label>
                <select class="input-field" id="sexo" name="sexo">
                  <option value="">Prefiro não informar</option>
                  <option value="f" <?= $sexo === 'f' ? 'selected' : '' ?>>Feminino</option>
                  <option value="m" <?= $sexo === 'm' ? 'selected' : '' ?>>Masculino</option>
                  <option value="nb" <?= $sexo === 'nb' ? 'selected' : '' ?>>Não-binário</option>
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

      <!-- -- CONTA & SEGURANÇA -- -->
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

        

        <div class="config-card danger-card">
          <div class="config-card-header">
            <div><h2>Excluir conta</h2></div>
          </div>
          <div class="config-card-body">
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

      <!-- -- PAGAMENTO -- -->
      <section class="config-section" id="sec-pagamento">
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

      
        
          </div>
        </div>
      </section>

      

    </div><!-- /conteúdo -->
  </div><!-- /config-layout -->
</main>

<!-- Toast de confirmação -->
<div class="toast" id="toast">
  <i data-lucide="check-circle"></i>
  <span id="toastMsg">Salvo com sucesso</span>
</div>

<script>window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;</script>
<script src="/js/arace-state.js"></script>
<script src="/js/brasil-api-validacao.js?v=20260630-fix"></script>
<script src="/js/config.js?v=20260630-fix"></script>
</body>
</html>
