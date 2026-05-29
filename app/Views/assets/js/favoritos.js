
// ── DADOS (substituir por fetch('/api/favoritos') em produção) ─────────────
const FAVORITOS = [
  { id:1, nome:"Panela de barro Capixaba", artesao:"Mestre Zé Pedro", preco:260, precoAntigo:300, estrelas:4.5, avaliacoes:142, img:"https://images.unsplash.com/photo-1614613535308-eb5fbd847f51?w=400&q=80", colecao:"ceramica", disponivel:true, desconto:13 },
  { id:2, nome:"Preguiça esculpida em madeira", artesao:"Atelier Capixaba", preco:200, precoAntigo:null, estrelas:5, avaliacoes:38, img:"https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80", colecao:"madeira", disponivel:true, desconto:0 },
  { id:3, nome:"Colar de pedras do ES", artesao:"Joias da Serra", preco:390, precoAntigo:480, estrelas:4, avaliacoes:21, img:"https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=400&q=80", colecao:"joias", disponivel:true, desconto:19 },
  { id:4, nome:"Tigela de cerâmica pintada", artesao:"Casa do Barro", preco:135, precoAntigo:null, estrelas:4, avaliacoes:57, img:"https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=400&q=80", colecao:"ceramica", disponivel:false, desconto:0 },
  { id:5, nome:"Vaso artesanal capixaba", artesao:"Arte Vitória", preco:178, precoAntigo:220, estrelas:4.5, avaliacoes:93, img:"https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&q=80", colecao:"ceramica", disponivel:true, desconto:19 },
];

let filtroAtivo = 'todos';
let colecaoAtiva = 'todos';
let buscaAtiva = '';

function stars(n) {
  return [1,2,3,4,5].map(i =>
    `<i data-lucide="star" style="${i<=Math.round(n)?'fill:var(--amarelo)':'fill:none;color:var(--border)'}"></i>`
  ).join('');
}

function renderCards(lista) {
  const grid = document.getElementById('favGrid');
  if (!lista.length) {
    grid.innerHTML = `
      <div class="fav-empty">
        <i data-lucide="heart"></i>
        <h2>Nenhum favorito aqui</h2>
        <p>Explore os produtos e salve os que você curtir</p>
        <a href="../main/produtos.html" class="btn-explorar">
          <i data-lucide="shopping-bag"></i> Explorar produtos
        </a>
      </div>`;
    lucide.createIcons(); return;
  }

  grid.innerHTML = lista.map(p => `
    <div class="fav-card" data-id="${p.id}" data-colecao="${p.colecao}">
      <div class="fav-card-img">
        <img src="${p.img}" alt="${p.nome}" loading="lazy" />
        ${p.desconto ? `<span class="fav-badge-off">-${p.desconto}%</span>` : ''}
        ${!p.disponivel ? `<div style="position:absolute;inset:0;background:rgba(255,255,255,.6);display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:500;color:var(--muted)">Indisponível</div>` : ''}
        <div class="fav-card-actions">
          <button class="card-action-btn" title="Mover para coleção" onclick="moverColecao(${p.id})"><i data-lucide="folder-plus"></i></button>
          <button class="card-action-btn" title="Compartilhar" onclick="compartilhar(${p.id})"><i data-lucide="share-2"></i></button>
          <button class="card-action-btn remove" title="Remover dos favoritos" onclick="removerFavorito(${p.id})"><i data-lucide="heart-off"></i></button>
        </div>
      </div>
      <div class="fav-card-body">
        <div class="fav-card-info">
          <div class="fav-card-artesao">${p.artesao}</div>
          <div class="fav-card-name">${p.nome}</div>
          <div class="fav-card-rating">${stars(p.estrelas)} ${p.estrelas} (${p.avaliacoes})</div>
          <div class="fav-card-price">
            <strong>R$${p.preco.toLocaleString('pt-BR')}</strong>
            ${p.precoAntigo ? `<s>R$${p.precoAntigo.toLocaleString('pt-BR')}</s><span class="badge-desc">-${p.desconto}%</span>` : ''}
          </div>
        </div>
        <button class="btn-add-cart" ${!p.disponivel ? 'disabled style="opacity:.5;cursor:not-allowed"' : ''} onclick="addCarrinho(${p.id})">
          <i data-lucide="shopping-cart"></i>
          ${p.disponivel ? 'Adicionar ao carrinho' : 'Indisponível'}
        </button>
      </div>
    </div>`).join('');

  lucide.createIcons();
  atualizarContador(lista.length);
}

function getListaFiltrada() {
  return FAVORITOS.filter(p => {
    const matchColecao = colecaoAtiva === 'todos' || p.colecao === colecaoAtiva;
    const matchFiltro = filtroAtivo === 'todos'
      || (filtroAtivo === 'disponivel' && p.disponivel)
      || (filtroAtivo === 'promocao' && p.desconto > 0);
    const matchBusca = !buscaAtiva || p.nome.toLowerCase().includes(buscaAtiva.toLowerCase())
      || p.artesao.toLowerCase().includes(buscaAtiva.toLowerCase());
    return matchColecao && matchFiltro && matchBusca;
  });
}

function filtrarColecao(btn, colecao) {
  colecaoAtiva = colecao;
  document.querySelectorAll('.col-chip').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  renderCards(getListaFiltrada());
}

function toggleFiltro(btn, filtro) {
  filtroAtivo = filtro;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  renderCards(getListaFiltrada());
}

function filtrarBusca(val) {
  buscaAtiva = val;
  renderCards(getListaFiltrada());
}

function trocarView(modo) {
  const grid = document.getElementById('favGrid');
  const btnG = document.getElementById('btnGrade');
  const btnL = document.getElementById('btnLista');
  if (modo === 'lista') {
    grid.classList.add('list-mode');
    btnL.classList.add('active'); btnG.classList.remove('active');
  } else {
    grid.classList.remove('list-mode');
    btnG.classList.add('active'); btnL.classList.remove('active');
  }
}

function removerFavorito(id) {
  const idx = FAVORITOS.findIndex(p => p.id === id);
  if (idx !== -1) FAVORITOS.splice(idx, 1);
  renderCards(getListaFiltrada());
}

function addCarrinho(id) {
  const p = FAVORITOS.find(f => f.id === id);
  if (p) showToast(`"${p.nome}" adicionado ao carrinho`);
}

function compartilhar(id) { showToast('Link copiado!'); }
function moverColecao(id) { showToast('Escolha uma coleção'); }
function novaColecao() { showToast('Coleção criada!'); }

function atualizarContador(n) {
  document.getElementById('headerCount').textContent = n;
  document.getElementById('favCount').textContent = `${n} itens`;
}

function showToast(msg) {
  let t = document.getElementById('_toast');
  if (!t) { t = document.createElement('div'); t.id='_toast'; t.style.cssText='position:fixed;bottom:1.5rem;right:1.5rem;background:var(--text);color:#fff;padding:10px 18px;border-radius:10px;font-size:14px;font-family:var(--body);box-shadow:0 4px 16px rgba(0,0,0,.18);z-index:999;transition:opacity .3s'; document.body.appendChild(t); }
  t.textContent = msg; t.style.opacity = '1';
  clearTimeout(t._timer);
  t._timer = setTimeout(() => t.style.opacity = '0', 2500);
}

document.addEventListener('DOMContentLoaded', () => renderCards(FAVORITOS));
