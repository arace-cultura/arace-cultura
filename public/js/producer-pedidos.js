const PEDIDOS = [
  { id: 4821, cliente: 'Ana Clara Silva', endereco: 'Vitoria, ES', produto: 'Kit Panela de Barro', qtd: 1, valor: 200.00, data: '02/06/2025', status: 'pendente' },
  { id: 4820, cliente: 'Marcos Oliveira', endereco: 'Serra, ES', produto: 'Panela de Barro Trad. M', qtd: 2, valor: 170.00, data: '01/06/2025', status: 'producao' },
  { id: 4819, cliente: 'Fernanda Costa', endereco: 'Cariacica, ES', produto: 'Prato de Ceramica', qtd: 3, valor: 135.00, data: '31/05/2025', status: 'enviado' },
  { id: 4818, cliente: 'Joao Pedro Matos', endereco: 'Cachoeiro de I., ES', produto: 'Kit Panela de Barro', qtd: 1, valor: 200.00, data: '30/05/2025', status: 'entregue' },
  { id: 4817, cliente: 'Luciana Ferreira', endereco: 'Linhares, ES', produto: 'Vaso Artesanal Grande', qtd: 1, valor: 95.00, data: '29/05/2025', status: 'pendente' },
  { id: 4816, cliente: 'Rafael Mendes', endereco: 'Colatina, ES', produto: 'Panela de Barro Trad. M', qtd: 4, valor: 340.00, data: '28/05/2025', status: 'cancelado' },
  { id: 4815, cliente: 'Beatriz Santos', endereco: 'Sao Mateus, ES', produto: 'Kit Panela de Barro', qtd: 2, valor: 400.00, data: '27/05/2025', status: 'entregue' },
  { id: 4814, cliente: 'Carlos Eduardo', endereco: 'Aracruz, ES', produto: 'Prato de Ceramica', qtd: 1, valor: 45.00, data: '26/05/2025', status: 'enviado' },
  { id: 4813, cliente: 'Patricia Lima', endereco: 'Guarapari, ES', produto: 'Vaso Artesanal Grande', qtd: 2, valor: 190.00, data: '25/05/2025', status: 'entregue' },
  { id: 4812, cliente: 'Diego Alves', endereco: 'Anchieta, ES', produto: 'Kit Panela de Barro', qtd: 1, valor: 200.00, data: '24/05/2025', status: 'producao' },
];

const STATUS_LABEL = { pendente: 'Pendente', producao: 'Em producao', enviado: 'Enviado', entregue: 'Entregue', cancelado: 'Cancelado' };
const STATUS_NEXT = { pendente: ['producao', 'cancelado'], producao: ['enviado', 'cancelado'], enviado: ['entregue'], entregue: [], cancelado: [] };
const STATUS_NEXT_LABEL = { producao: 'Iniciar producao', enviado: 'Marcar enviado', entregue: 'Confirmar entrega', cancelado: 'Cancelar pedido' };

let filtroStatus = 'todos';
let termoBusca = '';

function formatarMoeda(valor) {
  return `R$ ${valor.toFixed(2).replace('.', ',')}`;
}

function renderTabela() {
  const tbody = document.getElementById('pedidosBody');
  if (!tbody) return;

  const lista = PEDIDOS.filter(pedido => {
    const statusOk = filtroStatus === 'todos' || pedido.status === filtroStatus;
    const busca = termoBusca.toLowerCase();
    const buscaOk = !busca
      || String(pedido.id).includes(busca)
      || pedido.cliente.toLowerCase().includes(busca)
      || pedido.produto.toLowerCase().includes(busca);
    return statusOk && buscaOk;
  });

  if (!lista.length) {
    tbody.innerHTML = '<tr><td colspan="7" class="empty-row"><i data-lucide="inbox"></i> Nenhum pedido encontrado</td></tr>';
    lucide.createIcons();
    return;
  }

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

  lucide.createIcons();
}

function abrirModal(id) {
  const pedido = PEDIDOS.find(item => item.id === Number(id));
  if (!pedido) return;

  document.getElementById('modalTitulo').textContent = `Pedido #${pedido.id}`;
  document.getElementById('modalData').textContent = `Realizado em ${pedido.data}`;
  document.getElementById('modalCliente').textContent = pedido.cliente;
  document.getElementById('modalEndereco').textContent = pedido.endereco;
  document.getElementById('modalProduto').textContent = pedido.produto;
  document.getElementById('modalQtd').textContent = `Quantidade: ${pedido.qtd}`;
  document.getElementById('modalValor').textContent = formatarMoeda(pedido.valor);
  document.getElementById('modalStatus').innerHTML = `<span class="status-badge ${pedido.status}">${STATUS_LABEL[pedido.status]}</span>`;

  const actions = STATUS_NEXT[pedido.status].map(next => `
    <button class="btn-status-action ${next}" type="button" data-update-order="${pedido.id}" data-next-status="${next}">
      <i data-lucide="${next === 'cancelado' ? 'x-circle' : next === 'enviado' ? 'truck' : next === 'entregue' ? 'circle-check' : 'package'}"></i>
      ${STATUS_NEXT_LABEL[next]}
    </button>
  `).join('');

  document.getElementById('statusActions').innerHTML = actions || '<p class="modal-sub">Nenhuma acao disponivel</p>';
  document.getElementById('modalOverlay').classList.add('aberto');
  lucide.createIcons();
}

function fecharModal() {
  document.getElementById('modalOverlay')?.classList.remove('aberto');
}

function atualizarStatus(id, novoStatus) {
  const pedido = PEDIDOS.find(item => item.id === Number(id));
  if (!pedido) return;
  pedido.status = novoStatus;
  fecharModal();
  renderTabela();
}

function bindEventos() {
  document.getElementById('searchPedidos')?.addEventListener('input', event => {
    termoBusca = event.target.value;
    renderTabela();
  });

  document.querySelectorAll('.filter-chip').forEach(button => {
    button.addEventListener('click', () => {
      filtroStatus = button.dataset.status;
      document.querySelectorAll('.filter-chip').forEach(item => item.classList.remove('active'));
      button.classList.add('active');
      renderTabela();
    });
  });

  document.getElementById('pedidosBody')?.addEventListener('click', event => {
    const action = event.target.closest('[data-open-order]');
    if (action) {
      event.stopPropagation();
      abrirModal(action.dataset.openOrder);
      return;
    }

    const row = event.target.closest('.pedido-row');
    if (row) abrirModal(row.dataset.orderId);
  });

  const modal = document.getElementById('modalOverlay');
  modal?.addEventListener('click', event => {
    if (event.target === modal || event.target.closest('.modal-close')) fecharModal();

    const statusAction = event.target.closest('[data-update-order]');
    if (statusAction) {
      atualizarStatus(statusAction.dataset.updateOrder, statusAction.dataset.nextStatus);
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  bindEventos();
  renderTabela();
  lucide.createIcons();
});
