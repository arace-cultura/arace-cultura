lucide.createIcons();

  // Favorito toggle
  document.querySelectorAll('.fav').forEach(btn =>
    btn.addEventListener('click', () => btn.classList.toggle('active'))
  );

  // Sincronizar queries nos dois inputs de busca
  const params = new URLSearchParams(window.location.search);
  const q = params.get('q') || '';
  const categoriaAtiva = params.get('categoria') || '';
  document.querySelectorAll('input[name="q"]').forEach(i => i.value = q);

  const LABELS = { pinturas:'Pinturas','casa-e-vida':'Casa & Vida', retro:'Retro', joias:'Joias',
    ceramica:'Cerâmica', gastronomia:'Gastronomia', roupas:'Roupas', acessorios:'Acessórios', artesanato:'Artesanato' };

  if (categoriaAtiva) {
    const label = LABELS[categoriaAtiva] || categoriaAtiva;
    document.getElementById('resultsTitle').textContent = q ? `Busca por "${q}"` : label;
    document.getElementById('activeFilterLabel').textContent = label;
    const cb = document.querySelector(`input[name="categoria"][value="${categoriaAtiva}"]`);
    const card = document.querySelector(`[data-category-card="${categoriaAtiva}"]`);
    if (cb) cb.checked = true;
    if (card) card.classList.add('active');
  } else if (q) {
    document.getElementById('resultsTitle').textContent = `Busca por "${q}"`;
    document.getElementById('activeFilterLabel').textContent = 'Termo pesquisado';
  }

  // Filtros com data-filter-param
  document.querySelectorAll('[data-filter-param]').forEach(btn => {
    if (params.get(btn.dataset.filterParam) === btn.dataset.filterValue) btn.classList.add('active');
    btn.addEventListener('click', () => {
      const p = new URLSearchParams(window.location.search);
      p.set(btn.dataset.filterParam, btn.dataset.filterValue);
      window.location.href = `arace-search.html?${p}`;
    });
  });

  // Aplicar filtro
  document.getElementById('btnAplicarFiltro').addEventListener('click', () => {
    const p = new URLSearchParams(window.location.search);
    const cat = document.querySelector('input[name="categoria"]:checked');
    cat ? p.set('categoria', cat.value) : p.delete('categoria');
    window.location.href = `arace-search.html${p.toString() ? '?' + p : ''}`;
  });

  // Range duplo
  const rMin = document.getElementById('rangeMin');
  const rMax = document.getElementById('rangeMax');
  const fill  = document.getElementById('rangeFill');

  function updateRange() {
    let min = parseInt(rMin.value), max = parseInt(rMax.value);
    if (min > max) { const t = min; min = max; max = t; }
    const pMin = (min / 1000) * 100;
    const pMax = (max / 1000) * 100;
    fill.style.left  = pMin + '%';
    fill.style.width = (pMax - pMin) + '%';
    document.getElementById('valMin').textContent = 'R$' + min;
    document.getElementById('valMax').textContent = 'R$' + max;
    document.getElementById('inputMin').value = min;
    document.getElementById('inputMax').value = max;
  }
  rMin.addEventListener('input', updateRange);
  rMax.addEventListener('input', updateRange);
  updateRange();