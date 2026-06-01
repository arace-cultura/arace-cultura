(() => {
  const KEYS = {
    user: 'arace:user',
    producer: 'arace:producer',
    favorites: 'arace:favorites',
    mode: 'arace:viewMode',
  };

  const DEFAULT_USER = {
    nome: 'Maria',
    sobrenome: 'Silva',
    username: '@mariasilva',
    bio: 'Amante da cultura capixaba',
    nascimento: '1995-06-12',
    genero: 'f',
    email: 'maria@email.com',
    telefone: '',
    cidade: 'Cariacica',
    estado: 'ES',
    avatar: '',
    membroDesde: 'Janeiro de 2024',
    cpf: '',
  };

  const DEFAULT_PRODUCER = {
    cadastrado: false,
    lojaNome: 'Paneleiras Capixabas',
    lojaBio: 'Preservamos uma tradicao centenaria de producao artesanal de panelas de barro, simbolo da cultura capixaba.',
    lojaCategoria: 'ceramica',
    lojaCidade: 'Vitoria',
    lojaEstado: 'ES',
    lojaTelefone: '(27) 99999-1234',
    lojaEmail: 'contato@paneleiras.com',
    lojaAvatar: '',
    lojaBanner: '../assets/imgs/bahia-vitoria.jpg',
  };

  const DEFAULT_FAVORITES = [
    { id: 'fav-panela-capixaba', nome: 'Panela de barro Capixaba', artesao: 'Mestre Ze Pedro', preco: 260, precoAntigo: 300, estrelas: 4.5, avaliacoes: 142, img: '', colecao: 'ceramica', disponivel: true, desconto: 13, cor: '#b5a898' },
    { id: 'fav-preguica-madeira', nome: 'Preguica esculpida em madeira', artesao: 'Atelier Capixaba', preco: 200, precoAntigo: null, estrelas: 5, avaliacoes: 38, img: '', colecao: 'madeira', disponivel: true, desconto: 0, cor: '#8F5E35' },
  ];

  function read(key, fallback) {
    try {
      const value = localStorage.getItem(key);
      return value ? { ...fallback, ...JSON.parse(value) } : { ...fallback };
    } catch {
      return { ...fallback };
    }
  }

  function readArray(key, fallback) {
    try {
      const value = localStorage.getItem(key);
      return value ? JSON.parse(value) : [...fallback];
    } catch {
      return [...fallback];
    }
  }

  function write(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
    window.dispatchEvent(new CustomEvent('arace:state-change', { detail: { key, value } }));
    return value;
  }

  function getUser() {
    return read(KEYS.user, DEFAULT_USER);
  }

  function saveUser(partial) {
    return write(KEYS.user, { ...getUser(), ...partial });
  }

  function getProducer() {
    return read(KEYS.producer, DEFAULT_PRODUCER);
  }

  function saveProducer(partial) {
    return write(KEYS.producer, { ...getProducer(), ...partial });
  }

  function getFavorites() {
    return readArray(KEYS.favorites, DEFAULT_FAVORITES);
  }

  function saveFavorites(items) {
    return write(KEYS.favorites, items);
  }

  function normalizeProduct(product) {
    return {
      id: String(product.id || product.nome || Date.now()),
      nome: product.nome || 'Produto Arace',
      artesao: product.artesao || product.produtor || 'Produtor Arace',
      preco: Number(product.preco || 0),
      precoAntigo: product.precoAntigo ?? null,
      estrelas: Number(product.estrelas || 4),
      avaliacoes: Number(product.avaliacoes || 0),
      img: product.img || product.imagem || '',
      colecao: product.colecao || product.categoria || 'artesanato',
      disponivel: product.disponivel ?? true,
      desconto: Number(product.desconto || 0),
      cor: product.cor || '#b5a898',
    };
  }

  function isFavorite(id) {
    return getFavorites().some(item => String(item.id) === String(id));
  }

  function addFavorite(product) {
    const item = normalizeProduct(product);
    const favorites = getFavorites();
    if (!favorites.some(fav => String(fav.id) === item.id)) {
      favorites.push(item);
      saveFavorites(favorites);
    }
    return item;
  }

  function removeFavorite(id) {
    const favorites = getFavorites().filter(item => String(item.id) !== String(id));
    saveFavorites(favorites);
  }

  function toggleFavorite(product) {
    const id = String(product.id || product.nome);
    if (isFavorite(id)) {
      removeFavorite(id);
      return false;
    }
    addFavorite(product);
    return true;
  }

  function setMode(mode) {
    localStorage.setItem(KEYS.mode, mode);
    window.dispatchEvent(new CustomEvent('arace:mode-change', { detail: { mode } }));
  }

  function getMode() {
    return localStorage.getItem(KEYS.mode) || 'cliente';
  }

  function renderAvatar(target, src, fallbackIcon = 'user') {
    if (!target) return;
    if (src) {
      target.innerHTML = `<img src="${src}" alt="Foto de perfil" />`;
      target.classList.add('has-image');
      return;
    }
    target.innerHTML = `<i data-lucide="${fallbackIcon}"></i>`;
    target.classList.remove('has-image');
  }

  function syncHeader() {
    const user = getUser();
    const favoritesCount = getFavorites().length;

    document.querySelectorAll('.cart-btn, .icon-btn').forEach(button => {
      const text = button.textContent.toLowerCase();
      const icon = button.querySelector('[data-lucide]');
      const isFavoriteButton = text.includes('favorito') || icon?.getAttribute('data-lucide') === 'heart';
      const count = button.querySelector('.cart-count, #fav-label');
      if (isFavoriteButton && count) count.textContent = favoritesCount === 1 ? '1 item' : `${favoritesCount} itens`;
    });

    document.querySelectorAll('.avatar-btn').forEach(avatar => {
      renderAvatar(avatar, user.avatar);
    });
  }

  function setupProducerTransition() {
    document.addEventListener('click', event => {
      const link = event.target.closest('a[href*="cadastro-produtor"], a[href*="cadastro-producer"]');
      if (!link) return;

      const producer = getProducer();
      if (!producer.cadastrado) return;

      event.preventDefault();
      setMode('produtor');
      window.location.href = link.href.includes('/authentication/')
        ? '../user-producter/arace-producer-profile-loja.html'
        : './arace-producer-profile-loja.html';
    });
  }

  window.AraceState = {
    getUser,
    saveUser,
    getProducer,
    saveProducer,
    getFavorites,
    saveFavorites,
    addFavorite,
    removeFavorite,
    toggleFavorite,
    isFavorite,
    getMode,
    setMode,
    renderAvatar,
    syncHeader,
  };

  document.addEventListener('DOMContentLoaded', syncHeader);
  document.addEventListener('DOMContentLoaded', setupProducerTransition);
  window.addEventListener('arace:state-change', syncHeader);
})();
