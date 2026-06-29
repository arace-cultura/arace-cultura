function preencherCampo(id, valor) {
  const campo = document.getElementById(id);
  if (campo) campo.value = valor ?? '';
}

function renderPerfilConfig(dados) {
  preencherCampo('nome', dados.nome);
  preencherCampo('sobrenome', dados.sobrenome);
  preencherCampo('username', dados.username);
  preencherCampo('bio', dados.bio);
  preencherCampo('nascimento', dados.nascimento);
  preencherCampo('genero', dados.genero);
  preencherCampo('email', dados.email);
  preencherCampo('tel', dados.telefone);
  preencherCampo('cidade', dados.cidade);
  preencherCampo('estado', dados.estado);

  const preview = document.getElementById('avatarPreview');
  if (preview && dados.avatar) preview.innerHTML = `<img src="${dados.avatar}" alt="Avatar do usuario" />`;
}

async function carregarConfiguracoes() {
  if (window.ARACE_AUTH_USER) renderPerfilConfig(window.ARACE_AUTH_USER);
  renderLojaConfig();
}

function renderLojaConfig() {
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

function coletarPerfilConfig() {
  return {
    nome: document.getElementById('nome')?.value.trim() || '',
    sobrenome: document.getElementById('sobrenome')?.value.trim() || '',
    username: document.getElementById('username')?.value.trim() || '',
    bio: document.getElementById('bio')?.value.trim() || '',
    nascimento: document.getElementById('nascimento')?.value || '',
    genero: document.getElementById('genero')?.value || '',
    email: document.getElementById('email')?.value.trim() || '',
    telefone: document.getElementById('tel')?.value.trim() || '',
    cidade: document.getElementById('cidade')?.value.trim() || '',
    estado: document.getElementById('estado')?.value || '',
  };
}

function coletarLojaConfig() {
  return {
    cadastrado: true,
    lojaNome: document.getElementById('lojaNome')?.value.trim() || 'Minha loja Arace',
    lojaBio: document.getElementById('lojaBio')?.value.trim() || '',
    lojaCategoria: document.getElementById('lojaCategoria')?.value || 'artesanato',
    lojaEmail: document.getElementById('lojaEmail')?.value.trim() || '',
    lojaTelefone: document.getElementById('lojaTelefone')?.value.trim() || '',
    lojaCidade: (document.getElementById('lojaCidade')?.value || 'Vitoria - ES').split('-')[0].trim(),
    lojaEstado: (document.getElementById('lojaCidade')?.value || 'Vitoria - ES').split('-')[1]?.trim() || 'ES',
  };
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

function previewAvatar(input) {
  if (!input.files || !input.files[0]) return;

  const reader = new FileReader();
  reader.onload = event => {
    const preview = document.getElementById('avatarPreview');
    const image = event.target.result;
    if (preview) preview.innerHTML = `<img src="${image}" alt="Avatar do usuario" />`;
  };
  reader.readAsDataURL(input.files[0]);
}

function removerAvatar() {
  const preview = document.getElementById('avatarPreview');
  if (!preview) return;

  preview.innerHTML = '<i data-lucide="user"></i>';
  lucide.createIcons();
}

function mascaraCEP(el) {
  let valor = el.value.replace(/\D/g, '').slice(0, 8);
  if (valor.length > 5) valor = valor.slice(0, 5) + '-' + valor.slice(5);
  el.value = valor;

  if (valor.length === 9) buscarCEP(valor.replace('-', ''));
}

async function buscarCEP(cep) {
  try {
    const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const dados = await resposta.json();

    if (!dados.erro) {
      preencherCampo('cidade', dados.localidade || '');
      preencherCampo('estado', dados.uf || '');
    }
  } catch (erro) {
    salvar('Nao foi possivel buscar o CEP agora');
  }
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
window.mascaraCEP = mascaraCEP;
window.selecionarTema = selecionarTema;

document.addEventListener('DOMContentLoaded', () => {
  carregarConfiguracoes();
  const abaInicial = window.location.hash.replace('#', '');
  if (abaInicial) {
    const botao = document.querySelector(`.config-nav-item[onclick*="'${abaInicial}'"]`);
    if (botao) trocarAba(botao, abaInicial);
  }
  lucide.createIcons();
});
