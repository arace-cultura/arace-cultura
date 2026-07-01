<!DOCTYPE html>
<html lang="pt-BR">
<?php
$usuario = $usuario ?? session()->get('arace_user') ?? [];
$produtor = session('produtor') ?? $produtor ?? [];
$avatar = trim((string) ($usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$lojaAvatar = trim((string) ($produtor['fotoUrl'] ?? $produtor['lojaAvatar'] ?? $produtor['avatar'] ?? $usuario['fotoUrl'] ?? $usuario['avatar'] ?? ''));
$bannerUrl = trim((string) ($produtor['bannerUrl'] ?? $produtor['banner'] ?? ''));
$lojaNome = (string) ($produtor['nomeLoja'] ?? $produtor['nome_loja'] ?? $produtor['nome'] ?? $usuario['nome'] ?? '');
$lojaBio = (string) ($produtor['lojaBio'] ?? $produtor['bio'] ?? '');
$lojaCategoria = (string) ($produtor['categoria'] ?? $produtor['categoria_principal'] ?? '');
$lojaEmail = (string) ($produtor['email'] ?? $produtor['email_comercial'] ?? $usuario['email'] ?? '');
$lojaTelefone = (string) ($produtor['telefone'] ?? $produtor['telefone_comercial'] ?? $usuario['telefone'] ?? '');
$lojaCidade = (string) ($produtor['cidade'] ?? $usuario['cidade'] ?? '');
$lojaEstado = (string) ($produtor['estado'] ?? $usuario['estado'] ?? '');
$lojaLocal = trim($lojaCidade . ($lojaEstado !== '' ? ' - ' . $lojaEstado : ''), ' -');
$lojaPix = (string) ($produtor['pix'] ?? $usuario['pix'] ?? '');
$fotosHistoria = array_values(array_filter(array_map('strval', is_array($produtor['fotosHistoria'] ?? null) ? $produtor['fotosHistoria'] : [])));
?>
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Aracê - Configuração da Loja</title>
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
      <span class="cart-count">0 itens</span>
    </button>
    <button class="avatar-btn" type="button" onclick="window.location.href='<?= url_to('user_arace_perfil') ?>'" aria-label="Abrir perfil">
      <?php if ($avatar !== ''): ?>
        <img src="<?= esc($avatar, 'attr') ?>" alt="Avatar do usuário" />
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
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
  </aside>

  <main>
    <div class="config-header">
      <div>
        <h1>Configurações da Loja</h1>
        <p>Gerencie a identidade, dados comerciais e logística do seu negócio</p>
      </div>
    </div>

    <div class="config-layout config-layout-full store-config-layout">
      <div>
        <?php if (session('erro')): ?>
          <div class="toast show" style="position:static;transform:none;opacity:1;background:#dc2626;margin-bottom:1rem">
            <i data-lucide="alert-circle"></i>
            <span><?= esc(session('erro')) ?></span>
          </div>
        <?php elseif (session('sucesso')): ?>
          <div class="toast show" style="position:static;transform:none;opacity:1;margin-bottom:1rem">
            <i data-lucide="check-circle"></i>
            <span><?= esc(session('sucesso')) ?></span>
          </div>
        <?php endif; ?>

        <form action="<?= url_to('produtor_config_loja_update') ?>" method="post" enctype="multipart/form-data">

        <section class="config-section active" id="sec-identidade">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Logotipo e Capa da Loja</h2><p>Imagens que aparecem na sua vitrine pública</p></div>
            </div>
            <div class="config-card-body">
              <label style="display:block;margin-bottom:8px;font-size:13px;font-weight:500;color:var(--text)">Logotipo da Loja</label>
              <div class="avatar-upload-area" style="margin-bottom:1.5rem">
                <div class="avatar-preview" id="avatarPreview">
                  <?php if ($lojaAvatar !== ''): ?>
                    <img src="<?= esc($lojaAvatar, 'attr') ?>" alt="Logotipo da loja" />
                  <?php else: ?>
                    <i data-lucide="store"></i>
                  <?php endif; ?>
                </div>
                <div class="avatar-upload-btns">
                  <label for="avatarInput" class="btn-primary" style="cursor:pointer">
                    <i data-lucide="upload"></i> Enviar Logo
                  </label>
                  <input type="file" id="avatarInput" name="fotoUrl" accept="image/*" style="display:none" onchange="previewAvatar(this)" />
                </div>
              </div>

              <div class="field-group">
                <label>Imagem de Capa (Banner)</label>
                <div style="border:2px dashed var(--border);border-radius:var(--r);padding:2rem;text-align:center;background:var(--branco)">
                  <i data-lucide="image" style="width:32px;height:32px;color:var(--muted);margin-bottom:8px"></i>
                  <label for="bannerInput" style="display:block;font-size:14px;color:var(--text);cursor:pointer">Clique para enviar um banner panorâmico</label>
                  <small style="display:block;margin-top:4px;color:var(--faint)">Recomendado: 1200x300px</small>
                  <?php if ($bannerUrl !== ''): ?>
                    <small style="display:block;margin-top:8px;color:var(--verde)">Banner cadastrado</small>
                  <?php endif; ?>
                  <input type="file" id="bannerInput" name="bannerUrl" accept="image/*" style="display:none" />
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
                <input class="input-field" type="text" id="lojaNome" name="nomeLoja" value="<?= esc($lojaNome, 'attr') ?>" />
              </div>
              <div class="field-group">
                <label>História / Biografia da Loja</label>
                <textarea class="input-field" id="lojaBio" name="lojaBio" rows="4" style="resize:vertical;line-height:1.5"><?= esc($lojaBio) ?></textarea>
              </div>
              <div class="field-group">
                <label>Fotos da história</label>
                <label for="fotosHistoriaInput" class="btn-primary" style="display:inline-flex;cursor:pointer">
                  <i data-lucide="image-plus"></i> Enviar fotos
                </label>
                <input id="fotosHistoriaInput" type="file" name="fotosHistoria[]" accept="image/*" multiple style="display:none" />
                <?php if ($fotosHistoria !== []): ?>
                  <small style="display:block;margin-top:8px;color:var(--verde)"><?= count($fotosHistoria) ?> foto(s) cadastrada(s)</small>
                <?php endif; ?>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
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
                  <input class="input-field" type="text" name="cnpj" value="<?= esc($produtor['cnpj'] ?? '', 'attr') ?>" placeholder="00.000.000/0001-00" />
                </div>
                <div class="field-group">
                  <label>Categoria Principal</label>
                  <select class="input-field" id="lojaCategoria" name="categoria">
                    <option value="ceramica" <?= $lojaCategoria === 'ceramica' ? 'selected' : '' ?>>Cerâmica & Panelas de Barro</option>
                    <option value="artesanato" <?= $lojaCategoria === 'artesanato' ? 'selected' : '' ?>>Artesanato Geral</option>
                    <option value="alimentos" <?= $lojaCategoria === 'alimentos' ? 'selected' : '' ?>>Doces & Alimentos Caseiros</option>
                  </select>
                </div>
              </div>
              <div class="field-row">
                <div class="field-group">
                  <label>E-mail de Atendimento Comercial</label>
                  <input class="input-field" type="email" id="lojaEmail" name="email" value="<?= esc($lojaEmail, 'attr') ?>" />
                </div>
                <div class="field-group">
                  <label>WhatsApp / Telefone da Loja</label>
                  <input class="input-field" type="tel" id="lojaTelefone" name="telefone" value="<?= esc($lojaTelefone, 'attr') ?>" />
                </div>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
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
                <label class="toggle-switch"><input type="checkbox" name="retiradaLocal" value="1" <?= ($produtor['retiradaLocal'] ?? true) ? 'checked' : '' ?> /><span class="toggle-slider"></span></label>
              </div>
              <div class="toggle-row">
                <div class="toggle-info"><span>Envio via Correios (PAC/Sedex)</span><small>Cálculo de peso baseado na tabela oficial dos Correios</small></div>
                <label class="toggle-switch"><input type="checkbox" name="envioCorreios" value="1" <?= ($produtor['envioCorreios'] ?? true) ? 'checked' : '' ?> /><span class="toggle-slider"></span></label>
              </div>
              <div class="toggle-row">
                <div class="toggle-info"><span>Entrega Local / Motoboy</span><small>Taxa fixa para distritos vizinhos ou mesma cidade</small></div>
                <label class="toggle-switch"><input type="checkbox" name="entregaLocal" value="1" <?= ($produtor['entregaLocal'] ?? false) ? 'checked' : '' ?> /><span class="toggle-slider"></span></label>
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
                  <input class="input-field" type="text" name="cepOrigem" value="<?= esc($produtor['cepOrigem'] ?? '', 'attr') ?>" />
                </div>
                <div class="field-group">
                  <label>Distrito / Município</label>
                  <input class="input-field" type="text" id="lojaCidade" value="<?= esc($lojaLocal, 'attr') ?>" readonly style="background:var(--bg)" />
                  <input type="hidden" name="cidade" value="<?= esc($lojaCidade, 'attr') ?>" />
                  <input type="hidden" name="estado" value="<?= esc($lojaEstado, 'attr') ?>" />
                </div>
              </div>
              <div class="field-group">
                <label>Endereço Completo da Oficina</label>
                <input class="input-field" type="text" name="endereco" value="<?= esc($produtor['endereco'] ?? '', 'attr') ?>" />
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
            </div>
          </div>
        </section>
        <section class="config-section" id="sec-financeiro">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Pix</h2><p>Chave que receberá os repasses das vendas realizadas na Aracê</p></div>
            </div>
            <div class="config-card-body">
              <div class="field-group">
                <label>Chave Pix</label>
                <input class="input-field" type="text" name="pix" value="<?= esc($lojaPix, 'attr') ?>" placeholder="CNPJ, CPF, e-mail, telefone ou chave aleatória" />
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
            </div>
          </div>
        </section>

        <section class="config-section" id="sec-horarios">
          <div class="config-card">
            <div class="config-card-header">
              <div><h2>Horário de Funcionamento</h2><p>Períodos em que a loja aceita retiradas</p></div>
            </div>
            <div class="config-card-body">
              <div class="field-row" style="align-items: center; margin-bottom: 12px;">
                <div style="width: 120px; font-weight: 500;">Segunda a Sexta</div>
                <input class="input-field" type="time" name="horarioSemanaInicio" value="<?= esc($produtor['horarioSemanaInicio'] ?? '08:00', 'attr') ?>" style="max-width: 100px;" />
                <span>às</span>
                <input class="input-field" type="time" name="horarioSemanaFim" value="<?= esc($produtor['horarioSemanaFim'] ?? '18:00', 'attr') ?>" style="max-width: 100px;" />
              </div>
              <div class="field-row" style="align-items: center; margin-bottom: 12px;">
                <div style="width: 120px; font-weight: 500;">Sábados</div>
                <input class="input-field" type="time" name="horarioSabadoInicio" value="<?= esc($produtor['horarioSabadoInicio'] ?? '08:00', 'attr') ?>" style="max-width: 100px;" />
                <span>às</span>
                <input class="input-field" type="time" name="horarioSabadoFim" value="<?= esc($produtor['horarioSabadoFim'] ?? '12:00', 'attr') ?>" style="max-width: 100px;" />
              </div>
              <div class="field-row" style="align-items: center;">
                <div style="width: 120px; font-weight: 500;">Domingos</div>
                <span style="color:var(--muted); font-size:14px; font-style:italic;">Fechado</span>
              </div>
            </div>
            <div class="config-card-footer">
              <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar</button>
            </div>
          </div>
        </section>

        <div class="store-config-actions">
          <button class="btn-primary" type="submit"><i data-lucide="check"></i> Salvar alterações</button>
        </div>

        </form>

        <div class="toast" id="toast">
          <i data-lucide="check-circle"></i>
          <span id="toastMsg">Salvo com sucesso</span>
        </div>

        <script>
          window.ARACE_AUTH_USER = <?= json_encode($usuario, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
          window.ARACE_PRODUCER = <?= json_encode($produtor, JSON_UNESCAPED_UNICODE | JSON_HEX_TAG | JSON_HEX_AMP | JSON_HEX_APOS | JSON_HEX_QUOT) ?>;
        </script>
        <script src="/js/arace-state.js"></script>
        <script src="/js/brasil-api-validacao.js?v=20260630-fix"></script>
        <script src="/js/config.js?v=20260630-fix"></script>

      </div></div></main>

</body>
</html>
