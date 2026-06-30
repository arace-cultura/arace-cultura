
// INTERAÇÕES DA PÁGINA DE BUSCA: FILTROS, RANGE DE PREÇO E FAVORITOS


// Inicializa/converte as tags de ícones do Lucide presentes no HTML inicial da página
lucide.createIcons();

/**
 * Auxiliar para resolver URLs internas do ecossistema da aplicação.
 * Se o estado global AraceState possuir um método de mapeamento de rotas, ele é usado.
 */
const araceUrl = path => window.AraceState?.url(path) || path;

/**
 * Extrai, calcula e normaliza os dados de um card de produto para um formato padronizado.
 * Se o card não possuir um ID, cria um de forma dinâmica baseado no nome (Slugify).
 * * @param {HTMLElement} card - O elemento DOM do card do produto.
 * @param {number} index - O índice do card atual na listagem (usado como fallback no ID).
 * @returns {Object} Objeto com os dados estruturados do produto.
 */
function produtoDoCard(card, index) {
  const nome = card.querySelector('.nome')?.textContent.trim() || 'Produto Arace';
  
  // Captura o preço textual, remove caracteres não numéricos e converte o padrão brasileiro (,) para americano (.)
  const precoTexto = card.querySelector('.preco strong, .preco s')?.textContent || '0';
  const preco = Number(precoTexto.replace(/[^\d,]/g, '').replace(',', '.')) || 0;
  
  // Resgata a cor de fundo do estilo do elemento ou usa uma cor padrão (nude/bege)
  const cor = card.querySelector('.produto-img')?.style.background || '#b5a898';
  
  // GERAÇÃO DE ID DINÂMICO: Se não houver data-produto-id, limpa o nome do produto tirando acentos, 
  // caracteres especiais e espaços para gerar uma string segura (Ex: "Vaso de Cerâmica" vira "search-vaso-de-ceramica-0")
  const id = card.dataset.produtoId || `search-${nome.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/\W+/g, '-')}-${index}`;
  card.dataset.produtoId = id; // Garante que o elemento HTML passe a ter esse ID no DOM
  
  return { 
    id, 
    nome, 
    preco, 
    cor, 
    img: card.dataset.img || '', 
    artesao: 'Arace', 
    categoria: categoriaAtiva || 'ceramica', 
    colecao: categoriaAtiva || 'ceramica', 
    estrelas: 4, 
    avaliacoes: 24 
  };
}

/**
 * Varre todos os produtos da busca, verifica seu estado de favoritismo no Firestore 
 * (via AraceState) e configura os eventos de clique para alternar o estado do favorito.
 */
function configurarFavoritosBusca() {
  document.querySelectorAll('.produto').forEach((card, index) => {
    const produto = produtoDoCard(card, index);
    const btn = card.querySelector('.fav');
    if (!btn) return;

    // Sincroniza o visual do botão baseado no estado atual gravado no Firestore/LocalStorage
    if (window.AraceState?.isFavorite(produto.id)) btn.classList.add('active');

    // Escuta o clique para favoritar/desfavoritar
    btn.addEventListener('click', event => {
      event.preventDefault();  // Evita que o clique dispare um link (caso o card seja um <a>)
      event.stopPropagation(); // Evita que o clique borbulhe para o card pai

      // Executa a inversão no Firestore (se disponível) ou apenas inverte a classe localmente
      const ativo = window.AraceState
        ? window.AraceState.toggleFavorite(produto)
        : !btn.classList.contains('active');
        
      btn.classList.toggle('active', ativo);
    });
  });
}


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

// Inicializa o bind de eventos nos botões de favoritar
configurarFavoritosBusca();


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