﻿﻿<!DOCTYPE html>
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

    <p class="subtitle item-animado atraso-1">Comece a vender seus produtos</p>

    <?php if (session('erro')): ?>
      <p class="erro-campo" style="display:block"><?= esc(session('erro')) ?></p>
    <?php endif; ?>

    <form id="formCadastro" action="/cadastro/produtores" method="post" novalidate>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="store"></i> <input type="text" id="nome" name="nomeLoja" placeholder="Nome da loja" required />
      </div>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="file-text"></i>
        <input type="text" id="cnpj" name="cnpj" placeholder="CNPJ (opcional)" />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="mail"></i>
        <input type="email" id="email" name="email" placeholder="E-mail comercial" autocomplete="email" required />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="phone"></i>
        <input type="tel" id="telefone" name="telefone" placeholder="Telefone / WhatsApp comercial" autocomplete="tel" required />
      </div>

      <div class="input-group-custom item-animado atraso-4">
        <i data-lucide="tags"></i>
        <select id="categoria" name="categoria" required>
          <option value="" disabled selected>Categoria principal</option>
          <option value="artesanato">Artesanato</option>
          <option value="ceramica">Cerâmica</option>
          <option value="vestuario">Vestuário</option>
          <option value="praiana">Praiana</option>
          <option value="outros">Outros</option>
        </select>
      </div>

      <div class="input-group-custom item-animado atraso-4">
        <i data-lucide="map-pin"></i>
        <select id="distritos" name="distritoId" required>
          <option value="" disabled selected>Distrito da loja</option>
          <option value="vitoria">Vitoria</option>
          <option value="vila-velha">Vila Velha</option>
          <option value="serra">Serra</option>
          <option value="cariacica">Cariacica</option>
          <option value="guarapari">Guarapari</option>
        </select>
      </div>
      <span id="distritos-erro" class="erro-campo" style="display:none">Selecione um distrito</span>

      <div class="item-animado atraso-5">
        <label class="permanecer">
          <input type="checkbox" id="termos" name="termosAceitos" value="1" required />
          Aceito os <a href="#" target="_blank">termos de uso</a>
        </label>

        <button type="submit" class="btn-login">Criar conta da loja</button>

        <div class="links-rodape">
          <a href="/cadastro/produtora-arace" class="voltar">Voltar</a>
        </div>
      </div>

    </form>
  </div>

  <script src="https://unpkg.com/lucide@latest"></script>
  <script>lucide.createIcons();</script>
  <script src="<?= base_url('js/cadastro-loja.js') ?>"></script>
</body>
</html>
