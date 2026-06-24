if (window.lucide) lucide.createIcons();

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

function mostrarErroCPF(mostrar) {
  const erro = document.getElementById('cpf-erro');
  const campo = document.getElementById('cpf');

  if (erro) erro.style.display = mostrar ? 'block' : 'none';
  if (campo) campo.style.borderColor = mostrar ? '#dc2626' : '';
}

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('formCadastroDono');
  const cpf = document.getElementById('cpf');

  if (!form || !cpf) return;

  cpf.addEventListener('input', () => formatarCPF(cpf));
  cpf.addEventListener('focus', () => mostrarErroCPF(false));
  cpf.addEventListener('blur', () => {
    if (cpf.value) mostrarErroCPF(!validarCPF(cpf.value));
  });

  form.addEventListener('submit', event => {
    event.preventDefault();

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    if (!validarCPF(cpf.value)) {
      mostrarErroCPF(true);
      return;
    }

    const email = document.getElementById('email')?.value.trim() || '';
    const nome = document.getElementById('nome-dono')?.value.trim() || '';
    const telefone = document.getElementById('telefone')?.value.trim() || '';
    const termos = document.getElementById('termos')?.checked || false;

    if (!termos) {
      alert('Voce precisa aceitar os termos de uso.');
      return;
    }

    const [primeiroNome, ...sobrenomes] = nome.split(/\s+/);
    localStorage.setItem('arace:auth', JSON.stringify({ loggedIn: true, email }));
    localStorage.setItem('arace:user', JSON.stringify({
      nome: primeiroNome || nome,
      sobrenome: sobrenomes.join(' '),
      username: `@${email.split('@')[0]}`,
      email,
      telefone,
      cidade: '',
      estado: 'ES',
      avatar: '',
      membroDesde: 'Junho de 2026',
      cpf: cpf.value,
    }));

    window.location.href = '/cadastro/produtor-loja';
  });
});
