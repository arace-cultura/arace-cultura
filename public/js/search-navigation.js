function termoDeBusca(input) {
  return input ? input.value.trim() : '';
}

function irParaBusca(form) {
  const input = form.querySelector('input[name="q"], input[type="text"]');
  const destino = new URL(form.getAttribute('action') || 'arace-search.html', window.location.href);
  const paginaAtualBusca = window.location.pathname.endsWith('/arace-search.html');
  const params = paginaAtualBusca ? new URLSearchParams(window.location.search) : new URLSearchParams();
  const termo = termoDeBusca(input);

  if (termo) {
    params.set('q', termo);
  } else {
    params.delete('q');
  }

  destino.search = params.toString();
  window.location.href = destino.href;
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.js-search-form').forEach(form => {
    const icon = form.querySelector('[data-lucide="search"]');

    form.addEventListener('submit', event => {
      event.preventDefault();
      irParaBusca(form);
    });

    if (icon) {
      icon.setAttribute('role', 'button');
      icon.setAttribute('tabindex', '0');
      icon.setAttribute('aria-label', 'Pesquisar');
      icon.addEventListener('click', () => irParaBusca(form));
      icon.addEventListener('keydown', event => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault();
          irParaBusca(form);
        }
      });
    }
  });
});
