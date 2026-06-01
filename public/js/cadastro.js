/* Inicializa os ícones Lucide */
lucide.createIcons();

/* MOSTRAR / OCULTAR SENHA - Reutilizável para os dois campos de senha */
function alternarSenha(campoId, iconeId) {
  const campo = document.getElementById(campoId);
  const icone = document.getElementById(iconeId);

  if (campo.type === 'password') {
    campo.type = 'text';
    icone.setAttribute('data-lucide', 'eye-off');
  } else {
    campo.type = 'password';
    icone.setAttribute('data-lucide', 'eye');
  }

  lucide.createIcons();
}

/*ENVIO DO FORMULÁRIO — INTEGRAÇÃO COM BANCO DE DADOS*/
document.getElementById('formCadastro').addEventListener('submit', async function (e) {
  e.preventDefault();

  const nome           = document.getElementById('nome').value.trim();
  const email          = document.getElementById('email').value.trim();
  const telefone       = document.getElementById('telefone').value.trim();
  const senha          = document.getElementById('senha').value;
  const confirmarSenha = document.getElementById('confirmarSenha').value;
  const termos         = document.getElementById('termos').checked;
  const erroSenha      = document.getElementById('erro-senha');

  /* Validação: senhas iguais */
  if (senha !== confirmarSenha) {
    erroSenha.style.display = 'block';
    return;
  } else {
    erroSenha.style.display = 'none';
  }

  /* Validação: termos aceitos */
  if (!termos) {
    alert('Você precisa aceitar os termos de uso.');
    return;
  }

  // -------------------------------------------------------
  // EXEMPLO: requisição para sua API / backend
  // -------------------------------------------------------
  /*
  try {
    const resposta = await fetch('https://sua-api.com/auth/cadastro', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, email, telefone, senha })
    });

    const dados = await resposta.json();

    if (resposta.ok) {
      window.location.href = 'login.html';
    } else {
      alert(dados.mensagem || 'Erro ao criar conta.');
    }
  } catch (erro) {
    console.error('Erro ao cadastrar:', erro);
    alert('Erro de conexão. Tente novamente.');
  }
  */

  //conectar ao banco de dados
  alert(`Conta criada para: ${nome} (${email})`);
});