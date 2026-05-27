<!DOCTYPE html>
<html lang="pt-br" class="p-recuperar-senha">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aracê | Recuperar senha</title>
    <link rel="stylesheet" href="<?= base_url("css/style.css") ?>">
    <link rel="stylesheet" href="<?= base_url("css/auth/recuperar_senha.css") ?>">
</head>

<body>
    <main>
        <main>
            <div class="container-flex">
                <form action="#" method="POST" class="forms-blur">
                    <h1>Recuperar senha</h1>
                    <div>
                        <p>
                            Insira o e-mail para o qual será enviado o código de recuperação de senha
                        </p>

                        <input type="email" class="input-style-perfil" placeholder="E-mail">

                        <button type="submit">
                            Enviar e-mail
                        </button>
                        <a href="./login.html" class="link-voltar">
                            Voltar
                        </a>
                    </div>
                </form>
            </div>
        </main>
    </main>
</body>

</html>