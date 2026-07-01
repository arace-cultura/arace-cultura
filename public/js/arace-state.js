(() => {
  // Centraliza estado compartilhado entre paginas: usuario, carrinho, tema e modo produtor/cliente.


  const KEYS = {
    user: 'arace:user',
    producer: 'arace:producer',
    mode: 'arace:viewMode',
    theme: 'arace:theme',
  };

  const DEFAULT_USER = {
    nome: '',
    username: '',
    bio: '',
    nascimento: '',
    sexo: '',
    genero: '',
    email: '',
    telefone: '',
    cidade: '',
    estado: '',
    fotoUrl: '',
    avatar: '',
    membroDesde: '',
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
    fotoUrl: '',
    bannerUrl: '/images/bahia-vitoria.jpg',
    lojaAvatar: '',
    lojaBanner: '/images/bahia-vitoria.jpg',
  };

  const stateScript = document.currentScript || document.querySelector('script[src*="/js/arace-state.js"]');
  const baseUrl = new URL('../', stateScript?.src || window.location.href);

  function url(path = '') {
    const value = String(path);
    if (/^(?:[a-z]+:|#)/i.test(value)) return value;

    return new URL(value.replace(/^\/+/, ''), baseUrl).toString();
  }

  function go(path) {
    window.location.href = url(path);
  }

  // Leitura local e usada apenas como cache/apoio visual quando a pagina ainda nao recebeu dados do servidor.

  function read(key, fallback) {
    try {
      const value = localStorage.getItem(key);
      return value ? { ...fallback, ...JSON.parse(value) } : { ...fallback };
    } catch {
      return { ...fallback };
    }
  }

  function write(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
    window.dispatchEvent(new CustomEvent('arace:state-change', { detail: { key, value } }));
    return value;
  }

  // Usuario injetado pelo PHP, porque ele veio do Firestore na renderizacao da pagina.


  function getUser() {
    if (window.ARACE_AUTH_USER) {
      const user = { ...DEFAULT_USER, ...window.ARACE_AUTH_USER };
      user.fotoUrl = user.fotoUrl || user.avatar || '';
      user.avatar = user.fotoUrl;
      user.sexo = user.sexo || user.genero || '';
      user.genero = user.sexo;
      return user;
    }

    return read(KEYS.user, DEFAULT_USER);
  }

  function saveUser(partial) {
    return write(KEYS.user, { ...getUser(), ...partial });
  }

  function getProducer() {
    const producer = window.ARACE_PRODUCER
      ? { ...DEFAULT_PRODUCER, ...window.ARACE_PRODUCER }
      : read(KEYS.producer, DEFAULT_PRODUCER);

    producer.fotoUrl = producer.fotoUrl || producer.lojaAvatar || producer.avatar || '';
    producer.bannerUrl = producer.bannerUrl || producer.lojaBanner || producer.banner || '';
    producer.lojaAvatar = producer.fotoUrl;
    producer.lojaBanner = producer.bannerUrl;

    return producer;
  }

  function saveProducer(partial) {
    return write(KEYS.producer, { ...getProducer(), ...partial });
  }

  // Wrapper simples para falar com endpoints CodeIgniter que persistem no Firestore.
  async function api(path, options = {}) {
    const response = await fetch(url(path), {
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json', ...(options.headers || {}) },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    return response.json();
  }

  // Carrinho sempre tenta persistir no Firestore via /api/cart.

  async function addCartItem(productId, quantity = 1) {
    const payload = await api('api/cart', {
      method: 'POST',
      body: JSON.stringify({ produtoId: productId, quantidade: quantity }),
    });
    window.ARACE_CART = payload.data;
    syncHeader();
    return payload.data;
  }

  function setMode(mode) {
    localStorage.setItem(KEYS.mode, mode);
    window.dispatchEvent(new CustomEvent('arace:mode-change', { detail: { mode } }));
  }

  function getMode() {
    return localStorage.getItem(KEYS.mode) || 'cliente';
  }

  function getTheme() {
    return localStorage.getItem(KEYS.theme) || 'claro';
  }

  function applyTheme(theme = getTheme()) {
    const selected = theme === 'escuro' || theme === 'sistema' ? theme : 'claro';
    const systemDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches;
    const applied = selected === 'sistema' ? (systemDark ? 'escuro' : 'claro') : selected;

    document.documentElement.dataset.theme = applied;
    document.body?.classList.toggle('theme-dark', applied === 'escuro');

    document.querySelectorAll('.theme-option').forEach(option => {
      const label = option.querySelector('.theme-label')?.textContent?.trim().toLowerCase();
      option.classList.toggle('active', label === selected);
    });
  }

  function setTheme(theme) {
    localStorage.setItem(KEYS.theme, theme);
    applyTheme(theme);
    window.dispatchEvent(new CustomEvent('arace:theme-change', { detail: { theme } }));
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

  // Sincroniza contadores e avatar de cabecalhos repetidos nas paginas.

  function syncHeader() {
    const user = getUser();
    const cartItems = window.ARACE_CART?.items || [];
    const cartCount = cartItems.reduce((total, item) => total + Number(item.quantidade || 1), 0);

    document.querySelectorAll('.cart-btn, .icon-btn').forEach(button => {
      const icon = button.querySelector('[data-lucide]');
      const isCartButton = icon?.getAttribute('data-lucide') === 'shopping-cart';
      const count = button.querySelector('.cart-count, #fav-label');
      if (isCartButton && count) count.textContent = cartCount === 1 ? '1 item' : `${cartCount} itens`;
    });

    const avatarSrc = user.fotoUrl || user.avatar;
    document.querySelectorAll('.avatar-btn').forEach(avatar => {
      if (!avatarSrc && avatar.querySelector('img')) return;
      renderAvatar(avatar, avatarSrc);
    });
  }

  // Redireciona links de cadastro quando o usuario ja é produtor.
  function setupProducerTransition() {
    document.addEventListener('click', event => {
      const link = event.target.closest('a[href*="cadastro/produtor"], a[href*="cadastro-produtor"], a[href*="cadastro-producer"]');
      if (!link) return;

      const producer = getProducer();
      if (!producer.cadastrado) return;

      event.preventDefault();
      setMode('produtor');
      go('produtor/perfil-loja');
    });
  }

  // Aplica tema salvo e destaca item ativo do menu lateral.
  function applyPageStyles() {
    applyTheme();

    const current = new URL(window.location.href);
    document.querySelectorAll('aside .nav-item[href]').forEach(item => {
      try {
        const target = new URL(item.getAttribute('href'), window.location.href);
        if (target.pathname === current.pathname) {
          item.classList.add('active');
        }
      } catch {
        // Links parciais nao precisam participar do destaque.
      }
    });
  }

  // Inicializa o mapa com geolocalizacao quando a pagina tiver um elemento #mapa.
  function initMap(targetId = 'mapa') {
    const mapaEl = document.getElementById(targetId);
    if (!mapaEl || !window.L || mapaEl.dataset.araceMapReady === '1') return null;

    mapaEl.dataset.araceMapReady = '1';

    const mapa = L.map(targetId, {
      zoomControl: true,
      scrollWheelZoom: false,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap',
    }).addTo(mapa);

    const fallback = [-20.3155, -40.3128];
    mapa.setView(fallback, 12);

    const markerIcon = L.divIcon({
      className: 'arace-map-marker',
      html: '<span></span>',
      iconSize: [24, 24],
      iconAnchor: [12, 12],
    });

    navigator.geolocation?.getCurrentPosition(
      pos => {
        const { latitude, longitude } = pos.coords;
        mapa.setView([latitude, longitude], 13);
        L.marker([latitude, longitude], { icon: markerIcon })
          .addTo(mapa)
          .bindPopup('Voce esta aqui')
          .openPopup();
      },
      () => {
        L.marker(fallback, { icon: markerIcon })
          .addTo(mapa)
          .bindPopup('Arace em Vitoria')
          .openPopup();
      }
    );

    setTimeout(() => mapa.invalidateSize(), 150);

    return mapa;
  }

  window.AraceState = {
    url,
    go,
    getUser,
    saveUser,
    getProducer,
    saveProducer,
    addCartItem,
    getMode,
    setMode,
    getTheme,
    setTheme,
    applyTheme,
    applyPageStyles,
    initMap,
    renderAvatar,
    syncHeader,
  };

  document.addEventListener('DOMContentLoaded', syncHeader);
  document.addEventListener('DOMContentLoaded', applyPageStyles);
  document.addEventListener('DOMContentLoaded', setupProducerTransition);
  window.addEventListener('arace:state-change', syncHeader);
  window.matchMedia?.('(prefers-color-scheme: dark)').addEventListener?.('change', () => {
    if (getTheme() === 'sistema') applyTheme('sistema');
  });
})();
