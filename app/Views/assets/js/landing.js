const ROTAS = {
  produtos: 'produtos.html',
  carrinho: 'carrinho.html',
  favoritos: '../user/favoritos.html',
  perfil: '../user/perfil.html',
};

const PRODUTOS_DESTAQUE = [
  {
    id: 'panela-barro',
    nome: 'Panela de barro',
    artesao: 'Espirito das Pedras',
    categoria: 'ceramica',
    preco: 245,
    avaliacoes: 24,
    estrelas: 4.5,
    favorito: false,
    cor: '#C1734A',
  },
  {
    id: 'preguica-madeira',
    nome: 'Preguica de machao',
    artesao: 'Atelier Capixaba',
    categoria: 'madeira',
    preco: 290,
    avaliacoes: 11,
    estrelas: 4,
    favorito: false,
    cor: '#8F5E35',
  },
  {
    id: 'panela-barro-2',
    nome: 'Panela de barro n. 2',
    artesao: 'Arte Local',
    categoria: 'ceramica',
    preco: 180,
    avaliacoes: 38,
    estrelas: 5,
    favorito: true,
    cor: '#D28A4D',
  },
  {
    id: 'prato-barro',
    nome: 'Prato de barro',
    artesao: 'Casa do Barro',
    categoria: 'ceramica',
    preco: 170,
    avaliacoes: 42,
    estrelas: 5,
    favorito: false,
    cor: '#B45A3C',
  },
  {
    id: 'bolsa-arace',
    nome: 'Bolsa Arace',
    artesao: 'Arte Arace',
    categoria: 'pintura',
    preco: 310,
    avaliacoes: 19,
    estrelas: 4.5,
    favorito: false,
    cor: '#25518f',
  },
  {
    id: 'escultura-madeira',
    nome: 'Escultura em madeira',
    artesao: 'Madeira Capixaba',
    categoria: 'madeira',
    preco: 195,
    avaliacoes: 3,
    estrelas: 3,
    favorito: false,
    cor: '#478632',
  },
];

const PRODUTORES_DESTAQUE = [
  { nome: 'Espirito das Pedras', iniciais: 'EP', produtos: 12 },
  { nome: 'Arte Arace', iniciais: 'AA', produtos: 8 },
  { nome: 'Nativo Pottery', iniciais: 'NP', produtos: 21 },
  { nome: 'Serra Pinturas', iniciais: 'SP', produtos: 5 },
  { nome: 'Madeira Viva', iniciais: 'MV', produtos: 17 },
  { nome: 'Joias Capixabas', iniciais: 'JC', produtos: 9 },
];

let produtosAtuais = [...PRODUTOS_DESTAQUE];

function formatarMoeda(valor) {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function escaparHTML(valor) {
  return String(valor).replace(/[&<>"']/g, caractere => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;',
  }[caractere]));
}

function renderStars(nota) {
  return [1, 2, 3, 4, 5].map(indice => {
    const icon = indice <= Math.floor(nota)
      ? 'star'
      : indice - nota <= 0.5
        ? 'star-half'
        : 'star';
    const apagada = indice > Math.ceil(nota) ? ' style="opacity:.3"' : '';
    return `<i data-lucide="${icon}"${apagada}></i>`;
  }).join('');
}

function produtoTemplate(produto) {
  return `
    <article class="produto" data-cat="${escaparHTML(produto.categoria)}" data-produto-id="${escaparHTML(produto.id)}">
      <div class="produto-img" style="background:${escaparHTML(produto.cor)}">
        <button class="fav ${produto.favorito ? 'active' : ''}" type="button" aria-label="Favoritar produto">
          <i data-lucide="heart"></i>
        </button>
      </div>
      <div class="produto-info">
        <span class="artesao">${escaparHTML(produto.artesao)}</span>
        <a href="arace-produto.html?id=${encodeURIComponent(produto.id)}" class="nome">${escaparHTML(produto.nome)}</a>
        <div class="stars">
          ${renderStars(produto.estrelas)}
          <span>(${produto.avaliacoes})</span>
        </div>
        <div class="preco"><strong>${formatarMoeda(produto.preco)}</strong></div>
        <button class="add-cart" type="button" data-produto-id="${escaparHTML(produto.id)}">
          <i data-lucide="shopping-cart"></i> Adicionar ao carrinho
        </button>
      </div>
    </article>`;
}

function renderProdutos(lista) {
  const grid = document.getElementById('produtosGrid');
  if (!grid) return;

  grid.innerHTML = lista.map(produtoTemplate).join('');
  lucide.createIcons();
}

function renderProdutores() {
  const grid = document.getElementById('produtoresGrid');
  if (!grid) return;

  grid.innerHTML = PRODUTORES_DESTAQUE.map(produtor => `
    <article class="produtor">
      <div class="avatar">${escaparHTML(produtor.iniciais)}</div>
      <span class="p-nome">${escaparHTML(produtor.nome)}</span>
      <span class="p-qtd">${produtor.produtos} produtos</span>
    </article>`).join('');
}

async function carregarProdutos() {
  // Firestore/API: buscar produtos aqui e substituir PRODUTOS_DESTAQUE.
  // Exemplo futuro: const snapshot = await getDocs(collection(db, 'produtos'));
  // Campos esperados: {{nome_produto}}, {{preco_produto}}, {{imagem_produto}}, {{categoria_produto}}.
  produtosAtuais = [...PRODUTOS_DESTAQUE];
  renderProdutos(produtosAtuais);
}

function configurarFiltros() {
  document.querySelectorAll('.filter').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.filter').forEach(item => item.classList.remove('active'));
      btn.classList.add('active');

      const filtro = btn.dataset.filter;
      const lista = filtro === 'all'
        ? produtosAtuais
        : produtosAtuais.filter(produto => produto.categoria === filtro);

      renderProdutos(lista);
    });
  });
}

function configurarAcoesProdutos() {
  const grid = document.getElementById('produtosGrid');
  if (!grid) return;

  grid.addEventListener('click', event => {
    const favorito = event.target.closest('.fav');
    if (favorito) {
      favorito.classList.toggle('active');
      return;
    }

    const addCart = event.target.closest('.add-cart');
    if (addCart) {
      const textoOriginal = addCart.innerHTML;
      addCart.innerHTML = '<i data-lucide="check"></i> Adicionado';
      addCart.classList.add('is-added');
      lucide.createIcons();

      setTimeout(() => {
        addCart.innerHTML = textoOriginal;
        addCart.classList.remove('is-added');
        lucide.createIcons();
      }, 1600);
    }
  });
}

function configurarHeroCarousel() {
  const slides = Array.from(document.querySelectorAll('.hero-slide'));
  const dotsContainer = document.getElementById('heroDots');
  if (!slides.length) return;

  let atual = 0;
  let timer = null;

  if (dotsContainer) {
    dotsContainer.innerHTML = slides.map((_, index) => `
      <button class="hero-dot ${index === 0 ? 'active' : ''}" type="button" aria-label="Ir para slide ${index + 1}"></button>
    `).join('');
  }

  const dots = dotsContainer ? Array.from(dotsContainer.querySelectorAll('.hero-dot')) : [];

  function irPara(index) {
    slides[atual].classList.remove('active');
    if (dots[atual]) dots[atual].classList.remove('active');

    atual = (index + slides.length) % slides.length;

    slides[atual].classList.add('active');
    if (dots[atual]) dots[atual].classList.add('active');
  }

  function iniciar() {
    clearInterval(timer);
    timer = setInterval(() => irPara(atual + 1), 5000);
  }

  dots.forEach((dot, index) => {
    dot.addEventListener('click', () => {
      irPara(index);
      iniciar();
    });
  });

  iniciar();
}

function configurarMapa() {
  const mapaEl = document.getElementById('mapa');
  if (!mapaEl || !window.L) return;

  const mapa = L.map('mapa');
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap',
  }).addTo(mapa);

  navigator.geolocation.getCurrentPosition(
    pos => {
      const { latitude, longitude } = pos.coords;
      mapa.setView([latitude, longitude], 13);
      L.marker([latitude, longitude])
        .addTo(mapa)
        .bindPopup('Voce esta aqui')
        .openPopup();
    },
    () => mapa.setView([-20.3155, -40.3128], 12)
  );
}

document.addEventListener('DOMContentLoaded', () => {
  lucide.createIcons();
  carregarProdutos();
  renderProdutores();
  configurarFiltros();
  configurarAcoesProdutos();
  configurarHeroCarousel();
  configurarMapa();
});
