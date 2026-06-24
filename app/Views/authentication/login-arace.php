﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê - Login</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/autenticacao.css">
</head>
<body>

  <div class="login-card item-animado">
    <div class="logo-wrap item-animado atraso-1">
      <img src="/images/arace.png" alt="Logo Aracê" />
    </div>

    <p class="subtitle item-animado atraso-1">Bem-vindo de volta</p>

    <?php if (session('erro')): ?>
      <p class="mensagem-login mensagem-erro" role="alert"><?= esc(session('erro')) ?></p>
    <?php elseif (session('sucesso')): ?>
      <p class="mensagem-login mensagem-sucesso" role="status"><?= esc(session('sucesso')) ?></p>
    <?php endif; ?>

    <form id="formLogin" action="<?= site_url('login') ?>" method="post">
      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="user"></i>
        <input type="email" id="email" name="email" placeholder="E-mail" autocomplete="email" value="<?= esc(old('email')) ?>" required />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="lock"></i>
        <input type="password" id="senha" name="senha" placeholder="Senha" autocomplete="current-password" required />
        <button type="button" class="toggle-senha" onclick="alternarSenha()" aria-label="Mostrar/ocultar senha">
          <i id="icone-olho" data-lucide="eye"></i>
        </button>
      </div>

      <div class="item-animado atraso-4">
        <label class="permanecer">
          <input type="checkbox" id="lembrar" name="lembrar" value="1" <?= old('lembrar') ? 'checked' : '' ?> />
          Permanecer conectado
        </label>

        <button type="submit" class="btn-login" id="btnEntrar">Entrar</button>

        <div class="links-rodape">
          <a href="<?= site_url('cadastro') ?>">Cadastre-se</a>
          <a href="#" class="esqueceu">Esqueceu a senha?</a>
        </div>
      </div>
    </form>
  </div>

  <script src="https://unpkg.com/lucide@latest"></script>
  <script>lucide.createIcons();</script>
  <script src="/js/login.js"></script>
</body>
</html>
