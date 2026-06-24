function alternarSenha() {
  const campo = document.getElementById('senha');
  const icone = document.getElementById('icone-olho');

  campo.type = campo.type === 'password' ? 'text' : 'password';
  icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
  lucide.createIcons();
}
