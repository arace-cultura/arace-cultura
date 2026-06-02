<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="refresh" content="0; url=arace-carrinho.php" />
  <title>Arace - Carrinho</title>
</head>
<body>
  <script>
    // O PHP gera a URL e o JavaScript executa o redirecionamento
    window.location.replace("<?= url_to('main_arace_carrinho') ?>");
</script>
  <a href="{{routes('main_arace_carrinho')}}">Ir para Carrinho</a>
</body>
</html>
