// Renderiza favoritos vindos do Firestore e envia remocoes/adicoes ao carrinho pela API.

let favoritos = Array.isArray(window.ARACE_FAVORITES)
  ? window.ARACE_FAVORITES
  : (window.AraceState ? window.AraceState.getFavorites() : []);
let filtroAtivo = 'todos';
let colecaoAtiva = 'todos';
let buscaAtiva = '';
const araceUrl = path => window.AraceState?.url(path) || path;

function stars(n) {
  return [1, 2, 3, 4, 5].map(i =>
    `<i data-lucide="star" style="${i <= Math.round(n) ? 'fill:var(--amarelo)' : 'fill:none;color:var(--border)'}"></i>`
  ).join('');
}

function showToast(msg) {
  let toast = document.getElementById('_toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = '_toast';
    toast.style.cssText = 'position:fixed;bottom:1.5rem;right:1.5rem;background:var(--text);color:#fff;padding:10px 18px;border-radius:10px;font-size:14px;font-family:var(--body);box-shadow:0 4px 16px rgba(0,0,0,.18);z-index:999;transition:opacity .3s';
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.style.opacity = '1';
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => { toast.style.opacity = '0'; }, 2500);
}

function atualizarContador() {
  const total = favoritos.length;
  const headerCount = document.getElementById('headerCount');
  const favCount = document.getElementById('favCount');
  if (headerCount) headerCount.textContent = total;
  if (favCount) favCount.textContent = `${total} itens`;
  if (window.AraceState) window.AraceState.syncHeader();
}

function renderCards(lista) {
  const grid = document.getElementById('favGrid');
  if (!grid) return;

  if (!lista.length) {
    grid.innerHTML = `
      <div class="fav-empty">
        <i data-lucide="heart"></i>
        <h2>Nenhum favorito aqui</h2>
        <p>Explore os produtos e salve os que voce curtir</p>
        <a href="${araceUrl('arace-produtos')}" class="btn-explorar">
          <i data-lucide="shopping-bag"></i> Explorar produtos
        </a>
      </div>`;
    atualizarContador();
    lucide.createIcons();
    return;
  }

  grid.innerHTML = lista.map(p => `
    <div class="fav-card" data-id="${p.id}" data-colecao="${p.colecao}">
      <div class="fav-card-img">
        ${p.img ? `<img src="${p.img}" alt="${p.nome}" loading="lazy" />` : `<div class="fav-card-color" style="background:${p.cor || '#b5a898'}"></div>`}
        ${p.desconto ? `<span class="fav-badge-off">-${p.desconto}%</span>` : ''}
        ${!p.disponivel ? '<div class="fav-unavailable">Indisponivel</div>' : ''}
        <div class="fav-card-actions">
          <button class="card-action-btn" title="Mover para colecao" onclick="moverColecao('${p.id}')"><i data-lucide="folder-plus"></i></button>
          <button class="card-action-btn" title="Compartilhar" onclick="compartilhar('${p.id}')"><i data-lucide="share-2"></i></button>
          <button class="card-action-btn remove" title="Remover dos favoritos" onclick="removerFavorito('${p.id}')"><i data-lucide="heart-off"></i></button>
        </div>
      </div>
      <div class="fav-card-body">
        <div class="fav-card-info">
          <div class="fav-card-artesao">${p.artesao}</div>
          <div class="fav-card-name">${p.nome}</div>
          <div class="fav-card-rating">${stars(p.estrelas)} ${p.estrelas} (${p.avaliacoes})</div>
          <div class="fav-card-price">
            <strong>R$${Number(p.preco).toLocaleString('pt-BR')}</strong>
            ${p.precoAntigo ? `<s>R$${Number(p.precoAntigo).toLocaleString('pt-BR')}</s><span class="badge-desc">-${p.desconto}%</span>` : ''}
          </div>
        </div>
        <button class="btn-add-cart" ${!p.disponivel ? 'disabled style="opacity:.5;cursor:not-allowed"' : ''} onclick="addCarrinho('${p.id}')">
          <i data-lucide="shopping-cart"></i>
          ${p.disponivel ? 'Adicionar ao carrinho' : 'Indisponivel'}
        </button>
      </div>
    </div>`).join('');

  atualizarContador();
  lucide.createIcons();
}

function getListaFiltrada() {
  return favoritos.filter(p => {
    const matchColecao = colecaoAtiva === 'todos' || p.colecao === colecaoAtiva;
    const matchFiltro = filtroAtivo === 'todos'
      || (filtroAtivo === 'disponivel' && p.disponivel)
      || (filtroAtivo === 'promocao' && p.desconto > 0);
    const busca = buscaAtiva.toLowerCase();
    const matchBusca = !busca || p.nome.toLowerCase().includes(busca) || p.artesao.toLowerCase().includes(busca);
    return matchColecao && matchFiltro && matchBusca;
  });
}

function filtrarColecao(btn, colecao) {
  colecaoAtiva = colecao;
  document.querySelectorAll('.col-chip').forEach(item => item.classList.remove('active'));
  btn.classList.add('active');
  renderCards(getListaFiltrada());
}

function toggleFiltro(btn, filtro) {
  filtroAtivo = filtro;
  document.querySelectorAll('.filter-btn').forEach(item => item.classList.remove('active'));
  btn.classList.add('active');
  renderCards(getListaFiltrada());
}

function filtrarBusca(valor) {
  buscaAtiva = valor;
  renderCards(getListaFiltrada());
}

function trocarView(modo) {
  const grid = document.getElementById('favGrid');
  const btnGrade = document.getElementById('btnGrade');
  const btnLista = document.getElementById('btnLista');
  if (!grid || !btnGrade || !btnLista) return;

  const lista = modo === 'lista';
  grid.classList.toggle('list-mode', lista);
  btnLista.classList.toggle('active', lista);
  btnGrade.classList.toggle('active', !lista);
}

async function removerFavorito(id) {
  favoritos = favoritos.filter(item => String(item.id) !== String(id));
  if (window.AraceState) {
    await window.AraceState.removeFavorite(id);
  }
  renderCards(getListaFiltrada());
}

async function addCarrinho(id) {
  const produto = favoritos.find(item => String(item.id) === String(id));
  if (!produto) return;

  if (window.AraceState) {
    await window.AraceState.addCartItem(id, 1);
  }

  showToast(`"${produto.nome}" adicionado ao carrinho`);
}

function compartilhar() {
  showToast('Link copiado!');
}

function moverColecao() {
  showToast('Escolha uma colecao');
}

function novaColecao() {
  showToast('Colecao criada!');
}

window.filtrarColecao = filtrarColecao;
window.toggleFiltro = toggleFiltro;
window.filtrarBusca = filtrarBusca;
window.trocarView = trocarView;
window.removerFavorito = removerFavorito;
window.addCarrinho = addCarrinho;
window.compartilhar = compartilhar;
window.moverColecao = moverColecao;
window.novaColecao = novaColecao;

document.addEventListener('DOMContentLoaded', () => {
  favoritos = Array.isArray(window.ARACE_FAVORITES)
    ? window.ARACE_FAVORITES
    : (window.AraceState ? window.AraceState.getFavorites() : favoritos);
  renderCards(favoritos);
});
