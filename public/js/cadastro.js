if (window.lucide) lucide.createIcons();

function alternarSenha(campoId, iconeId) {
  const campo = document.getElementById(campoId);
  const icone = document.getElementById(iconeId);

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
  const form = document.getElementById('formCadastro');
  if (!form) return;

  form.addEventListener('submit', event => {
    event.preventDefault();

    const nome = document.getElementById('nome')?.value.trim() || '';
    const email = document.getElementById('email')?.value.trim() || '';
    const telefone = document.getElementById('telefone')?.value.trim() || '';
    const senha = document.getElementById('senha')?.value || '';
    const confirmarSenha = document.getElementById('confirmarSenha')?.value || '';
    const termos = document.getElementById('termos')?.checked || false;
    const erroSenha = document.getElementById('erro-senha');
    const botao = form.querySelector('.btn-login');

    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }

    if (!nome || !email || !senha || !confirmarSenha) {
      alert('Preencha nome, e-mail e senha para criar sua conta.');
      return;
    }

    if (senha !== confirmarSenha) {
      if (erroSenha) erroSenha.style.display = 'block';
      alert('As senhas precisam ser iguais.');
      return;
    }

    if (erroSenha) erroSenha.style.display = 'none';

    if (!termos) {
      alert('Voce precisa aceitar os termos de uso.');
      return;
    }

    if (botao?.disabled) return;
    if (botao) {
      botao.disabled = true;
      botao.textContent = 'Criando conta...';
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
      cpf: '',
    }));

    window.location.href = '/usuario/arace-perfil';
  });
});
