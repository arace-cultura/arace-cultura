const CONFIG_PLACEHOLDER = {
  perfil: {
    nome: 'Maria',
    sobrenome: 'Silva',
    username: '@mariasilva',
    bio: 'Amante da cultura capixaba',
    nascimento: '1995-06-12',
    genero: 'f',
    email: 'maria@email.com',
    telefone: '',
  },
  endereco: {
    cidade: 'Vitoria',
    estado: 'ES',
  },
};

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
}

async function carregarConfiguracoes() {
  // Firestore/API: buscar o documento do usuario autenticado aqui.
  // Campos esperados: {{nome_usuario}}, {{email_usuario}}, {{telefone_usuario}}, {{endereco_usuario}}.
  renderPerfilConfig(CONFIG_PLACEHOLDER.perfil);
}

function trocarAba(btn, id) {
  document.querySelectorAll('.config-nav-item').forEach(item => item.classList.remove('active'));
  document.querySelectorAll('.config-section').forEach(secao => secao.classList.remove('active'));

  btn.classList.add('active');
  const secao = document.getElementById('sec-' + id);
  if (secao) secao.classList.add('active');

  lucide.createIcons();
}

function salvar(msg) {
  const toast = document.getElementById('toast');
  const toastMsg = document.getElementById('toastMsg');

  if (!toast || !toastMsg) return;

  toastMsg.textContent = msg || 'Salvo com sucesso';
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2800);
}

function previewAvatar(input) {
  if (!input.files || !input.files[0]) return;

  const reader = new FileReader();
  reader.onload = event => {
    const preview = document.getElementById('avatarPreview');
    if (preview) preview.innerHTML = `<img src="${event.target.result}" alt="Avatar do usuario" />`;
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
