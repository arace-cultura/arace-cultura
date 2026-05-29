// Inicializa os ícones na primeira carga
lucide.createIcons();

function alternarSenha() {
  const campo = document.getElementById('senha');
  const icone = document.getElementById('icone-olho');

  if (campo.type === 'password') {
    campo.type = 'text';
    icone.setAttribute('data-lucide', 'eye-off');
  } else {
    campo.type = 'password';
    icone.setAttribute('data-lucide', 'eye');
  }
  
  // Força o Lucide a redesenhar o ícone trocado
  lucide.createIcons();
}