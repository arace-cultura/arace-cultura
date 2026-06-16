function alternarSenha(campoId, iconeId) {
  const campo = document.getElementById(campoId);
  const icone = document.getElementById(iconeId);

  if (!campo || !icone) return;

  campo.type = campo.type === 'password' ? 'text' : 'password';
  icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
  lucide.createIcons();
}

document.getElementById('formCadastro')?.addEventListener('submit', event => {
  const senha = document.getElementById('senha');
  const confirmarSenha = document.getElementById('confirmarSenha');
  const erroSenha = document.getElementById('erro-senha');

  if (!senha || !confirmarSenha || senha.value === confirmarSenha.value) {
    return;
  }

  event.preventDefault();
  if (erroSenha) erroSenha.style.display = 'block';
});
