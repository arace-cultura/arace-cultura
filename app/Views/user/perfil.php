<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="refresh" content="0; url=arace-perfil.php" />
  <title>Arace - Perfil</title>
</head>
<body>
  <script>
    // O PHP gera a URL e o JavaScript executa o redirecionamento
    window.location.replace("<?= url_to('user_arace_perfil') ?>");
</script>
  <a href="{{route('user_arace_perfil')}}">Ir para Perfil</a>
</body>
</html>
