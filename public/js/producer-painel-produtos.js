function preencherDataHoje() {
  const alvo = document.getElementById('dataHoje');
  if (!alvo) return;

  alvo.textContent = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  });
}

function configurarNavegacao() {
  document.querySelectorAll('[data-href]').forEach(elemento => {
    elemento.addEventListener('click', event => {
      event.stopPropagation();
      window.location.href = elemento.dataset.href;
    });
  });
}

function configurarExclusaoMock() {
  document.querySelectorAll('.produto-mini-actions .del').forEach(button => {
    button.addEventListener('click', event => {
      event.stopPropagation();
      if (confirm('Remover este produto?')) {
        button.closest('.produto-mini')?.remove();
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  preencherDataHoje();
  configurarNavegacao();
  configurarExclusaoMock();
  lucide.createIcons();
});
