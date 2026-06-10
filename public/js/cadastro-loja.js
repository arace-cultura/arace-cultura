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

  select.innerHTML = '<option value="" disabled selected>Distrito da loja</option>';

  distritos
    .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
    .forEach(distrito => {
      const option = document.createElement('option');
      option.value = distrito.id;
      option.textContent = distrito.nome;
      select.appendChild(option);
    });
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
    event.preventDefault();

    const loja = {
      nome: document.getElementById('nome')?.value.trim() || '',
      cnpj: document.getElementById('cnpj')?.value.trim() || '',
      email: document.getElementById('email')?.value.trim() || '',
      telefone: document.getElementById('telefone')?.value.trim() || '',
      categoria: document.getElementById('categoria')?.value || '',
      distritoId: distritos.value,
      distritoNome: distritos.options[distritos.selectedIndex]?.textContent || '',
      termosAceitos: document.getElementById('termos')?.checked || false,
    };

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    if (!loja.distritoId) {
      mostrarErro('distritos-erro', true);
      return;
    }

    if (!loja.termosAceitos) {
      alert('Voce precisa aceitar os termos de uso.');
      return;
    }

    localStorage.setItem('arace:auth', JSON.stringify({ loggedIn: true, email: loja.email }));
    localStorage.setItem('arace:producer', JSON.stringify({
      cadastrado: true,
      lojaNome: loja.nome,
      lojaBio: '',
      lojaCategoria: loja.categoria,
      lojaCidade: loja.distritoNome,
      lojaEstado: 'ES',
      lojaTelefone: loja.telefone,
      lojaEmail: loja.email,
      lojaAvatar: '',
      lojaBanner: '/images/bahia-vitoria.jpg',
      cnpj: loja.cnpj,
    }));
    localStorage.setItem('arace:viewMode', 'produtor');

    window.location.href = '/produtor/perfil-loja';
  });
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();
  carregarDistritos();
  configurarFormularioLoja();
});
