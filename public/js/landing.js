function produtoDoCard(card) {
  return {
    id: card.dataset.produtoId,
    nome: card.dataset.nome,
    artesao: card.dataset.artesao,
    preco: Number(card.dataset.preco || 0),
    categoria: card.dataset.categoria,
    cor: card.dataset.cor || '#b5a898',
    img: card.dataset.img || '',
  };
}

function configurarFiltros() {
  const cards = Array.from(document.querySelectorAll('.produto'));

  document.querySelectorAll('.filter').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.filter').forEach(item => item.classList.remove('active'));
      btn.classList.add('active');

      const filtro = btn.dataset.filter;
      cards.forEach(card => {
        card.hidden = filtro !== 'all' && card.dataset.cat !== filtro;
      });
    });
  });
}

function configurarAcoesProdutos() {
  const grid = document.getElementById('produtosGrid');
  if (!grid) return;

  grid.querySelectorAll('.produto').forEach(card => {
    const fav = card.querySelector('.fav');
    if (fav && window.AraceState?.isFavorite(card.dataset.produtoId)) {
      fav.classList.add('active');
    }
  });

  grid.addEventListener('click', event => {
    const favorito = event.target.closest('.fav');
    if (favorito) {
      const card = favorito.closest('.produto');
      const ativo = window.AraceState
        ? window.AraceState.toggleFavorite(produtoDoCard(card))
        : !favorito.classList.contains('active');
      favorito.classList.toggle('active', ativo);
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
  configurarFiltros();
  configurarAcoesProdutos();
  configurarHeroCarousel();
  configurarMapa();
});
