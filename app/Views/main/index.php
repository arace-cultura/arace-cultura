<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="refresh" content="0; url=arace-landing.php" />
  <title>Arace - Home</title>
</head>
<body>
<script>
    // O PHP gera a URL e o JavaScript executa o redirecionamento
    window.location.replace("<?= url_to('landing-page') ?>");
</script>


  <a href="{{ route('landing-page') }}">Ir para a Home</a>
</body>
</html>
