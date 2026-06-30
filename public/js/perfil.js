function textoOuPendente(valor) {
  return valor || '<i data-lucide="alert-circle"></i> Nao informado';
}

async function carregarPerfil() {
  const user = window.ARACE_AUTH_USER || {};
  const favoritos = Number(user.favoritos || 0);
  const sexo = user.sexo || user.genero || '';
  const sexoLabel = {
    f: 'Feminino',
    m: 'Masculino',
    nb: 'Nao-binario',
  }[sexo] || '';

  return {
    nome: user.nome || 'Usuario',
    username: user.username || '',
    sexo: sexoLabel,
    email: user.email || '',
    telefone: user.telefone || '',
    nascimento: user.nascimento || '',
    bio: user.bio || '',
    avatar: user.fotoUrl || user.avatar || '',
    pedidos: 12,
    carrinho: 2,
    favoritos,
  };
}

function renderPerfil(dados) {
  const stats = document.querySelectorAll('.stat-value');
  if (stats[0]) stats[0].textContent = dados.pedidos;
  if (stats[1]) stats[1].textContent = dados.carrinho;
  if (stats[2]) stats[2].textContent = dados.favoritos;

  const nome = document.querySelector('.profile-name');
  const email = document.querySelector('.profile-email');
  if (nome) nome.textContent = dados.nome;
  if (email) email.textContent = dados.email;

  const avatar = document.querySelector('.profile-card .avatar');
  if (avatar && dados.avatar) avatar.innerHTML = `<img src="${dados.avatar}" alt="Avatar do usuario" />`;

  const campos = document.querySelectorAll('.field-value');
  if (campos[0]) campos[0].innerHTML = textoOuPendente(dados.nome);
  if (campos[1]) campos[1].innerHTML = textoOuPendente(dados.username);
  if (campos[2]) campos[2].innerHTML = textoOuPendente(dados.sexo);
  if (campos[3]) campos[3].innerHTML = textoOuPendente(dados.email);
  if (campos[4]) campos[4].innerHTML = textoOuPendente(dados.telefone);
  if (campos[5]) campos[5].innerHTML = textoOuPendente(dados.nascimento);
  if (campos[6]) campos[6].innerHTML = textoOuPendente(dados.bio);

  lucide.createIcons();
}

document.addEventListener('DOMContentLoaded', async () => {
  const dados = await carregarPerfil();
  renderPerfil(dados);
});
