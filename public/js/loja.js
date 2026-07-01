// Interacoes da pagina de loja do produtor. Produtos e carrinho usam dados do Firestore.
const lojaState = {
  cartCount: (window.ARACE_CART?.items || []).reduce((total, item) => total + Number(item.quantidade || 1), 0),
};

const araceUrl = path => window.AraceState?.url(path) || path;
const araceGo = path => window.AraceState?.go(path) || (window.location.href = path);

function atualizarCarrinho() {
  const label = document.getElementById('cart-label');
  if (label) label.textContent = lojaState.cartCount === 1 ? '1 item' : `${lojaState.cartCount} itens`;
}

function asideCliente() {
  const produtor = [true, 1, '1', 'true'].includes(window.ARACE_AUTH_USER?.isProdutor);
  const cadastroProdutor = produtor ? '' : `<a class="nav-item" href="${araceUrl('cadastro/produtor')}"><i data-lucide="box"></i> Quero ser produtor</a>`;

  return `
    <a class="nav-item" href="${araceUrl('')}"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="${araceUrl('pesquisa')}"><i data-lucide="shopping-bag"></i> Produtos</a>
    <a class="nav-item" href="${araceUrl('arace-carrinho')}"><i data-lucide="shopping-cart"></i> Carrinho</a>
    <a class="nav-item" href="${araceUrl('arace-config')}"><i data-lucide="settings"></i> Configurações</a>
    <a class="nav-item" href="${araceUrl('usuario/arace-perfil')}"><i data-lucide="user"></i> Perfil</a>
    ${cadastroProdutor}
    <form class="logout-form" action="${araceUrl('sair')}" method="post">
      <button class="nav-item logout-button" type="submit"><i data-lucide="log-out"></i> Sair da conta</button>
    </form>
  `;
}

function asideProdutor() {
  return `
    <a class="nav-item" href="${araceUrl('')}"><i data-lucide="house"></i> Home page</a>
    <a class="nav-item" href="${araceUrl('produtor/painel')}"><i data-lucide="layout-dashboard"></i> Painel</a>
    <a class="nav-item" href="${araceUrl('produtor/produtos/novo')}"><i data-lucide="plus"></i> Criar produto</a>
    <a class="nav-item" href="${araceUrl('produtor/painel')}"><i data-lucide="shopping-bag"></i> Meus produtos</a>
    <a class="nav-item" href="${araceUrl('produtor/pedidos')}"><i data-lucide="package"></i> Pedidos</a>
    <a class="nav-item active" href="${araceUrl('produtor/perfil-loja')}" aria-current="page"><i data-lucide="store"></i> Minha loja</a>
    <div class="nav-divider"></div>
    <a class="nav-item" href="${araceUrl('produtor/perfil')}"><i data-lucide="user"></i> Perfil</a>
    <a class="nav-item" href="${araceUrl('produtor/configuracao-loja')}"><i data-lucide="settings"></i> Configurações da loja</a>
    <form class="logout-form" action="${araceUrl('sair')}" method="post">
      <button class="nav-item logout-button" type="submit"><i data-lucide="log-out"></i> Sair da conta</button>
    </form>
  `;
}

function configurarAside() {
  const aside = document.querySelector('aside');
  if (!aside || !window.AraceState) return;

  aside.innerHTML = asideProdutor();
}

function configurarProdutos() {
  document.querySelectorAll('.product-card').forEach(card => {
    card.dataset.produtoId = card.dataset.produtoId || card.dataset.id || '';
  });
}

function configurarHeader() {
  document.getElementById('btn-cart')?.addEventListener('click', () => {
    araceGo('arace-carrinho');
  });

  document.querySelector('header .avatar-btn')?.addEventListener('click', () => {
    araceGo('usuario/arace-perfil');
  });
}

function configurarBanner() {
  document.getElementById('btn-config-loja')?.addEventListener('click', () => {
    araceGo('arace-config-producer-loja');
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
  configurarHeader();
  configurarProdutos();
  configurarBanner();
  configurarAnimacoes();
  atualizarCarrinho();
  lucide.createIcons();
});
