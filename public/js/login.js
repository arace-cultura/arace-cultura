function alternarSenha() {
  const campo = document.getElementById('senha');
  const icone = document.getElementById('icone-olho');

  if (!campo || !icone) return;

  campo.type = campo.type === 'password' ? 'text' : 'password';
  icone.setAttribute('data-lucide', campo.type === 'password' ? 'eye' : 'eye-off');
  if (window.lucide) lucide.createIcons();
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) lucide.createIcons();
});
