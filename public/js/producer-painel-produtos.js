
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

/**
 * Gerencia a abertura, fechamento, acessibilidade e envio assíncrono do 
 * modal de cadastro/edição de produtos.
 */
function configurarProdutoModal() {
  const modal = document.getElementById('produtoModal');
  const form = document.getElementById('produtoForm');
  const feedback = document.getElementById('produtoFormFeedback');

  // Encerra a função caso os elementos estruturais do modal não estejam no DOM da página
  if (!modal || !form) return;

  /**
   * Abre o modal, gerencia o estado para leitores de tela e joga o foco no primeiro input.
   */
  const abrir = () => {
    modal.classList.add('aberto');              // Ativa o posicionamento/transição via CSS
    modal.setAttribute('aria-hidden', 'false'); // Informa aos leitores de tela que o modal está visível
    feedback.textContent = '';                  // Limpa mensagens de sucesso ou erro anteriores
    
    // PRÁTICA DE ACESSIBILIDADE: Move automaticamente o cursor para o campo de nome,
    // poupando o usuário de ter que clicar manualmente no campo ao abrir o modal
    document.getElementById('produtoNome')?.focus();
  };

  /**
   * Fecha o modal e atualiza seu estado de visibilidade para os leitores de tela.
   */
  const fechar = () => {
    modal.classList.remove('aberto');
    modal.setAttribute('aria-hidden', 'true'); // Informa aos leitores de tela que o conteúdo está oculto
  };

  // Mapeia e distribui a ação de abertura em todos os gatilhos que possuem o atributo data correspondente
  document.querySelectorAll('[data-open-product-modal]').forEach(botao => {
    botao.addEventListener('click', abrir);
  });

  // Mapeia e distribui a ação de fechamento (geralmente nos botões "Cancelar" ou "X" do cabeçalho do modal)
  document.querySelectorAll('[data-close-product-modal]').forEach(botao => {
    botao.addEventListener('click', fechar);
  });

  // Fecha o modal caso o usuário clique diretamente no fundo escurecido (overlay) externo
  modal.addEventListener('click', event => {
    if (event.target === modal) fechar();
  });

  // --- SUBMISSÃO ASSÍNCRONA DO FORMULÁRIO ---
  form.addEventListener('submit', async event => {
    event.preventDefault(); // Impede o comportamento de reload síncrono padrão do formulário HTML

    const submit = form.querySelector('[type="submit"]');
    
    // FormData captura automaticamente todos os campos de input que possuem o atributo 'name'.
    // Ideal para formulários que possuem uploads de arquivo/imagens do produto.
    const data = new FormData(form);
    
    // Feedback visual de carregamento e bloqueio de segurança contra múltiplos cliques acidentais
    feedback.textContent = 'Salvando produto...';
    submit.disabled = true;

    try {
      const response = await fetch('/api/producer/products', {
        method: 'POST',
        body: data, // O FormData define o Content-Type como 'multipart/form-data' automaticamente
      });
      
      // Tenta ler o JSON de resposta da API; caso falhe (resposta vazia), retorna um objeto vazio
      const payload = await response.json().catch(() => ({}));

      // Se o status HTTP não estiver na faixa de sucesso (200-299), lança uma exceção com a mensagem do backend
      if (!response.ok) {
        throw new Error(payload.message || 'Não foi possível salvar o produto.');
      }

      // Sucesso na gravação (API/Firestore)
      feedback.textContent = 'Produto salvo no Firestore.';
      
      // Aguarda 600 milissegundos (tempo para o usuário ler o feedback) e recarrega a página para atualizar a grade
      setTimeout(() => window.location.reload(), 600);
      
    } catch (erro) {
      // Tratamento de falhas: exibe o texto do erro capturado e reativa o botão para nova tentativa
      feedback.textContent = erro.message;
      submit.disabled = false;
    }
  });
}


// INICIALIZAÇÃO DA APLICAÇÃO

document.addEventListener('DOMContentLoaded', () => {
  preencherDataHoje();      // Escreve a data atualizada no topo da página
  configurarNavegacao();    // Configura os ouvintes de rota nos cards com data-href
  configurarProdutoModal(); // Ativa os fluxos, acessibilidade e envio do formulário do modal
  lucide.createIcons();      // Inicializa os ícones do Lucide carregados no layout
});