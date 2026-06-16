﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê - Cadastro</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Google+Sans+Flex&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="/css/autenticacao.css">
</head>
<body>

  <div class="login-card cadastro-card item-animado">
    <div class="logo-wrap item-animado atraso-1">
      <img src="/images/arace.png" alt="Logo Aracê" />
    </div>

    <p class="subtitle item-animado atraso-1">Crie sua conta</p>

    <form id="formCadastro" action="/usuario/arace-perfil" method="get" novalidate>
    <?php if (session('erro')): ?>
      <p class="erro-campo" style="display:block"><?= esc(session('erro')) ?></p>
    <?php endif; ?>

    <?php if (session('erros')): ?>
      <ul class="erro-campo" style="display:block">
        <?php foreach (session('erros') as $erro): ?>
          <li><?= esc($erro) ?></li>
        <?php endforeach; ?>
      </ul>
    <?php endif; ?>

    <form id="formCadastro" action="/cadastro/clientes" method="post" novalidate>
      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="user"></i>
        <input type="text" id="nome" name="nome" placeholder="Nome completo" value="<?= esc(old('nome')) ?>" required />
      </div>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="mail"></i>
        <input type="email" id="email" name="email" placeholder="E-mail" value="<?= esc(old('email')) ?>" required />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="lock"></i>
        <input type="password" id="senha" name="senha" placeholder="Senha" required />
        <button type="button" class="toggle-senha" onclick="alternarSenha('senha', 'icone-olho-1')">
          <i id="icone-olho-1" data-lucide="eye"></i>
        </button>
      </div>

      <div class="item-animado atraso-5">
        <button type="submit" class="btn-login">Criar conta</button>

        <div class="links-rodape">
          <a href="login" class="esqueceu">Já tenho uma conta</a>
        </div>
      </div>

    </form>
  </div>

  <script src="https://unpkg.com/lucide@latest"></script>
  <script>lucide.createIcons();</script>
  <script>
    function alternarSenha(campoId, iconeId) {
      const campo = document.getElementById(campoId);
      const icone = document.getElementById(iconeId);

      if (!campo || !icone) return;

      campo.type = campo.type === 'password' ? 'text' : 'password';
      icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
      lucide.createIcons();
    }
  </script>
</body>
</html>
