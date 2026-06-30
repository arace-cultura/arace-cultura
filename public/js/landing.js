

/**
 * Extrai e estrutura os dados de um produto a partir dos atributos 'data-*' do HTML do card.
 * @param {HTMLElement} card - O elemento DOM que representa o card do produto.
 * @returns {Object} Objeto contendo as propriedades do produto mapeadas.
 */
function produtoDoCard(card) {
  return {
    id: card.dataset.produtoId,
    nome: card.dataset.nome,
    artesao: card.dataset.artesao,
    preco: Number(card.dataset.preco || 0), // Garante o tipo numérico, usando 0 como fallback
    categoria: card.dataset.categoria,
    cor: card.dataset.cor || '#b5a898',     // Cor padrão caso não esteja definida
    img: card.dataset.img || '',
  };
}

/**
 * Configura os botões de filtro de categoria.
 * Gerencia a alternância de classes ativas e exibe/oculta os cards correspondentes.
 */
function configurarFiltros() {
  // Cria um array fixo com todos os cards de produtos da página
  const cards = Array.from(document.querySelectorAll('.produto'));

  // Adiciona o evento de clique em cada botão de filtro disponível
  document.querySelectorAll('.filter').forEach(btn => {
    btn.addEventListener('click', () => {
      // Remove o estado ativo de todos os botões e adiciona apenas no botão clicado
      document.querySelectorAll('.filter').forEach(item => item.classList.remove('active'));
      btn.classList.add('active');

      const filtro = btn.dataset.filter;
      
      // Controla a visibilidade do card: oculta se o filtro não for 'all' E a categoria do card for diferente
      cards.forEach(card => {
        card.hidden = filtro !== 'all' && card.dataset.cat !== filtro;
      });
    });
  });
}

/**
 * Gerencia as ações dos produtos (favoritar e adicionar ao carrinho).
 * Utiliza delegação de eventos (Event Delegation) no container do grid para melhor performance.
 */
function configurarAcoesProdutos() {
  const grid = document.getElementById('produtosGrid');
  if (!grid) return; // Encerra se o grid de produtos não existir na página atual

  // Sincronização inicial: Marca os corações (favoritos) como ativos caso já estejam salvos no estado global
  grid.querySelectorAll('.produto').forEach(card => {
    const fav = card.querySelector('.fav');
    if (fav && window.AraceState?.isFavorite(card.dataset.produtoId)) {
      fav.classList.add('active');
    }
  });

  // Ouvinte único no grid para interceptar cliques nos botões internos dos cards
  grid.addEventListener('click', event => {
    
    // --- FLUXO DE FAVORITAR ---
    const favorito = event.target.closest('.fav');
    if (favorito) {
      const card = favorito.closest('.produto');
      // Alterna o estado global (se existir) ou apenas inverte a classe localmente
      const ativo = window.AraceState
        ? window.AraceState.toggleFavorite(produtoDoCard(card))
        : !favorito.classList.contains('active');
      
      favorito.classList.toggle('active', ativo);
      return; // Interrompe a execução para não avaliar o bloco do carrinho
    }

    // --- FLUXO DE ADICIONAR AO CARRINHO ---
    const addCart = event.target.closest('.add-cart');
    if (addCart) {
      const card = addCart.closest('.produto');
      const textoOriginal = addCart.innerHTML;
      
      // Feedback visual imediato de sucesso
      addCart.innerHTML = '<i data-lucide="check"></i> Adicionado';
      addCart.classList.add('is-added');
      lucide.createIcons(); // Renderiza o novo ícone do Lucide inserido dinamicamente

      // Envia a ação para o estado global de forma assíncrona (ignora falhas de forma silenciosa com catch)
      window.AraceState?.addCartItem(card.dataset.produtoId, 1).catch(() => {});

      // Restaura o estado e texto original do botão após 1.6 segundos
      setTimeout(() => {
        addCart.innerHTML = textoOriginal;
        addCart.classList.remove('is-added');
        lucide.createIcons(); // Renderiza novamente os ícones originais do botão
      }, 1600);
    }
  });
}

/**
 * Controla o carrossel de destaque (Hero Carousel).
 * Cria os indicadores de paginação (dots) dinamicamente e gerencia a rotação automática.
 */
function configurarHeroCarousel() {
  const slides = Array.from(document.querySelectorAll('.hero-slide'));
  const dotsContainer = document.getElementById('heroDots');
  if (!slides.length) return; // Encerra se não houver slides

  let atual = 0;   // Índice do slide visível no momento
  let timer = null; // Armazena a referência do setInterval de rotação

  // Gera dinamicamente os botões de navegação baseando-se na quantidade de slides
  if (dotsContainer) {
    dotsContainer.innerHTML = slides.map((_, index) => `
      <button class="hero-dot ${index === 0 ? 'active' : ''}" type="button" aria-label="Ir para slide ${index + 1}"></button>
    `).join('');
  }

  const dots = dotsContainer ? Array.from(dotsContainer.querySelectorAll('.hero-dot')) : [];

  /**
   * Altera o slide visível atual para o índice informado.
   * @param {number} index - O índice do novo slide que deve ser exibido.
   */
  function irPara(index) {
    // Remove o estado ativo do elemento anterior
    slides[atual].classList.remove('active');
    if (dots[atual]) dots[atual].classList.remove('active');

    // Calcula o próximo índice contornando valores negativos ou acima do limite do array
    atual = (index + slides.length) % slides.length;

    // Adiciona o estado ativo no novo elemento selecionado
    slides[atual].classList.add('active');
    if (dots[atual]) dots[atual].classList.add('active');
  }

  /**
   * Inicia ou reinicia o temporizador de rotação do carrossel (avança a cada 5 segundos).
   */
  function iniciar() {
    clearInterval(timer);
    timer = setInterval(() => irPara(atual + 1), 5000);
  }

  // Associa o clique nos "dots" para pular direto para um slide e reiniciar o timer
  dots.forEach((dot, index) => {
    dot.addEventListener('click', () => {
      irPara(index);
      iniciar(); // Reinicia o timer para evitar que o slide mude logo em seguida
    });
  });

  iniciar(); // Inicialização automática do loop
}

/**
 * Inicializa e configura o mapa interativo usando a biblioteca Leaflet.
 * Tenta centralizar na geolocalização do usuário; usa coordenadas padrão em caso de recusa.
 */
function configurarMapa() {
  // Delega a inicialização para o AraceState caso uma função customizada esteja definida globalmente
  if (window.AraceState?.initMap) {
    window.AraceState.initMap('mapa');
    return;
  }

  const mapaEl = document.getElementById('mapa');
  if (!mapaEl || !window.L) return; // Encerra se o elemento container ou a biblioteca Leaflet (L) não existirem

  // Cria a instância do mapa associada à div correspondente
  const mapa = L.map('mapa');
  
  // Adiciona a camada de visualização de mapa (tiles) do OpenStreetMap
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap',
  }).addTo(mapa);

  // Solicita autorização de geolocalização ao navegador do usuário
  navigator.geolocation.getCurrentPosition(
    // Sucesso: Centraliza e coloca um marcador na localização real do usuário
    pos => {
      const { latitude, longitude } = pos.coords;
      mapa.setView([latitude, longitude], 13);
      L.marker([latitude, longitude])
        .addTo(mapa)
        .bindPopup('Você está aqui')
        .openPopup();
    },
    // Falha/Recusa: Centraliza em coordenadas padrão (região da Grande Vitória - ES) como fallback
    () => mapa.setView([-20.3155, -40.3128], 12)
  );
}

// INICIALIZAÇÃO DA APLICAÇÃO
document.addEventListener('DOMContentLoaded', () => {
  lucide.createIcons();        // Inicializa/converte as tags de ícones do Lucide no HTML inicial
  configurarFiltros();         // Ativa o sistema de filtros de categoria
  configurarAcoesProdutos();   // Liga os seletores de clique para fav/carrinho no grid
  configurarHeroCarousel();    // Dá partida no carrossel do banner principal
  configurarMapa();            // Renderiza o mapa interativo
});