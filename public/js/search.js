
// INTERAÇÕES DA PÁGINA DE BUSCA: FILTROS E RANGE DE PREÇO


// Inicializa/converte as tags de ícones do Lucide presentes no HTML inicial da página
lucide.createIcons();

/**
 * Auxiliar para resolver URLs internas do ecossistema da aplicação.
 * Se o estado global AraceState possuir um método de mapeamento de rotas, ele é usado.
 */
const araceUrl = path => window.AraceState?.url(path) || path;


// LEITURA DOS PARÂMETROS DA URL (QUERY STRING)

const params = new URLSearchParams(window.location.search);
const q = params.get('q') || '';                    // Termo de busca digitado pelo usuário
const categoriaAtiva = params.get('categoria') || ''; // Categoria selecionada na URL

// Sincroniza o valor pesquisado em todos os inputs de busca da página (ex: busca do topo e busca lateral)
document.querySelectorAll('input[name="q"]').forEach(i => i.value = q);

// Dicionário de tradução dos slugs de categoria da URL para nomes amigáveis exibidos na tela
const LABELS = {
  pinturas: 'Pinturas', 'casa-e-vida': 'Casa & Vida', retro: 'Retro', joias: 'Joias',
  ceramica: 'Cerâmica', gastronomia: 'Gastronomia', roupas: 'Roupas', acessorios: 'Acessórios', artesanato: 'Artesanato'
};

// --- TRATAMENTO VISUAL DOS TÍTULOS E FILTROS ATIVOS ---

if (categoriaAtiva) {
  const label = LABELS[categoriaAtiva] || categoriaAtiva;

  // Se houver palavra-chave E categoria ativa, mostra a palavra. Se não, mostra o nome da categoria.
  document.getElementById('resultsTitle').textContent = q ? `Busca por "${q}"` : label;
  document.getElementById('activeFilterLabel').textContent = label;

  // Marca o checkbox e o card visual correspondentes à categoria atual da URL
  const cb = document.querySelector(`input[name="categoria"][value="${categoriaAtiva}"]`);
  const card = document.querySelector(`[data-category-card="${categoriaAtiva}"]`);
  if (cb) cb.checked = true;
  if (card) card.classList.add('active');
} else if (q) {
  document.getElementById('resultsTitle').textContent = `Busca por "${q}"`;
  document.getElementById('activeFilterLabel').textContent = 'Termo pesquisado';
}


// CONTROLE DE FILTROS E REDIRECIONAMENTO DE URL

// Configura botões de filtro rápido que possuem atributos diretos (ex: ordenar por preço menor/maior)
document.querySelectorAll('[data-filter-param]').forEach(btn => {
  // Se o parâmetro atual da URL bater com o valor do botão, marca o botão como ativo
  if (params.get(btn.dataset.filterParam) === btn.dataset.filterValue) btn.classList.add('active');

  btn.addEventListener('click', () => {
    const p = new URLSearchParams(window.location.search);
    p.set(btn.dataset.filterParam, btn.dataset.filterValue); // Insere ou atualiza o parâmetro na URL
    window.location.href = `${araceUrl('pesquisa')}?${p}`;    // Recarrega a página aplicando o filtro
  });
});

// Ação do botão principal "Aplicar Filtro" (Geralmente em modais ou barras laterais de filtro)
document.getElementById('btnAplicarFiltro').addEventListener('click', () => {
  const p = new URLSearchParams(window.location.search);
  const cat = document.querySelector('input[name="categoria"]:checked');

  // Se houver uma categoria checada, atualiza na URL; caso contrário, remove o parâmetro
  cat ? p.set('categoria', cat.value) : p.delete('categoria');

  // Recarrega a página com a nova combinação de parâmetros (se existirem)
  window.location.href = `${araceUrl('pesquisa')}${p.toString() ? '?' + p : ''}`;
});

// RENDERIZAÇÃO DO RANGE DUPLO DE PREÇO (SLIDER)
const rMin = document.getElementById('rangeMin');   // Input range do valor mínimo
const rMax = document.getElementById('rangeMax');   // Input range do valor máximo
const fill = document.getElementById('rangeFill');  // A faixa colorida que fica entre os dois ponteiros

/**
 * Calcula os percentuais, atualiza a faixa colorida do slider (CSS)
 * e sincroniza os textos/inputs escondidos de valor numérico na tela.
 */
function updateRange() {
  let min = parseInt(rMin.value), max = parseInt(rMax.value);

  // Inversão de segurança: se o usuário arrastar o mínimo para além do máximo, eles trocam de valor
  if (min > max) { const t = min; min = max; max = t; }

  // Transforma os valores em porcentagem baseados em um teto máximo de R$ 1000,00
  const pMin = (min / 1000) * 100;
  const pMax = (max / 1000) * 100;

  // Atualiza a posição inicial e a largura da faixa preenchida (CSS) dinamicamente
  fill.style.left  = pMin + '%';
  fill.style.width = (pMax - pMin) + '%';

  // Exibe o valor formatado textualmente para o usuário
  document.getElementById('valMin').textContent = 'R$' + min;
  document.getElementById('valMax').textContent = 'R$' + max;

  // Atualiza os inputs do tipo 'hidden' ou text que farão o envio do formulário
  document.getElementById('inputMin').value = min;
  document.getElementById('inputMax').value = max;
}

// Ouve as movimentações arrastadas nos seletores de preço para recalcular em tempo real
rMin.addEventListener('input', updateRange);
rMax.addEventListener('input', updateRange);

// Executa a primeira vez no load da página para desenhar o slider no estado correto vindo do HTML
updateRange();
