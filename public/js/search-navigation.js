
// PADRONIZAÇÃO E ACESSIBILIDADE DOS FORMULÁRIOS DE BUSCA NO CABEÇALHO

/**
 * Captura o valor digitado no input de busca e remove espaços extras no início e fim.
 * @param {HTMLInputElement|null} input - O elemento de entrada de texto.
 * @returns {string} O termo limpo ou uma string vazia caso o input não exista.
 */
function termoDeBusca(input) {
  return input ? input.value.trim() : '';
}

/**
 * Processa o termo pesquisado, monta a URL com os parâmetros corretos
 * e redireciona o usuário para a página de resultados.
 * @param {HTMLFormElement} form - O formulário que disparou a busca.
 */
function irParaBusca(form) {
  // Procura o input pelo nome padrão 'q' ou, em último caso, pelo tipo 'text'
  const input = form.querySelector('input[name="q"], input[type="text"]');
  
  // Cria um objeto de URL nativo baseado no 'action' do form ou rotas do AraceState.
  // O segundo argumento (window.location.href) garante a resolução correta caso o caminho seja relativo.
  const destino = new URL(form.getAttribute('action') || window.AraceState?.url('pesquisa') || 'pesquisa', window.location.href);
  
  // Verifica se o usuário já está na página de pesquisa
  const paginaAtualBusca = window.location.pathname.endsWith('/pesquisa');
  
  // ESTRATÉGIA DE PARÂMETROS: Se ele já estiver na pesquisa, preserva os filtros atuais (ex: categoria, preço).
  // Se estiver em outra página (home, produto), inicia uma lista de parâmetros limpa.
  const params = paginaAtualBusca ? new URLSearchParams(window.location.search) : new URLSearchParams();
  const termo = termoDeBusca(input);

  // Se houver algo digitado, adiciona ou atualiza o parâmetro 'q'. Se estiver vazio, remove 'q' da URL.
  if (termo) {
    params.set('q', termo);
  } else {
    params.delete('q');
  }

  // Atualiza a query string do objeto URL e redireciona o navegador
  destino.search = params.toString();
  window.location.href = destino.href;
}


// INICIALIZAÇÃO E ACESSIBILIDADE (A11Y)

document.addEventListener('DOMContentLoaded', () => {
  
  // Seleciona todos os formulários que possuem a classe de comportamento de busca
  document.querySelectorAll('.js-search-form').forEach(form => {
    const icon = form.querySelector('[data-lucide="search"]');

    // Intercepta o envio padrão do formulário (teclar Enter dentro do input ou clicar num botão submit)
    form.addEventListener('submit', event => {
      event.preventDefault(); // Impede o recarregamento síncrono padrão do HTML
      irParaBusca(form);
    });

    // --- TRATAMENTO DE ACESSIBILIDADE PARA O ÍCONE DA LUPA ---
    // Se a lupa for um elemento clicável (como um <svg> ou <i>) em vez de um <button> nativo,
    // precisamos injetar os atributos ARIA para que leitores de tela entendam o elemento.
    if (icon) {
      icon.setAttribute('role', 'button');      // Informa que o elemento se comporta como um botão
      icon.setAttribute('tabindex', '0');       // Permite que o elemento seja focado via teclado (tecla Tab)
      icon.setAttribute('aria-label', 'Pesquisar'); // Dá um nome textual ao botão para leitores de tela

      // Permite disparar a busca ao clicar fisicamente no ícone
      icon.addEventListener('click', () => irParaBusca(form));

      // Permite que usuários navegando por teclado disparem a busca usando as teclas Enter ou Espaço
      icon.addEventListener('keydown', event => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault(); // Evita o comportamento de scroll padrão da barra de espaço
          irParaBusca(form);
        }
      });
    }
  });
});