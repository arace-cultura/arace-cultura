
// COMPORTAMENTOS DO PAINEL DO PRODUTOR: NAVEGAÇÃO, DATA E CADASTRO


/**
 * Captura a data atual do sistema e a renderiza formatada por extenso em português.
 * Exemplo de saída: "terça-feira, 30 de junho"
 */
function preencherDataHoje() {
  const alvo = document.getElementById('dataHoje');
  if (!alvo) return; // Evita erros caso o elemento não exista na página atual

  // Formata o objeto Date nativo utilizando a API internacional do JavaScript (Intl)
  alvo.textContent = new Date().toLocaleDateString('pt-BR', {
    weekday: 'long', // Dia da semana por extenso
    day: 'numeric',  // Dia do mês em formato numérico
    month: 'long',   // Mês por extenso
  });
}

/**
 * Transforma qualquer elemento HTML comum em um link clicável (como cards informativos).
 * Procura pelo atributo 'data-href' e redireciona o navegador.
 */
function configurarNavegacao() {
  document.querySelectorAll('[data-href]').forEach(elemento => {
    elemento.addEventListener('click', event => {
      // Impede o borbulhamento do clique, garantindo que botões ou ações internas 
      // contidas no mesmo card não disparem a navegação por acidente
      event.stopPropagation();
      
      // Realiza o redirecionamento para o endereço mapeado no atributo customizado
      window.location.href = elemento.dataset.href;
    });
  });
}


// INICIALIZAÇÃO DA APLICAÇÃO

document.addEventListener('DOMContentLoaded', () => {
  preencherDataHoje();      // Escreve a data atualizada no topo da página
  configurarNavegacao();    // Configura os ouvintes de rota nos cards com data-href
  lucide.createIcons();      // Inicializa os ícones do Lucide carregados no layout
});
