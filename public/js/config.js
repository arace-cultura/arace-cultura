// Controla abas, previews e preenchimento dos formularios de configuracao.
function preencherCampo(id, valor) {
  const campo = document.getElementById(id);
  if (campo) campo.value = valor ?? '';
}

function renderPerfilConfig(dados) {
  preencherCampo('nome', dados.nome);
  preencherCampo('username', dados.username);
  preencherCampo('bio', dados.bio);
  preencherCampo('nascimento', dados.nascimento);
  preencherCampo('sexo', dados.sexo || dados.genero);
  preencherCampo('email', dados.email);
  preencherCampo('tel', dados.telefone);

  const preview = document.getElementById('avatarPreview');
  const fotoUrl = dados.fotoUrl || dados.avatar;
  if (preview && fotoUrl) {
    renderAvatarImage(preview, fotoUrl);
  }
}

function renderAvatarImage(container, src) {
  container.replaceChildren();

  const img = document.createElement('img');
  img.src = src;
  img.alt = 'Avatar do usuario';
  container.appendChild(img);
}

async function carregarConfiguracoes() {
  if (window.ARACE_AUTH_USER) renderPerfilConfig(window.ARACE_AUTH_USER);
  renderLojaConfig();
}

function renderLojaConfig() {
  if (document.querySelector('form[action*="configuracao-loja"]')) return;
  if (!window.AraceState) return;
  const loja = window.AraceState.getProducer();
  preencherCampo('lojaNome', loja.lojaNome);
  preencherCampo('lojaBio', loja.lojaBio);
  preencherCampo('lojaCategoria', loja.lojaCategoria);
  preencherCampo('lojaEmail', loja.lojaEmail);
  preencherCampo('lojaTelefone', loja.lojaTelefone);
  preencherCampo('lojaCidade', `${loja.lojaCidade} - ${loja.lojaEstado}`);

  const preview = document.getElementById('avatarPreview');
  if (preview && document.getElementById('lojaNome')) {
    window.AraceState.renderAvatar(preview, loja.lojaAvatar, 'store');
  }
}

function trocarAba(btn, id) {
  document.querySelectorAll('.config-nav-item').forEach(item => item.classList.remove('active'));
  document.querySelectorAll('.config-section').forEach(secao => secao.classList.remove('active'));

  btn.classList.add('active');
  const secao = document.getElementById('sec-' + id);
  if (secao) secao.classList.add('active');

  lucide.createIcons();
}

async function salvar(msg) {
  mostrarToast(msg || 'Salvo com sucesso');
}

function mostrarToast(msg) {
  const toast = document.getElementById('toast');
  const toastMsg = document.getElementById('toastMsg');

  if (!toast || !toastMsg) return;

  toastMsg.textContent = msg;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2800);
}

// Aplica a mesma imagem no preview grande e, opcionalmente, em todos os
// botoes de avatar do header (mantendo cada icone de avatar sincronizado).
function aplicarAvatar(src, incluirHeader) {
  const preview = document.getElementById('avatarPreview');
  if (preview) renderAvatarImage(preview, src);

  if (incluirHeader) {
    document.querySelectorAll('.avatar-btn').forEach(el => renderAvatarImage(el, src));
  }
}

// Reseta o preview e, se pedido, os avatares do header para o icone padrao.
function resetarAvatar(incluirHeader) {
  const alvos = incluirHeader
    ? [document.getElementById('avatarPreview'), ...document.querySelectorAll('.avatar-btn')]
    : [document.getElementById('avatarPreview')];

  alvos.forEach(el => {
    if (el) el.innerHTML = '<i data-lucide="user"></i>';
  });
  lucide.createIcons();
}

// O logo da loja nao deve trocar o avatar pessoal exibido no header.
function ehLogoDaLoja(input) {
  const acao = input?.closest('form')?.getAttribute('action') || '';
  return acao.includes('configuracao-loja');
}

function previewAvatar(input) {
  if (!input.files || !input.files[0]) return;

  const sincronizarHeader = !ehLogoDaLoja(input);
  const reader = new FileReader();
  reader.onload = event => aplicarAvatar(event.target.result, sincronizarHeader);
  reader.readAsDataURL(input.files[0]);
}

function removerAvatar(input) {
  resetarAvatar(!ehLogoDaLoja(input));
}

function selecionarTema(el) {
  document.querySelectorAll('.theme-option').forEach(item => item.classList.remove('active'));
  el.classList.add('active');

  const tema = el.querySelector('.theme-label')?.textContent?.trim().toLowerCase();
  if (tema && window.AraceState?.setTheme) {
    window.AraceState.setTheme(tema === 'escuro' ? 'escuro' : tema === 'sistema' ? 'sistema' : 'claro');
  }
}

window.trocarAba = trocarAba;
window.salvar = salvar;
window.previewAvatar = previewAvatar;
window.removerAvatar = removerAvatar;
window.selecionarTema = selecionarTema;

document.addEventListener('DOMContentLoaded', () => {
  carregarConfiguracoes();
  window.AraceBrasilApiValidation?.configurarTelefone(document.getElementById('tel'), null);

  const abaInicial = window.location.hash.replace('#', '');
  if (abaInicial) {
    const botao = document.querySelector(`.config-nav-item[onclick*="'${abaInicial}'"]`);
    if (botao) trocarAba(botao, abaInicial);
  }
  lucide.createIcons();
});
