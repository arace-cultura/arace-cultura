(function () {
  // Utilitarios de mascara e validacao usados nos formularios antes do envio ao backend.
  const BRASIL_API_BASE = 'https://brasilapi.com.br/api';

  function somenteDigitos(valor) {
    return (valor || '').replace(/\D/g, '');
  }

  function formatarTelefoneValor(valor) {
    const digitos = somenteDigitos(valor).slice(0, 11);

    if (digitos.length > 10) {
      return digitos.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3').trim();
    }

    if (digitos.length > 6) {
      return digitos.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3').trim();
    }

    if (digitos.length > 2) {
      return digitos.replace(/(\d{2})(\d{0,5})/, '($1) $2').trim();
    }

    return digitos;
  }

  function formatarCnpjValor(valor) {
    const digitos = somenteDigitos(valor).slice(0, 14);

    if (digitos.length > 12) {
      return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/, '$1.$2.$3/$4-$5');
    }

    if (digitos.length > 8) {
      return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{0,4})/, '$1.$2.$3/$4');
    }

    if (digitos.length > 5) {
      return digitos.replace(/(\d{2})(\d{3})(\d{0,3})/, '$1.$2.$3');
    }

    if (digitos.length > 2) {
      return digitos.replace(/(\d{2})(\d{0,3})/, '$1.$2');
    }

    return digitos;
  }

  function formatarCepValor(valor) {
    const digitos = somenteDigitos(valor).slice(0, 8);
    return digitos.length > 5 ? `${digitos.slice(0, 5)}-${digitos.slice(5)}` : digitos;
  }

  function formatarCpfValor(valor) {
    let digitos = somenteDigitos(valor).slice(0, 11);

    if (digitos.length > 9) {
      digitos = digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
    } else if (digitos.length > 6) {
      digitos = digitos.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
    } else if (digitos.length > 3) {
      digitos = digitos.replace(/(\d{3})(\d{0,3})/, '$1.$2');
    }

    return digitos;
  }

  function validarCpf(valor) {
    const numeros = somenteDigitos(valor);
    if (numeros.length !== 11 || /^(\d)\1+$/.test(numeros)) return false;

    for (let t = 9; t < 11; t++) {
      let soma = 0;
      for (let c = 0; c < t; c++) soma += Number(numeros[c]) * ((t + 1) - c);
      const digito = ((10 * soma) % 11) % 10;
      if (Number(numeros[t]) !== digito) return false;
    }

    return true;
  }

  async function buscarJson(caminho) {
    const resposta = await fetch(`${BRASIL_API_BASE}${caminho}`, {
      headers: { Accept: 'application/json' },
    });

    if (!resposta.ok) return null;
    return resposta.json();
  }

  async function validarTelefone(valor) {
    const digitos = somenteDigitos(valor);
    if (digitos.length !== 10 && digitos.length !== 11) return false;
    if (digitos.length === 11 && digitos[2] !== '9') return false;

    const ddd = digitos.slice(0, 2);
    try {
      return Boolean(await buscarJson(`/ddd/v1/${ddd}`));
    } catch {
      return true;
    }
  }

  async function validarCnpj(valor) {
    const digitos = somenteDigitos(valor);
    if (digitos === '') return true;
    if (digitos.length !== 14 || /^(\d)\1+$/.test(digitos)) return false;

    try {
      return Boolean(await buscarJson(`/cnpj/v1/${digitos}`));
    } catch {
      return true;
    }
  }

  async function buscarCep(valor) {
    const digitos = somenteDigitos(valor);
    if (digitos.length !== 8) return null;

    return buscarJson(`/cep/v2/${digitos}`);
  }

  function alternarErro(campo, erro, mostrar) {
    if (erro) erro.style.display = mostrar ? 'block' : 'none';
    if (campo) campo.style.borderColor = mostrar ? '#dc2626' : '';
  }

  function configurarTelefone(input, erro) {
    if (!input) return;

    input.addEventListener('input', () => {
      input.value = formatarTelefoneValor(input.value);
      alternarErro(input, erro, false);
    });

    input.addEventListener('blur', async () => {
      if (!input.value) return;
      alternarErro(input, erro, !(await validarTelefone(input.value)));
    });
  }

  function configurarCnpj(input, erro) {
    if (!input) return;

    input.addEventListener('input', () => {
      input.value = formatarCnpjValor(input.value);
      alternarErro(input, erro, false);
    });

    input.addEventListener('blur', async () => {
      if (!input.value) return;
      alternarErro(input, erro, !(await validarCnpj(input.value)));
    });
  }

  window.AraceBrasilApiValidation = {
    somenteDigitos,
    formatarCepValor,
    formatarCnpjValor,
    formatarCpfValor,
    formatarTelefoneValor,
    validarCnpj,
    validarCpf,
    validarTelefone,
    buscarCep,
    alternarErro,
    configurarCnpj,
    configurarTelefone,
  };
})();
