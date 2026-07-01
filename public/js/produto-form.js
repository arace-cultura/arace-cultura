function formatarPreco(valor) {
  const numero = Number(String(valor || 0).replace(',', '.')) || 0;
  return numero.toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
}

function textoOuPadrao(valor, padrao) {
  const texto = String(valor || '').trim();
  return texto === '' ? padrao : texto;
}

function configurarPreviewProduto() {
  const nome = document.getElementById('produtoNome');
  const descricao = document.getElementById('produtoDescricao');
  const preco = document.getElementById('produtoPreco');
  const categoria = document.getElementById('produtoCategoria');
  const cor = document.getElementById('produtoCor');
  const arquivo = document.getElementById('produtoImagemArquivo');
  const imagemUrl = document.getElementById('produtoImagemUrl');

  const previewNome = document.getElementById('produtoPreviewNome');
  const previewDescricao = document.getElementById('produtoPreviewDescricao');
  const previewPreco = document.getElementById('produtoPreviewPreco');
  const previewCategoria = document.getElementById('produtoPreviewCategoria');
  const previewImage = document.getElementById('produtoPreviewImage');

  if (!previewImage) return;

  function renderImagem(src) {
    if (src) {
      const img = document.createElement('img');
      img.src = src;
      img.alt = 'Prévia do produto';
      previewImage.replaceChildren(img);
      return;
    }

    previewImage.style.background = cor?.value || '#b5a898';
    previewImage.innerHTML = '<i data-lucide="image"></i>';
    window.lucide?.createIcons();
  }

  function atualizar() {
    if (previewNome) previewNome.textContent = textoOuPadrao(nome?.value, 'Nome do produto');
    if (previewDescricao) {
      previewDescricao.textContent = textoOuPadrao(
        descricao?.value,
        'A descrição do produto aparecerá aqui enquanto você preenche o cadastro.'
      );
    }
    if (previewPreco) previewPreco.textContent = formatarPreco(preco?.value);
    if (previewCategoria) previewCategoria.textContent = textoOuPadrao(categoria?.value, 'Categoria');

    if (imagemUrl?.value) {
      renderImagem(imagemUrl.value);
    } else if (!arquivo?.files?.length) {
      renderImagem('');
    }
  }

  [nome, descricao, preco, categoria, cor, imagemUrl].forEach(campo => {
    campo?.addEventListener('input', atualizar);
  });

  arquivo?.addEventListener('change', () => {
    const file = arquivo.files?.[0];
    if (!file) {
      atualizar();
      return;
    }

    renderImagem(URL.createObjectURL(file));
  });

  atualizar();
}

document.addEventListener('DOMContentLoaded', configurarPreviewProduto);
