﻿﻿<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aracê - Cadastro Produtor</title>
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

    <p class="subtitle item-animado atraso-1">Comece a vender seus produtos</p>

    <form id="formCadastro" novalidate>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="store"></i> <input type="text" id="nome" placeholder="Nome da loja" required />
      </div>

      <div class="input-group-custom item-animado atraso-2">
        <i data-lucide="file-text"></i>
        <input type="text" id="cnpj" placeholder="CNPJ (opcional)" />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="mail"></i>
        <input type="email" id="email" placeholder="E-mail comercial" required />
      </div>

      <div class="input-group-custom item-animado atraso-3">
        <i data-lucide="phone"></i>
        <input type="tel" id="telefone" placeholder="Telefone / WhatsApp comercial" required />
      </div>

      <div class="input-group-custom item-animado atraso-4">
        <i data-lucide="tags"></i>
        <select id="categoria" required>
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
        <select id="distritos" required>
          <option value="" disabled selected>Distrito da loja</option>
        </select>
      </div>
      <span id="distritos-erro" class="erro-campo" style="display:none">Selecione um distrito</span>

      <div class="item-animado atraso-5">
        <label class="permanecer">
          <input type="checkbox" id="termos" required />
          Aceito os <a href="#" target="_blank">termos de uso</a>
        </label>

        <button type="submit" class="btn-login">Criar conta da loja</button>

        <div class="links-rodape">
          <a href="<?= url_to('auth_cadastro_producter_arace') ?>" class="voltar">Voltar</a>
        </div>
      </div>

    </form>
  </div>

  <script src="https://unpkg.com/lucide@latest"></script>
  <script>lucide.createIcons();</script>
  <script src="/js/cadastro-loja.js"></script>
</body>
</html>
