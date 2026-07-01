
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


// CARD DO PRODUTO CLICÁVEL

// Faz o card inteiro do produto navegar para a página de detalhes ao ser clicado.
document.querySelectorAll('.produto[data-href]').forEach(card => {
  card.style.cursor = 'pointer';
  card.addEventListener('click', e => {
    // Ignora se o clique foi em um link interno (ex: o nome), que já leva ao destino
    if (e.target.closest('a')) return;
    window.location.href = card.dataset.href;
  });
});

