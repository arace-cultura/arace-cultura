const DISTRITOS_FALLBACK = [
  { id: 'vitoria', nome: 'Vitoria' },
  { id: 'vila-velha', nome: 'Vila Velha' },
  { id: 'serra', nome: 'Serra' },
  { id: 'cariacica', nome: 'Cariacica' },
  { id: 'guarapari', nome: 'Guarapari' },
];

function validarCPF(cpf) {
  const numeros = cpf.replace(/\D/g, '');
  if (numeros.length !== 11 || /^(\d)\1+$/.test(numeros)) return false;

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += Number(numeros[i]) * (10 - i);
  let digito1 = (soma * 10) % 11;
  if (digito1 === 10 || digito1 === 11) digito1 = 0;
  if (digito1 !== Number(numeros[9])) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += Number(numeros[i]) * (11 - i);
  let digito2 = (soma * 10) % 11;
  if (digito2 === 10 || digito2 === 11) digito2 = 0;

  return digito2 === Number(numeros[10]);
}

function formatarCPF(input) {
  let valor = input.value.replace(/\D/g, '').slice(0, 11);

  if (valor.length > 9) {
    valor = valor.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
  } else if (valor.length > 6) {
    valor = valor.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
  } else if (valor.length > 3) {
    valor = valor.replace(/(\d{3})(\d{0,3})/, '$1.$2');
  }

  input.value = valor;
}

function mostrarErro(id, mostrar) {
  const erro = document.getElementById(id);
  if (erro) erro.style.display = mostrar ? 'block' : 'none';
}

function preencherDistritos(distritos) {
  const select = document.getElementById('distritos');
  if (!select) return;

  select.innerHTML = '<option value="" disabled selected>Selecione um distrito</option>';

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
  } catch (erro) {
    preencherDistritos(DISTRITOS_FALLBACK);
  }
}

function configurarFormularioProdutor() {
  const form = document.getElementById('formCadastro');
  const campoCPF = document.getElementById('cpf');
  const distritos = document.getElementById('distritos');

  if (!form || !campoCPF || !distritos) return;

  campoCPF.addEventListener('input', () => formatarCPF(campoCPF));

  campoCPF.addEventListener('blur', () => {
    const invalido = campoCPF.value && !validarCPF(campoCPF.value);
    campoCPF.style.borderColor = invalido ? '#dc2626' : '';
    mostrarErro('cpf-erro', invalido);
  });

  campoCPF.addEventListener('focus', () => {
    campoCPF.style.borderColor = '';
    mostrarErro('cpf-erro', false);
  });

  distritos.addEventListener('blur', () => {
    mostrarErro('distritos-erro', !distritos.value);
  });

  form.addEventListener('submit', async event => {
    event.preventDefault();

    const produtor = {
      nomeLoja: document.getElementById('nome').value.trim(),
      email: document.getElementById('email').value.trim(),
      telefone: document.getElementById('telefone').value.trim(),
      cpf: campoCPF.value.trim(),
      distritoId: distritos.value,
      termosAceitos: document.getElementById('termos').checked,
    };

    if (!validarCPF(produtor.cpf)) {
      campoCPF.style.borderColor = '#dc2626';
      mostrarErro('cpf-erro', true);
      return;
    }

    if (!produtor.distritoId) {
      mostrarErro('distritos-erro', true);
      return;
    }

    if (!produtor.termosAceitos) {
      alert('Voce precisa aceitar os termos de uso.');
      return;
    }

    // Firebase/Firestore: criar usuario/loja aqui.
    // Campos sugeridos: {{nome_loja}}, {{email_comercial}}, {{telefone_comercial}}, {{cpf_produtor}}, {{distrito_id}}.
    alert(`Cadastro de produtor iniciado para: ${produtor.nomeLoja}`);
  });
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();
  carregarDistritos();
  configurarFormularioProdutor();
});
