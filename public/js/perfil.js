// Monta a pagina de perfil com dados reais da sessao e das APIs do Firestore.
// Quando alguma API nao responde, a tela mostra zero em vez de usar numeros falsos.
function textoOuPendente(valor) {
  return valor || '<i data-lucide="alert-circle"></i> Nao informado';
}

async function buscarTotalApi(caminho, extrairTotal) {
  try {
    const resposta = await fetch(caminho, { headers: { Accept: 'application/json' } });
    if (!resposta.ok) return 0;

    const payload = await resposta.json();
    return extrairTotal(payload);
  } catch {
    return 0;
  }
}

async function carregarPerfil() {
  const user = window.ARACE_AUTH_USER || {};
  const favoritos = await buscarTotalApi('/api/favorites', payload => Array.isArray(payload.data) ? payload.data.length : 0);
  const carrinho = await buscarTotalApi('/api/cart', payload => {
    const items = payload.data?.items || [];
    return items.reduce((total, item) => total + Number(item.quantidade || 1), 0);
  });
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
    pedidos: Number(user.pedidos || 0),
    carrinho,
    favoritos,
  };
}

// Atualiza os cards e campos do perfil sem alterar os dados no servidor.
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
