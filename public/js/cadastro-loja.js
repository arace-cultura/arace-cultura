const DISTRITOS_FALLBACK = [
  { id: 'vitoria', nome: 'Vitoria' },
  { id: 'vila-velha', nome: 'Vila Velha' },
  { id: 'serra', nome: 'Serra' },
  { id: 'cariacica', nome: 'Cariacica' },
  { id: 'guarapari', nome: 'Guarapari' },
];

function mostrarErro(id, mostrar) {
  const erro = document.getElementById(id);
  if (erro) erro.style.display = mostrar ? 'block' : 'none';
}

function preencherDistritos(distritos) {
  const select = document.getElementById('distritos');
  if (!select) return;

  const valorAtual = select.value;
  select.innerHTML = '<option value="" disabled selected>Distrito da loja</option>';

  distritos
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
    .forEach(distrito => {
      const option = document.createElement('option');
      option.value = distrito.id;
      option.textContent = distrito.nome;
      select.appendChild(option);
    });

  if (valorAtual) select.value = valorAtual;
}

async function carregarDistritos() {
  try {
    const resposta = await fetch('https://servicodados.ibge.gov.br/api/v1/localidades/estados/es/distritos');
    const distritos = await resposta.json();
    preencherDistritos(distritos);
  } catch {
    preencherDistritos(DISTRITOS_FALLBACK);
  }
}

function configurarFormularioLoja() {
  const form = document.getElementById('formCadastro');
  const distritos = document.getElementById('distritos');
  const telefone = document.getElementById('telefone');
  const telefoneErro = document.getElementById('telefone-erro');
  const cnpj = document.getElementById('cnpj');
  const cnpjErro = document.getElementById('cnpj-erro');
  const validador = window.AraceBrasilApiValidation;

  if (!form || !distritos) return;

  validador?.configurarTelefone(telefone, telefoneErro);
  validador?.configurarCnpj(cnpj, cnpjErro);

  distritos.addEventListener('blur', () => {
    mostrarErro('distritos-erro', !distritos.value);
  });

  form.addEventListener('submit', async event => {
    if (form.dataset.validadoBrasilApi === '1') {
      delete form.dataset.validadoBrasilApi;
      return;
    }

    event.preventDefault();

    if (!distritos.value) {
      mostrarErro('distritos-erro', true);
      distritos.focus();
      return;
    }

    form.dataset.validadoBrasilApi = '1';
    form.requestSubmit();
  });
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();
  carregarDistritos();
  configurarFormularioLoja();
});
