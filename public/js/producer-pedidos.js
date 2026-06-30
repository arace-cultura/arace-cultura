
// LISTAGEM E ATUALIZAÇÃO DE PEDIDOS DO PRODUTOR (INTEGRAÇÃO API / FIRESTORE)

// Recupera os pedidos injetados globalmente pelo PHP no HTML ou inicializa um array vazio
let PEDIDOS = Array.isArray(window.ARACE_PEDIDOS) ? window.ARACE_PEDIDOS : [];

// MÁQUINA DE ESTADOS: Dicionários que ditam as regras de negócio do fluxo do pedido
const STATUS_LABEL = { pendente: 'Pendente', producao: 'Em produção', enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado' };

// Define quais são os próximos status válidos a partir do status atual (regragem de transição)
const STATUS_NEXT = { 
  pendente: ['producao', 'cancelado'], 
  producao: ['enviado', 'cancelado'], 
  enviado: ['entregue'], 
  entregue: [], 
  cancelado: [] 
};

// Define os textos amigáveis que aparecerão dentro dos botões de ação do modal
const STATUS_NEXT_LABEL = { producao: 'Iniciar produção', enviado: 'Marcar enviado', entregue: 'Confirmar entrega', cancelado: 'Cancelar pedido' };

// Variáveis de estado da interface para controle de filtros ativos
let filtroStatus = 'todos';
let termoBusca = '';

/**
 * Formata um valor numérico para o padrão de moeda brasileiro (R$ 0,00).
 * @param {number} valor - O preço numérico.
 * @returns {string} Valor formatado.
 */
function formatarMoeda(valor) {
  return `R$ ${valor.toFixed(2).replace('.', ',')}`;
}

/**
 * Filtra a lista de pedidos pelos critérios ativos e renderiza as linhas no corpo da tabela.
 */
function renderTabela() {
  const tbody = document.getElementById('pedidosBody');
  if (!tbody) return; // Encerra se a tabela não estiver na página atual

  // Aplica os filtros combinados de Status (Tabs/Chips) e Busca por texto
  const lista = PEDIDOS.filter(pedido => {
    const statusOk = filtroStatus === 'todos' || pedido.status === filtroStatus;
    const busca = termoBusca.toLowerCase();
    
    // A busca avalia se o termo bate com o ID, o nome do cliente ou o nome do produto
    const buscaOk = !busca
      || String(pedido.id).includes(busca)
      || pedido.cliente.toLowerCase().includes(busca)
      || pedido.produto.toLowerCase().includes(busca);
      
    return statusOk && buscaOk;
  });

  // Estado Vazio: Trata o visual se nenhum pedido corresponder aos filtros aplicados
  if (!lista.length) {
    tbody.innerHTML = '<tr><td colspan="7" class="empty-row"><i data-lucide="inbox"></i> Nenhum pedido encontrado</td></tr>';
    atualizarResumo(lista.length);
    lucide.createIcons(); // Reconstrói o ícone de caixa vazia do Lucide
    return;
  }

  // Renderiza as linhas da tabela mapeando o array de dados para strings HTML

  tbody.innerHTML = lista.map(pedido => `
    <tr class="pedido-row" data-order-id="${pedido.id}">
      <td><span class="pedido-id">#${pedido.id}</span></td>
      <td>
        <div class="cliente-cell">
          <div class="cliente-avatar">${pedido.cliente.split(' ').map(word => word[0]).slice(0, 2).join('')}</div>
          <div>
            <div class="cliente-nome">${pedido.cliente}</div>
            <div class="cliente-local">${pedido.endereco}</div>
          </div>
        </div>
      </td>
      <td class="produto-cell">${pedido.produto} <span class="qtd-badge">x${pedido.qtd}</span></td>
      <td class="valor-cell">${formatarMoeda(pedido.valor)}</td>
      <td class="data-cell">${pedido.data}</td>
      <td><span class="status-badge ${pedido.status}">${STATUS_LABEL[pedido.status]}</span></td>
      <td>
        <div class="row-actions">
          <button class="row-action-btn" type="button" data-open-order="${pedido.id}" title="Ver detalhes"><i data-lucide="eye"></i></button>
        </div>
      </td>
    </tr>
  `).join('');

  lucide.createIcons(); // Processa os novos ícones de olho inseridos nas linhas
  atualizarResumo(lista.length);
}

/**
 * Calcula os totais de pedidos por categoria para os contadores superiores e atualiza o texto de paginação.
 * @param {number} totalFiltrado - Total de itens exibidos após os filtros.
 */
function atualizarResumo(totalFiltrado) {
  // Reduz a lista completa acumulando a contagem absoluta de cada status disponível
  const counts = PEDIDOS.reduce((acc, pedido) => {
    acc[pedido.status] = (acc[pedido.status] || 0) + 1;
    return acc;
  }, {});

  // Atualiza dinamicamente os números exibidos nas caixas de resumo do topo da tela
  ['pendente', 'producao', 'enviado', 'entregue'].forEach(status => {
    const el = document.getElementById(`cnt-${status}`);
    if (el) el.textContent = counts[status] || 0;
  });

  // Atualiza a legenda informativa de paginação/resultados da listagem
  const info = document.getElementById('paginacaoInfo');
  if (info) info.textContent = totalFiltrado ? `Mostrando 1-${totalFiltrado} de ${PEDIDOS.length} pedidos` : 'Mostrando 0 de 0 pedidos';
}

/**
 * Preenche e abre o modal detalhado com as informações do pedido selecionado.
 * @param {string|number} id - ID do pedido a ser detalhado.
 */
function abrirModal(id) {
  const pedido = PEDIDOS.find(item => String(item.id) === String(id));
  if (!pedido) return;

  // Injeta os dados textuais do pedido nos campos do modal
  document.getElementById('modalTitulo').textContent = `Pedido #${pedido.id}`;
  document.getElementById('modalData').textContent = `Realizado em ${pedido.data}`;
  document.getElementById('modalCliente').textContent = pedido.cliente;
  document.getElementById('modalEndereco').textContent = pedido.endereco;
  document.getElementById('modalProduto').textContent = pedido.produto;
  document.getElementById('modalQtd').textContent = `Quantidade: ${pedido.qtd}`;
  document.getElementById('modalValor').textContent = formatarMoeda(pedido.valor);
  document.getElementById('modalStatus').innerHTML = `<span class="status-badge ${pedido.status}">${STATUS_LABEL[pedido.status]}</span>`;

  // Monta dinamicamente os botões de ação permitidos baseando-se na máquina de estados (STATUS_NEXT)
  const actions = STATUS_NEXT[pedido.status].map(next => `
    <button class="btn-status-action ${next}" type="button" data-update-order="${pedido.id}" data-next-status="${next}">
      <i data-lucide="${next === 'cancelado' ? 'x-circle' : next === 'enviado' ? 'truck' : next === 'entregue' ? 'circle-check' : 'package'}"></i>
      ${STATUS_NEXT_LABEL[next]}
    </button>
  `).join('');

  document.getElementById('statusActions').innerHTML = actions || '<p class="modal-sub">Nenhuma ação disponível</p>';
  document.getElementById('modalOverlay').classList.add('aberto'); // Exibe o modal via CSS
  lucide.createIcons(); // Carrega os ícones específicos do botão injetado
}

/**
 * Remove a classe visual que mantém o modal visível na tela.
 */
function fecharModal() {
  document.getElementById('modalOverlay')?.classList.remove('aberto');
}

/**
 * Envia uma requisição PATCH assíncrona para atualizar o status do pedido no servidor/Firestore.
 * @param {string|number} id - ID do pedido.
 * @param {string} novoStatus - O novo status para transição.
 */
async function atualizarStatus(id, novoStatus) {
  const pedido = PEDIDOS.find(item => String(item.id) === String(id));
  if (!pedido) return;

  try {
    const response = await fetch(`/api/producer/orders/${encodeURIComponent(id)}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ status: novoStatus }),
    });

    if (response.ok) {
      const payload = await response.json();
      // Se a API retornar a lista atualizada de pedidos do Firestore, substitui na memória local
      if (Array.isArray(payload.data)) PEDIDOS = payload.data;
    } else {
      // Fallback Otimista: Se der erro HTTP (ex: 400 ou 500), força a alteração local na tela para o usuário
      pedido.status = novoStatus;
    }
  } catch {
    // Fallback Offline: Se a internet cair, altera localmente para garantir o fluxo de uso da interface
    pedido.status = novoStatus;
  }

  // Garante a aplicação do novo status, limpa a UI fechando o modal e renderiza as tabelas e somatórias atualizadas
  pedido.status = novoStatus;
  fecharModal();
  renderTabela();
}

/**
 * Acopla os ouvintes de evento e gerencia delegações de clique para filtros e ações da tabela.
 */
function bindEventos() {
  // Filtro por texto: Ouve a digitação na barra de buscas
  document.getElementById('searchPedidos')?.addEventListener('input', event => {
    termoBusca = event.target.value;
    renderTabela();
  });

  // Filtro por Status (Chips): Gerencia a troca visual e lógica da categoria de visualização
  document.querySelectorAll('.filter-chip').forEach(button => {
    button.addEventListener('click', () => {
      filtroStatus = button.dataset.status;
      document.querySelectorAll('.filter-chip').forEach(item => item.classList.remove('active'));
      button.classList.add('active');
      renderTabela();
    });
  });

  // Delegação de eventos no Corpo da Tabela: Detecta cliques nas linhas ou no botão específico de 'Ver'
  document.getElementById('pedidosBody')?.addEventListener('click', event => {
    const action = event.target.closest('[data-open-order]');
    if (action) {
      event.stopPropagation(); // Evita o disparo duplicado (já que o botão está dentro da própria linha)
      abrirModal(action.dataset.openOrder);
      return;
    }

    const row = event.target.closest('.pedido-row');
    if (row) abrirModal(row.dataset.orderId);
  });

  // Delegação de eventos no Modal Overlay: Fecha ao clicar fora do conteúdo ou em botões internos de status
  const modal = document.getElementById('modalOverlay');
  modal?.addEventListener('click', event => {
    // Fecha se clicar estritamente no fundo escuro ou no botão 'X' (modal-close)
    if (event.target === modal || event.target.closest('.modal-close')) fecharModal();

    // Captura os cliques nos botões dinâmicos de alteração de status dentro do modal
    const statusAction = event.target.closest('[data-update-order]');
    if (statusAction) {
      atualizarStatus(statusAction.dataset.updateOrder, statusAction.dataset.nextStatus);
    }
  });
}


// INICIALIZAÇÃO DA APLICAÇÃO

document.addEventListener('DOMContentLoaded', () => {
  bindEventos();       // Ativa e distribui todos os ouvintes de eventos da tela
  renderTabela();      // Faz a primeira plotagem dos dados herdados do backend PHP na tabela
  lucide.createIcons(); // Processa os ícones gerais da estrutura base da página
});