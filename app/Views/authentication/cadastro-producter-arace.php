﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê - Cadastro Produtor</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="<?= base_url('css/autenticacao.css') ?>">
</head>
<body>

  <div class="login-card cadastro-card item-animado">
    <div class="logo-wrap item-animado atraso-1">
      <img src="<?= base_url('images/arace.png') ?>" alt="Logo Aracê" />
    </div>

    <p class="subtitle item-animado atraso-1">Crie sua conta de produtor</p>

    <?php if (session('erro')): ?>
      <p class="erro-campo" style="display:block"><?= esc(session('erro')) ?></p>
    <?php endif; ?>

    <form id="formCadastroDono" action="/cadastro/produtor/dono" method="post" novalidate>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="user"></i>
        <input type="text" id="nome-dono" name="nome" placeholder="Nome completo" autocomplete="name" required />
      </div>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="id-card"></i> <input type="text" id="cpf" name="cpf" placeholder="CPF" required />
      </div>
      <span id="cpf-erro" class="erro-campo" style="display:none">CPF inválido</span>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="mail"></i>
        <input type="email" id="email" name="email" placeholder="E-mail de acesso" autocomplete="email" required />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="phone"></i>
        <input type="tel" id="telefone" name="telefone" placeholder="Telefone / Celular" autocomplete="tel" required />
      </div>

      

      <div class="item-animado atraso-5">
        <label class="permanecer">
          <input type="checkbox" id="termos" name="termosAceitos" value="1" required />
          Aceito os <a href="#" target="_blank">termos de uso</a>
        </label>

        <button type="submit" class="btn-login">Dados da Loja</button>

        <div class="links-rodape">
          <a href="/" class="voltar">Voltar</a>
        </div>
      </div>

    </form>
  </div>

  <script src="https://unpkg.com/lucide@latest"></script>
  <script>lucide.createIcons();</script>
  <script src="<?= base_url('js/cadastro-produtor.js') ?>"></script>
</body>
</html>
