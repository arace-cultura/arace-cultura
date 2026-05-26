<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aracê | Login</title>
    <link rel="stylesheet" href="<?= base_url("css/style.css") ?>">
</head>
<body>
    <main>
        
        <div class="container-flex">
        <img src="<?= base_url("images/bg_pedra_azul.jpg") ?>" class="bg_page" alt="Background">
        <form class="forms-blur">
           <h1>Login</h1>
           <div>
                <input type="email" class="input-style-perfil" placeholder="EMAIL">
                <input type="password" class="input-style-senha" placeholder="SENHA">
                <button>
                    Login
                </button>
           </div>
        </form>
        </div>
    </main>
</body>
</html>