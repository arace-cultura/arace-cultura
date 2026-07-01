// Interacoes da pagina de produto: galeria e adicionar ao carrinho via Firestore.

function trocarImagem(src) {
  const mainImg = document.getElementById('mainImg');
  if (mainImg) mainImg.src = src;
}

function ativarBotao(botao, seletor) {
  document.querySelectorAll(seletor).forEach(item => item.classList.remove('active'));
  botao.classList.add('active');
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.gallery-thumb, .variant-img-btn').forEach(botao => {
    botao.addEventListener('click', () => {
      const src = botao.dataset.image;
      if (!src) return;

      trocarImagem(src);
      ativarBotao(botao, botao.classList.contains('gallery-thumb') ? '.gallery-thumb' : '.variant-img-btn');
    });
  });

  document.querySelector('.btn-add')?.addEventListener('click', event => {
    const produtoId = event.currentTarget.dataset.produtoId;
    if (!produtoId) return;

    window.AraceState?.addCartItem(produtoId, 1).catch(() => {});
    event.currentTarget.innerHTML = '<i data-lucide="check"></i> Adicionado';
    lucide.createIcons();
  });

  lucide.createIcons();
});
