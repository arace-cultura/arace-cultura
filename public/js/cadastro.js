function alternarSenha(campoId, iconeId) {
  const campo = document.getElementById(campoId);
  const icone = document.getElementById(iconeId);

  if (!campo || !icone) return;

  campo.type = campo.type === 'password' ? 'text' : 'password';
  icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
  lucide.createIcons();
}
