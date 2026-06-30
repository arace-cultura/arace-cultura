// Mantem a pagina do carrinho sincronizada com o Firestore via endpoints /api/cart.


function formatarMoeda(valor) {
  return Number(valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

async function atualizarItem(produtoId, quantidade) {
  const response = await fetch(`/api/cart/${encodeURIComponent(produtoId)}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    body: JSON.stringify({ quantidade }),
  });

  if (!response.ok) throw new Error('Nao foi possivel atualizar o carrinho.');
  const payload = await response.json();
  window.ARACE_CART = payload.data;
  return payload.data;
}

async function removerItem(produtoId) {
  const response = await fetch(`/api/cart/${encodeURIComponent(produtoId)}`, {
    method: 'DELETE',
    headers: { 'Accept': 'application/json' },
  });

  if (!response.ok) throw new Error('Nao foi possivel remover o item.');
  const payload = await response.json();
  window.ARACE_CART = payload.data;
  return payload.data;
}

// Atualiza totais e estado vazio usando o retorno oficial da API.


function atualizarResumo(cart) {
  const items = cart?.items || [];
  const totais = cart?.totais || {};

  document.getElementById('summarySubtotal').textContent = formatarMoeda(totais.subtotal);
  document.getElementById('summaryDiscount').textContent = `-${formatarMoeda(totais.desconto)}`;
  document.getElementById('summaryShipping').textContent = formatarMoeda(totais.frete);
  document.getElementById('summaryTotal').textContent = formatarMoeda(totais.total);

  document.querySelectorAll('.cart-count').forEach(label => {
    if (!label.closest('.cart-btn')?.querySelector('[data-lucide="shopping-cart"]')) return;
    const total = items.reduce((acc, item) => acc + Number(item.quantidade || 1), 0);
    label.textContent = total === 1 ? '1 item' : `${total} itens`;
  });

  const empty = document.getElementById('emptyCart');
  const content = document.getElementById('cartContent');
  empty?.classList.toggle('hidden', items.length > 0);
  content?.classList.toggle('hidden', items.length === 0);
}

// Liga botoes de quantidade/remocao aos endpoints do carrinho.


function bindCarrinho() {
  document.getElementById('cartContent')?.addEventListener('click', async event => {
    const button = event.target.closest('[data-item-id]');
    if (!button) return;

    const produtoId = button.dataset.itemId;
    const input = document.getElementById(`qty-${produtoId}`);
    const atual = Number(input?.value || 1);

    try {
      if (button.classList.contains('qty-plus')) {
        const cart = await atualizarItem(produtoId, atual + 1);
        if (input) input.value = atual + 1;
        atualizarResumo(cart);
      }

      if (button.classList.contains('qty-minus')) {
        const novaQuantidade = Math.max(1, atual - 1);
        const cart = await atualizarItem(produtoId, novaQuantidade);
        if (input) input.value = novaQuantidade;
        atualizarResumo(cart);
      }

      if (button.classList.contains('btn-remove-icon')) {
        const cart = await removerItem(produtoId);
        button.closest('.cart-item')?.remove();
        atualizarResumo(cart);
      }
    } catch {
      alert('Nao foi possivel atualizar o carrinho agora.');
    }
  });

  document.querySelectorAll('.qty-input').forEach(input => {
    input.addEventListener('change', async () => {
      const produtoId = input.id.replace(/^qty-/, '');
      const quantidade = Math.max(1, Number(input.value || 1));
      input.value = quantidade;

      try {
        atualizarResumo(await atualizarItem(produtoId, quantidade));
      } catch {
        alert('Nao foi possivel atualizar o carrinho agora.');
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  bindCarrinho();
  atualizarResumo(window.ARACE_CART);
});
