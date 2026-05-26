<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Aracê - Cultura Capixaba</title>
    <link rel="stylesheet" href="<?= base_url("css/style2.css") ?>"></head>
<body>

<!--Menu superior-->

    <header>
    <nav class="navbar">
        <div class="menu-icon">
            <img src="/icons/menu_ic.svg" alt="Menu">
        </div>

        <div class="nav-right">
            <div class="cart-info">
                <img src="/icons/carrinho_ic.svg" alt="Carrinho"><br>
                <div class="linha-vertical"></div>
                <span>2 itens</span>
            </div>
            <div class="user-profile">
                <img src="/icons/perfil_ic.svg" alt="Perfil">
            </div>
        </div>
    </nav>
</header>

<!--hero-->
<section class="hero-banner">
    <div class="hero-content">
        <h1>aracê</h1>
        <p>Um espaço da cultura capixaba.</p>
        <p class="hero-cta">
            Leve parte <span class="highlight">desse lugar.</span>
        </p>
    </div>
</section>

<!--Barra de pesquisa-->

<div class="search-section">
    <form class="search-container" id="searchForm">
        <span class="search-icon">
            <img src="/icons/Search_ic.svg" alt="Lupa"></span>
        <input type="text" id="searchInput" placeholder="pesquise um produto...">
    </form>
</div>

    <!--Sessão de categorias-->

<section class="categories-section">
    <h2 class="title-categories">categorias</h2>

    <div class="categories-grid">

        <!--Verdes-->

        <a href="catalogo.html?cat=pinturas" class="cat-item">
    <div class="icon-box bg-green">
        <img src="/icons/pallete.svg" alt="Pinturas">
    </div>
    <span>Pinturas</span>
</a>

<a href="catalogo.html?cat=casa-vida" class="cat-item">
    <div class="icon-box bg-green">
        <img src="/icons/image_2.svg" alt="Casa & Vida">
    </div>
    <span>Casa & Vida</span>
</a>

<a href="catalogo.html?cat=retro" class="cat-item">
    <div class="icon-box bg-green">
        <img src="/icons/cassette_ic.svg" alt="Retrô">
    </div>
    <span>Retrô</span>
</a>

<a href="catalogo.html?cat=ecodesign" class="cat-item">
    <div class="icon-box bg-green">
        <img src="/icons/eco-light_ic.svg" alt="Ecodesign">
    </div>
    <span>Ecodesign</span>
</a>

<!--Laranjas-->

<a href="catalogo.html?cat=roupas" class="cat-item">
    <div class="icon-box bg-orange">
        <img src="/icons/tshirt 1.svg" alt="Roupas">
    </div>
    <span>Roupas</span>
</a>

<a href="catalogo.html?cat=acessorios" class="cat-item">
    <div class="icon-box bg-orange">
        <img src="/icons/necklace 1.svg" alt="Acessórios">
    </div>
    <span>Acessórios</span>
</a>

<a href="catalogo.html?cat=ceramica" class="cat-item">
    <div class="icon-box bg-orange">
        <img src="/icons/vase 1.svg" alt="Cerâmica">
    </div>
    <span>Cerâmica</span>
</a>

<a href="catalogo.html?cat=gastronomia" class="cat-item">
    <div class="icon-box bg-orange">
        <img src="/icons/cutlery_ic.svg" alt="Gastronomia">
    </div>
    <span>Gastronomia</span>
</a>

        </div>
</section>

<!--Fim de categorias-->



        <section id="produtos">
            <h2 class="title-products">produtos em destaque</h2>
            
            <!--Aqui adicionaremos os produtos-->

            

           <!-- Modelo do card do produto - Copie e use 
                       <div class="product-card">
                    <img src="/xxxx" class="product-img" alt="xxxx">
                
                <div class="product-name">
                    <h3 class="product-title"> Nome nome </h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">000</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>
           -->

           <div class="product-card">
                    <img src="/images/produtos/panela_convento.png" class="product-img" alt="Panela de barro personalizada com o desenho do Convento da Penha">
                
                <div class="product-name">
                    <h3 class="product-title"> Panela de barro personalizada</h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">125</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>

            <div class="product-card">
                    <img src="/images/produtos/panela_tres.png" class="product-img" alt="Kit com três panelas de barro de tamanhos variados">
                
                <div class="product-name">
                    <h3 class="product-title"> Kit com três panelas de barro </h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">200</span>
                    <span class="decimals">50</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>


            <div class="product-card">
                    <img src="/images/produtos/bracelete_joia.png" class="product-img" alt="Bracelete trançado com uma jóia brilhante">
                
                <div class="product-name">
                    <h3 class="product-title"> Bracelete trançado</h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">50</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>


            <div class="product-card">
                    <img src="/images/produtos/bracelete_flor.png" class="product-img" alt="Braceletes de flor, um dourado e outro prata">
                
                <div class="product-name">
                    <h3 class="product-title"> Braceletes de flor </h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">75</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>


            <div class="product-card">
                    <img src="/images/produtos/bolsa_beijaflor.png" class="product-img" alt="Bolsa azul com o bordado de um beija-flor">
                
                <div class="product-name">
                    <h3 class="product-title"> Bolsa de Beija-Flor</h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">30</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>


            <div class="product-card">
                    <img src="/images/produtos/bolsa_convento.png" class="product-img" alt="Bolsa estampada com o desenho do Convento da Penha">
                
                <div class="product-name">
                    <h3 class="product-title"> Bolsa Convento da Penha</h3>
                </div>
                <div class="product-rating">
                    <span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span>
                </div>

                <!-- Essa parte precisaria do BD, por enquanto fica só o texto mesmo-->
                <div class="product-price">
                    <span class="currency">R$</span>
                    <span class="value">35</span>
                    <span class="decimals">00</span>
                </div>

                <button class="add-cart">Adicionar ao carrinho</button>
            </div>

        </div>

<!--Fechamento de exemplos-->

        </section>



        <section id="produtores">

            <h2>Destaques Produtores</h2>
            <div class="producer-grid">

                <!--<div class="producer-circle"><span>Nome</span></div>
                <div class="producer-circle"><span>Nome</span></div>
                <div class="producer-circle"><span>Nome</span></div>
                <div class="producer-circle"><span>Nome</span></div>
                <div class="producer-circle"><span>Nome</span></div>
                <div class="producer-circle"><span>Nome</span></div>-->

                </div>
        </section>
        

        <section id="lojas">
            <h2>Sugestão de Lojas</h2>
            <div class="store-grid">
                <div class="store-card">Estabelecimento 1</div>
                <div class="store-card">Estabelecimento 2</div>
                <div class="store-card">Estabelecimento 3</div>
            </div>
        </section>



 <!--Mapa-->
        <section id="mapa">
    <h2>Mapa</h2>
    <div class="map-container">
        <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d12595.065278982896!2d-40.22530279939506!3d-20.19506470833454!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xb81de52407d8d7%3A0x289693da8d05dd09!2sIFES%20-%20Campus%20Serra!5e0!3m2!1spt-BR!2sbr!4v1778005795475!5m2!1spt-BR!2sbr" 
        width="600"
        height="450"
        style="border:0;"
        allowfullscreen=""
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade"></iframe>


    </div>
</section>
    </main>

    <footer>
        <div class="footer-top">
            <div class="footer-brand">aracê</div>
            <div class="newsletter">
                <p>Receba novidades</p>
                <input type="email" placeholder="seuemail@gmail.com">
                <button>Enviar</button>
            </div>
        </div>

        <div class="footer-links">
            <div class="col">
                <h4>Páginas Legais</h4>
                <ul>
                    <li>Termos e condições</li>
                    <li>Privacidade</li>
                </ul>
            </div>
            <div class="col">
                <h4>Links Importantes</h4>
                <ul>
                    <li>Comece Agora</li>
                    <li>Adicione seu restaurante</li>
                </ul>
            </div>
        </div>

        <div class="footer-bottom">
            <p>Aracê Copyright 2026. Todos os direitos reservados.</p>
        </div>
    </footer>

</body>
</html>