

const PRODUTO = {
  id: "prod-001",
  nome: "Panela de barro Capixaba",
  artesao: "Mestre Zé Pedro",
  avaliacao: 4.5,
  totalAvaliacoes: 142,
  preco: 260,
  precoAntigo: 300,
  desconto: 13,
  descricao: `Material: Barro\nAcabamento: Trigueiro\nIdeal para cozinhar com sabor, trazendo detalhes únicos\nCor: Questão em linha a sua destino\nCor: Pedra Berra`,
  imagens: [
    "https://images.unsplash.com/photo-1614613535308-eb5fbd847f51?w=600&q=80",
    "https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=600&q=80",
    "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=600&q=80"
  ],
  avaliacoes: [
    { usuario: "Usuário", estrelas: 4, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", data: "Postado em Abril de 2025" },
    { usuario: "Usuário", estrelas: 5, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magla.", data: "Postado em Abril de 2025" },
    { usuario: "Usuário", estrelas: 4, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua lorem magla.", data: "Postado em Março de 2025" }
  ],
  comentarios: [
    { usuario: "Usuário", estrelas: 5, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna.", data: "Postado em Abril de 2025" },
    { usuario: "Usuário", estrelas: 4, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", data: "Postado em Abril de 2025" },
    { usuario: "Usuário", estrelas: 3, texto: "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua lorem magla.", data: "Postado em Abril de 2025" }
  ],
  recomendados: [
    { nome: "Panela do barro", preco: 135, precoAntigo: null, estrelas: 4, img: "https://images.unsplash.com/photo-1614613535308-eb5fbd847f51?w=300&q=80", cta: "Adicionar ao carrinho" },
    { nome: "Preguiça de madeira", preco: 200, precoAntigo: null, estrelas: 5, img: "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&q=80", cta: "Adicionar ao carrinho" },
    { nome: "Panela de barro", preco: 260, precoAntigo: 300, estrelas: 4, img: "https://images.unsplash.com/photo-1590779033100-9f60a05a013d?w=300&q=80", cta: "Adicionar ao carrinho" },
    { nome: "Preguiça de madeira", preco: 390, precoAntigo: null, estrelas: 4, img: "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&q=80", cta: "Adicionar você frete" }
  ]
};

// ── RENDER ─────────────────────────────────────────────────────────────────
function stars(n) {
  let html = '<div class="stars">';
  for (let i = 1; i <= 5; i++) {
    if (i <= Math.floor(n)) html += `<i data-lucide="star"></i>`;
    else if (i === Math.ceil(n) && n % 1 >= 0.5) html += `<i data-lucide="star-half"></i>`;
    else html += `<i data-lucide="star" style="fill:none;color:var(--border)"></i>`;
  }
  return html + '</div>';
}

function formatPreco(v) { return `R$${v.toLocaleString('pt-BR')}`; }

function renderProduto(p) {
  const main = document.getElementById('main-content');

  const thumbsHTML = p.imagens.map((img, i) =>
    `<button class="gallery-thumb ${i===0?'active':''}" onclick="trocarImagem(this,'${img}')">
       <img src="${img}" alt="foto ${i+1}" loading="lazy" />
     </button>`
  ).join('');

  const variantImgsHTML = p.imagens.map((img, i) =>
    `<button class="variant-img-btn ${i===0?'active':''}" onclick="trocarVariante(this,'${img}')">
       <img src="${img}" alt="variante ${i+1}" loading="lazy" />
     </button>`
  ).join('');

  const descLines = p.descricao.split('\n').map(l => `<p>${l}</p>`).join('');

  const avaliacoesHTML = p.avaliacoes.map(r => `
    <div class="review-card">
      <div class="review-card-header">
        <div>
          ${stars(r.estrelas)}
          <span class="review-user">${r.usuario}</span>
        </div>
        <button class="review-more"><i data-lucide="more-horizontal"></i></button>
      </div>
      <p class="review-text">${r.texto}</p>
      <span class="review-date">${r.data}</span>
    </div>`).join('');

  const comentariosHTML = p.comentarios.map(r => `
    <div class="review-card">
      <div class="review-card-header">
        <div>
          ${stars(r.estrelas)}
          <span class="review-user">${r.usuario}</span>
        </div>
        <button class="review-more"><i data-lucide="more-horizontal"></i></button>
      </div>
      <p class="review-text">${r.texto}</p>
      <span class="review-date">${r.data}</span>
    </div>`).join('');

  const recsHTML = p.recomendados.map(r => `
    <div class="rec-card">
      <div class="rec-card-img"><img src="${r.img}" alt="${r.nome}" loading="lazy" /></div>
      <div class="rec-card-body">
        <div class="rec-card-name">${r.nome}</div>
        <div class="rec-card-rating">
          ${[1,2,3,4,5].map(i => `<i data-lucide="star" style="${i<=r.estrelas?'fill:var(--amarelo)':'fill:none;color:var(--border)'}"></i>`).join('')}
          ${r.estrelas.toFixed(1)}
        </div>
        <div class="rec-card-price">
          ${r.precoAntigo ? `<s>${formatPreco(r.precoAntigo)}</s> ` : ''}
          <strong>${formatPreco(r.preco)}</strong>
        </div>
        <button class="btn-add-rec ${r.cta.includes('frete')?'laranja':''}">${r.cta}</button>
      </div>
    </div>`).join('');

  main.innerHTML = `
    <!-- BREADCRUMB -->
    <nav class="breadcrumb">
      <a href="index.html">Início</a>
      <i data-lucide="chevron-right"></i>
      <a href="#">Loja</a>
      <i data-lucide="chevron-right"></i>
      <a href="#">Artesanato</a>
      <i data-lucide="chevron-right"></i>
      <span>Panela Capixaba</span>
    </nav>

    <!-- HERO DO PRODUTO -->
    <div class="product-hero">
      <!-- Galeria -->
      <div class="gallery">
        <div class="gallery-main">
          <img id="mainImg" src="${p.imagens[0]}" alt="${p.nome}" />
        </div>
        <div class="gallery-thumbs">${thumbsHTML}</div>
      </div>

      <!-- Info -->
      <div class="product-info">
        <h1 class="product-name">${p.nome}</h1>
        <div class="rating-row">
          ${stars(p.avaliacao)}
          <span class="rating-count">${p.avaliacao} (${p.totalAvaliacoes})</span>
        </div>

        <div class="price-row">
          <span class="price-current">${formatPreco(p.preco)}</span>
          <span class="price-old">${formatPreco(p.precoAntigo)}</span>
          <span class="price-badge">-${p.desconto}%</span>
        </div>

        <div class="variant-imgs">${variantImgsHTML}</div>

        <div class="product-desc-block">
          <strong>Descrição</strong>
          ${descLines}
        </div>

        <div class="action-row">
          <button class="btn-icon" title="Favoritar"><i data-lucide="heart"></i></button>
          <button class="btn-icon" title="Compartilhar"><i data-lucide="share-2"></i></button>
          <button class="btn-icon" title="Comparar"><i data-lucide="scale"></i></button>
          <button class="btn-add"><i data-lucide="shopping-cart"></i> Adicionar</button>
          <button class="btn-buy"><i data-lucide="zap"></i> Comprar</button>
        </div>
      </div>
    </div>

    <!-- DETALHES + AVALIAÇÕES + COMENTÁRIOS -->
    <h2 class="section-title">Detalhes do Produto</h2>

    <div class="product-details-grid">
      <!-- Avaliações -->
      <div class="avaliacoes-col">
        <div class="reviews-header">
          <div class="reviews-meta">
            <span class="big-rating">${p.avaliacao}</span>
            ${stars(p.avaliacao)}
            <span style="font-size:13px;color:var(--muted)">(${p.totalAvaliacoes})</span>
          </div>
        </div>
        <h3 style="font-size:15px;font-weight:500;margin-bottom:.75rem;">Avaliações</h3>
        <div class="review-list">${avaliacoesHTML}</div>
      </div>

      <!-- Comentários -->
      <div class="comentarios-col">
        <div class="reviews-header">
          <span style="font-size:15px;font-weight:500;">Comentários</span>
          <div class="reviews-filters">
            <select class="select-filter">
              <option>Recentes</option>
              <option>Melhores</option>
              <option>Piores</option>
            </select>
            <button class="btn-escrever"><i data-lucide="pen-line"></i> Escrever</button>
          </div>
        </div>
        <div class="review-list" style="margin-top:.75rem">${comentariosHTML}</div>
        <div style="text-align:right;margin-top:.75rem;font-size:13px;color:var(--muted)">Ver mais</div>
      </div>
    </div>

    <!-- RECOMENDADOS -->
    <div class="rec-section">
      <h2 class="section-title">Você pode gostar</h2>
      <div class="rec-grid">${recsHTML}</div>
    </div>
  `;

  lucide.createIcons();
}

// Troca imagem principal
function trocarImagem(btn, src) {
  document.getElementById('mainImg').src = src;
  document.querySelectorAll('.gallery-thumb').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');
}
function trocarVariante(btn, src) {
  document.getElementById('mainImg').src = src;
  document.querySelectorAll('.variant-img-btn').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');
}

// ── INICIALIZAR (em produção: fetch('/api/produto/'+id).then(r=>r.json()).then(renderProduto)) ──
document.addEventListener('DOMContentLoaded', () => {
  // Simula latência de rede
  setTimeout(() => renderProduto(PRODUTO), 300);
});
