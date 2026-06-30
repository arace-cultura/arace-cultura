// Regras de tela para cadastro do dono/produtor: mascara CPF e valida telefone/CPF.

if (window.lucide) lucide.createIcons();

function validarCPF(cpf) {
  return window.AraceBrasilApiValidation?.validarCpf(cpf) ?? false;
}

function formatarCPF(input) {
  input.value = window.AraceBrasilApiValidation?.formatarCpfValor(input.value) ?? input.value;
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
  const telefone = document.getElementById('telefone');
  const erroTelefone = document.getElementById('telefone-erro');
  const validador = window.AraceBrasilApiValidation;

  if (!form || !cpf) return;

  validador?.configurarTelefone(telefone, erroTelefone);

  cpf.addEventListener('input', () => formatarCPF(cpf));
  cpf.addEventListener('focus', () => mostrarErroCPF(false));
  cpf.addEventListener('blur', () => {
    if (cpf.value) mostrarErroCPF(!validarCPF(cpf.value));
  });

  form.addEventListener('submit', async event => {
    if (form.dataset.validadoBrasilApi === '1') {
      delete form.dataset.validadoBrasilApi;
      return;
    }

    event.preventDefault();

    if (!validarCPF(cpf.value)) {
      mostrarErroCPF(true);
      cpf.focus();
      return;
    }

    form.dataset.validadoBrasilApi = '1';
    form.requestSubmit();
  });
});
