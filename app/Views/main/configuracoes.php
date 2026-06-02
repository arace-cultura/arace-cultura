<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="refresh" content="0; url=arace-config.php" />
  <title>Arace - Configuracoes</title>
</head>
<body>
  <script>
    // O PHP gera a URL e o JavaScript executa o redirecionamento
    window.location.replace("<?= url_to('main_arace_configuracao') ?>");
</script>
  <a href="{{routes('main_arace_configuracao')}}">Ir para Configuracoes</a>
</body>
</html>
