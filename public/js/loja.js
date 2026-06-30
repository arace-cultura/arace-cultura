// Interacoes da pagina de loja do produtor. Produtos, carrinho e favoritos usam dados do Firestore.
const lojaState = {
  cartCount: (window.ARACE_CART?.items || []).reduce((total, item) => total + Number(item.quantidade || 1), 0),
};

const araceUrl = path => window.AraceState?.url(path) || path;
const araceGo = path => window.AraceState?.go(path) || (window.location.href = path);

const produtosLoja = Array.isArray(window.ARACE_STORE_PRODUCTS) ? window.ARACE_STORE_PRODUCTS : [];

function showToast(message, duration = 2600) {
  const toast = document.getElementById('toast');
  const msg = document.getElementById('toast-msg');
  if (!toast || !msg) return;

  msg.textContent = message;
  toast.classList.add('show');
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => toast.classList.remove('show'), duration);
}

function atualizarCarrinho() {
  const label = document.getElementById('cart-label');
  if (label) label.textContent = lojaState.cartCount === 1 ? '1 item' : `${lojaState.cartCount} itens`;
}

function asideCliente() {
  return `
    <a class="nav-item" href="${araceUrl('')}"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="${araceUrl('pesquisa')}"><i data-lucide="shopping-bag"></i> Produtos</a>
    <a class="nav-item" href="${araceUrl('arace-carrinho')}"><i data-lucide="shopping-cart"></i> Carrinho</a>
    <a class="nav-item" href="${araceUrl('arace-config')}"><i data-lucide="settings"></i> Configuracoes</a>
    <a class="nav-item" href="${araceUrl('usuario/arace-perfil')}"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="${araceUrl('cadastro/produtor')}"><i data-lucide="box"></i> Quero ser produtor</a>
    <div class="nav-divider"></div>
    <div class="nav-section">Reportar</div>
    <a class="nav-item" href="${araceUrl('arace-config#pagamento')}"><i data-lucide="hand-coins"></i> Detalhes de pagamento</a>
  `;
}

function asideProdutor() {
  return `
    <a class="nav-item" href="${araceUrl('')}"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="${araceUrl('produtor/painel')}"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item" href="${araceUrl('produtor/painel')}"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item" href="${araceUrl('produtor/pedidos')}"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item active" href="${araceUrl('produtor/perfil-loja')}" aria-current="page"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="${araceUrl('produtor/perfil')}"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="${araceUrl('produtor/configuracao-loja')}"><i data-lucide="settings"></i> Configuracoes da loja</a>
    <button class="nav-item nav-button" type="button" id="verComoCliente"><i data-lucide="eye"></i> Ver como cliente</button>
    <div class="nav-section">Suporte</div>
    <a class="nav-item" href="${araceUrl('arace-config#pagamento')}"><i data-lucide="hand-coins"></i> Pagamentos</a>
  `;
}

function configurarAside() {
  const aside = document.querySelector('aside[aria-label="Navegação principal"]');
  if (!aside || !window.AraceState) return;

  const produtor = window.AraceState.getProducer();
  const modo = window.AraceState.getMode();
  aside.innerHTML = produtor.cadastrado && modo === 'produtor' ? asideProdutor() : asideCliente();

  document.getElementById('verComoCliente')?.addEventListener('click', () => {
    window.AraceState.setMode('cliente');
    configurarAside();
    lucide.createIcons();
  });
}

function renderDadosLoja() {
  if (!window.AraceState) return;
  const loja = window.AraceState.getProducer();

  const title = document.querySelector('.store-title');
  const subtitle = document.querySelector('.store-subtitle');
  const history = document.querySelector('.history-text p');
  const avatar = document.querySelector('.store-avatar');
  const banner = document.querySelector('.banner-img');

  if (title) title.textContent = loja.lojaNome;
  if (subtitle) {
    const local = [loja.lojaCidade, loja.lojaEstado].filter(Boolean).join(', ') || 'Local nao informado';
    const categoria = loja.lojaCategoria || loja.categoria || 'Artesanato tradicional';
    subtitle.innerHTML = `<i data-lucide="map-pin"></i> ${local} · ${categoria}`;
  }
  if (history) history.textContent = loja.lojaBio;
  if (avatar) window.AraceState.renderAvatar(avatar, loja.fotoUrl || loja.lojaAvatar, 'store');
  if (banner) {
    const bannerUrl = loja.bannerUrl || loja.lojaBanner;
    banner.src = bannerUrl ? araceUrl(bannerUrl) : araceUrl('images/bahia-vitoria.jpg');
    banner.alt = `Capa da loja ${loja.lojaNome}`;
  }
}

function produtoPorCard(card) {
  const id = card.dataset.produtoId || card.dataset.id;
  const base = produtosLoja.find(produto => String(produto.id) === String(id)) || {};
  const nome = card.querySelector('.product-name')?.textContent.trim() || base.nome || 'Produto Arace';
  const precoTexto = card.querySelector('.product-price')?.textContent || String(base.preco || 0);
  return {
    ...base,
    id,
    nome,
    preco: Number(precoTexto.replace(/[^\d,]/g, '').replace(',', '.')) || Number(base.preco || 0),
  };
}

function configurarProdutos() {
  document.querySelectorAll('.product-card').forEach(card => {
    const produto = produtoPorCard(card);
    card.dataset.produtoId = produto.id;

    const fav = card.querySelector('.product-favorite');
    if (fav && window.AraceState?.isFavorite(produto.id)) fav.classList.add('active');
  });

  document.getElementById('products-grid')?.addEventListener('click', event => {
    const fav = event.target.closest('.product-favorite');
    if (fav) {
      event.stopPropagation();
      const produto = produtoPorCard(fav.closest('.product-card'));
      const ativo = window.AraceState ? window.AraceState.toggleFavorite(produto) : !fav.classList.contains('active');
      fav.classList.toggle('active', ativo);
      showToast(ativo ? `"${produto.nome}" adicionado aos favoritos` : `"${produto.nome}" removido dos favoritos`);
      return;
    }

    const addCart = event.target.closest('.add-cart-btn');
    if (addCart) {
      event.stopPropagation();
      const produto = produtoPorCard(addCart.closest('.product-card'));
      window.AraceState?.addCartItem(produto.id, 1).then(cart => {
        lojaState.cartCount = (cart?.items || []).reduce((total, item) => total + Number(item.quantidade || 1), 0);
        atualizarCarrinho();
      }).catch(() => {
        lojaState.cartCount += 1;
        atualizarCarrinho();
      });
      showToast(`"${produto.nome}" adicionado ao carrinho`);
    }
  });
}

function configurarHeader() {
  document.getElementById('btn-cart')?.addEventListener('click', () => {
    araceGo('arace-carrinho');
  });

  document.getElementById('btn-fav')?.addEventListener('click', () => {
    araceGo('usuario/arace-favoritos');
  });

  document.querySelector('header .avatar-btn')?.addEventListener('click', () => {
    araceGo('usuario/arace-perfil');
  });
}

function configurarBanner() {
  document.getElementById('btn-share')?.addEventListener('click', async () => {
    const loja = window.AraceState?.getProducer();
    const shareData = {
      title: `${loja?.lojaNome || 'Loja Arace'} - Arace`,
      text: 'Conheca esta loja na plataforma Arace.',
      url: window.location.href,
    };

    if (navigator.share) {
      try {
        await navigator.share(shareData);
        return;
      } catch (error) {
        if (error.name === 'AbortError') return;
      }
    }

    try {
      await navigator.clipboard.writeText(window.location.href);
      showToast('Link copiado para a area de transferencia!');
    } catch {
      showToast('Nao foi possivel compartilhar agora.');
    }
  });

  document.getElementById('btn-edit-banner')?.addEventListener('click', () => {
    araceGo('produtor/configuracao-loja');
  });
}

function configurarAnimacoes() {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visivel');
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.item-animado').forEach(el => observer.observe(el));
}

document.addEventListener('DOMContentLoaded', () => {
  configurarAside();
  renderDadosLoja();
  configurarHeader();
  configurarProdutos();
  configurarBanner();
  configurarAnimacoes();
  atualizarCarrinho();
  lucide.createIcons();
});
