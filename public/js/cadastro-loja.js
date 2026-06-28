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

  if (!form || !distritos) return;

  distritos.addEventListener('blur', () => {
    mostrarErro('distritos-erro', !distritos.value);
  });

  form.addEventListener('submit', event => {
    if (!distritos.value) {
      event.preventDefault();
      mostrarErro('distritos-erro', true);
      distritos.focus();
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();
  carregarDistritos();
  configurarFormularioLoja();
});
