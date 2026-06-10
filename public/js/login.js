if (window.lucide) lucide.createIcons();

function alternarSenha() {
  const campo = document.getElementById('senha');
  const icone = document.getElementById('icone-olho');

  if (!campo || !icone) return;

  if (campo.type === 'password') {
    campo.type = 'text';
    icone.setAttribute('data-lucide', 'eye-off');
  } else {
    campo.type = 'password';
    icone.setAttribute('data-lucide', 'eye');
  }

  if (window.lucide) lucide.createIcons();
}

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('formLogin');
  if (!form) return;

  form.addEventListener('submit', event => {
    event.preventDefault();

    const email = document.getElementById('email')?.value.trim() || '';
    const senha = document.getElementById('senha')?.value || '';
    const botao = form.querySelector('.btn-login');

    if (!email || !senha) {
      alert('Informe e-mail e senha para entrar.');
      return;
    }

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    if (botao?.disabled) return;
    if (botao) {
      botao.disabled = true;
      botao.textContent = 'Entrando...';
    }

    const [nomeEmail] = email.split('@');
    const nomeFormatado = nomeEmail
      .split(/[._-]/)
      .filter(Boolean)
      .map(parte => parte.charAt(0).toUpperCase() + parte.slice(1))
      .join(' ') || 'Usuario';

    localStorage.setItem('arace:auth', JSON.stringify({ loggedIn: true, email }));
    localStorage.setItem('arace:user', JSON.stringify({
      nome: nomeFormatado,
      sobrenome: '',
      username: `@${nomeEmail || 'usuario'}`,
      email,
      telefone: '',
      cidade: '',
      estado: 'ES',
      avatar: '',
      membroDesde: 'Junho de 2026',
      cpf: '',
    }));

    window.location.href = '/usuario/arace-perfil';
  });
});
