function alternarSenha(campoId, iconeId) {
  const campo = document.getElementById(campoId);
  const icone = document.getElementById(iconeId);

  if (!campo || !icone) return;

  campo.type = campo.type === 'password' ? 'text' : 'password';
  icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
  if (window.lucide) lucide.createIcons();
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();

  const form = document.getElementById('formCadastro');
  const senha = document.getElementById('senha');
  const confirmarSenha = document.getElementById('confirmarSenha');
  const erroSenha = document.getElementById('erro-senha');

  if (!form || !senha || !confirmarSenha) return;

  form.addEventListener('submit', event => {
    if (senha.value !== confirmarSenha.value) {
      event.preventDefault();
      if (erroSenha) erroSenha.style.display = 'block';
      confirmarSenha.focus();
      return;
    }

    if (erroSenha) erroSenha.style.display = 'none';
  });
});
