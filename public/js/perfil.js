const PERFIL_PLACEHOLDER = {
  nome: 'Usuario',
  email: 'usuario@gmail.com',
  telefone: '',
  localizacao: 'Cariacica - ES',
  membroDesde: 'Janeiro de 2024',
  cpf: '',
  pedidos: 12,
  carrinho: 2,
  favoritos: 5,
};

function textoOuPendente(valor) {
  return valor || '<i data-lucide="alert-circle"></i> Nao informado';
}

async function carregarPerfil() {
  // Firestore/API: buscar dados do usuario autenticado aqui.
  // Campos esperados: {{nome_usuario}}, {{email_usuario}}, {{telefone_usuario}}, {{cidade_usuario}}.
  return PERFIL_PLACEHOLDER;
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

  const campos = document.querySelectorAll('.field-value');
  if (campos[0]) campos[0].innerHTML = textoOuPendente(dados.nome);
  if (campos[1]) campos[1].innerHTML = textoOuPendente(dados.email);
  if (campos[2]) campos[2].innerHTML = textoOuPendente(dados.telefone);
  if (campos[3]) campos[3].innerHTML = textoOuPendente(dados.localizacao);
  if (campos[4]) campos[4].innerHTML = textoOuPendente(dados.membroDesde);
  if (campos[5]) campos[5].innerHTML = textoOuPendente(dados.cpf);

  lucide.createIcons();
}

document.addEventListener('DOMContentLoaded', async () => {
  const dados = await carregarPerfil();
  renderPerfil(dados);
});
