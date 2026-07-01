# Aracê

Plataforma Web e aplicativo Android para divulgação, venda e gestão de produtos ligados à cultura e ao artesanato capixaba.

O projeto foi desenvolvido como Projeto Integrador do 6º período do Curso Técnico em Informática para Internet do IFES Campus Serra. A proposta é aproximar produtores locais, artesãos e consumidores em um ambiente digital com foco em identidade regional, economia criativa e visibilidade para a produção cultural do Espírito Santo.

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Objetivos](#objetivos)
- [Funcionalidades](#funcionalidades)
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura e organização](#arquitetura-e-organização)
- [Fluxos principais](#fluxos-principais)
- [Branches](#branches)
- [Como executar](#como-executar)
- [Equipe](#equipe)
- [Status](#status)

## Sobre o projeto

O Aracê é um sistema dividido em duas frentes:

1. **Aplicativo Android**, voltado à experiência mobile do usuário e produtor.
2. **Plataforma Web**, voltada à apresentação institucional, navegação, perfis e operações administrativas.

A aplicação busca resolver um problema recorrente para pequenos produtores e artesãos: a baixa presença digital e a dificuldade de apresentar seus produtos com contexto, origem e identidade. Em vez de funcionar apenas como uma vitrine genérica, o Aracê organiza produtos, lojas e perfis em torno da cultura material capixaba.

## Objetivos

- Valorizar produtores, artesãos e expressões culturais do Espírito Santo.
- Facilitar o acesso do público a produtos locais.
- Permitir que produtores cadastrem, editem e gerenciem suas lojas.
- Unificar experiência de cliente e produtor em uma mesma conta.
- Apoiar a economia criativa regional por meio de uma solução digital acessível.

## Funcionalidades

### Cliente

- Cadastro e login de usuários.
- Autenticação por e-mail e senha.
- Persistência de sessão.
- Edição de dados do perfil.
- Visualização de produtos por categorias.
- Adição, remoção e atualização de itens no carrinho.
- Acompanhamento de pedidos e histórico de compras.

### Produtor

- Cadastro de loja/produtor.
- Alternância entre modo cliente e modo produtor.
- Gerenciamento de produtos da loja.
- Controle de estoque.
- Visualização de pedidos recebidos.
- Acesso a métricas básicas de vendas.
- Visualização de feedbacks e avaliações.

### Plataforma Web

- Landing page responsiva.
- Exibição institucional do projeto.
- Controle e customização de perfil.
- Operações de CRUD integradas ao banco de dados.

## Stack tecnológica

### Mobile Android

- **Linguagem:** Kotlin
- **IDE:** Android Studio
- **Interface:** XML e Jetpack Compose
- **Navegação:** Single Activity Architecture com fragments e Navigation Graph
- **Arquitetura:** separação de responsabilidades com base em MVVM
- **Integrações:** Firebase Authentication e Cloud Firestore

### Web

- **Linguagem:** PHP
- **Framework:** CodeIgniter 4
- **Interface:** HTML, CSS e JavaScript
- **Banco de dados:** Cloud Firestore

### Backend e persistência

- **Firebase Authentication:** autenticação, sessão e controle de credenciais.
- **Cloud Firestore:** persistência em banco NoSQL orientado a documentos.

## Arquitetura e organização

O repositório concentra as frentes Mobile e Web do projeto. A organização do desenvolvimento foi feita por branches, permitindo que cada equipe trabalhasse em sua área sem bloquear a outra. O fluxo da equipe Mobile está centralizado na branch `app-dev`, enquanto a equipe Web atua na branch `web-dev`.

### Mobile

A frente Android utiliza uma arquitetura baseada em uma única Activity, com navegação entre fragments por meio do Navigation Graph. A interface é feita em Jetpack Compose devido ao seu maior desempenho em telas que exigem maior flexibilidade visual e dinamismo. O gerenciamento de credenciais e a persistência de dados é realizado de forma integrada via Firebase.

### Web

A frente Web utiliza CodeIgniter 4 para estruturar rotas, views e operações de CRUD sob o padrão MVC em PHP. A aplicação contempla páginas com design responsivo, controle e customização de perfis e recursos  conectados ao banco de dados Firestore, garantindo o gerenciamento unificado do ecossistema.

## Fluxos principais

### 1. Gestão do cliente

Cadastro, login, recuperação de credenciais e atualização de informações do perfil.

### 2. Transição para produtor

Cadastro de loja e alternância entre os modos de navegação:

- `Modo.CLIENTE`
- `Modo.PRODUTOR`

A alternância permite que um usuário com loja cadastrada atue como comprador ou como gestor sem trocar de conta.

### 3. Comercialização e carrinho

Visualização de produtos, organização por categorias, manipulação do carrinho e preparação do processo de compra.

### 4. Histórico de compras

Consulta de pedidos realizados, compras concluídas e acompanhamento do status das solicitações.

### 5. Gestão da loja

Painel do produtor para cadastro, edição, listagem e remoção de produtos, além do acompanhamento de pedidos recebidos.

### 6. Inteligência do negócio

Dashboard simplificado com métricas básicas de vendas, avaliações e feedbacks de consumidores.

## Branches

O desenvolvimento foi organizado em um repositório unificado:

```text
arace-cultura/arace-cultura
```

### Mobile

Branch principal de desenvolvimento:

```text
app-dev
```

### Web

Branch principal de desenvolvimento:

```text
web-dev
```

## Como executar

### Mobile Android

1. Clone o repositório:

```bash
git clone https://github.com/arace-cultura/arace-cultura.git
cd arace-cultura
```

2. Acesse a branch da aplicação Android:

```bash
git checkout app-dev
```

3. Abra o projeto no Android Studio.

4. Configure o Firebase no projeto Android, incluindo o arquivo `google-services.json` no módulo correto.

5. Execute o aplicativo em um emulador ou dispositivo físico.

### Web

1. Clone o repositório:

```bash
git clone https://github.com/arace-cultura/arace-cultura.git
cd arace-cultura
```

2. Acesse a branch Web:

```bash
git checkout web-dev
```

3. Instale as dependências do projeto, caso aplicável:

```bash
composer install
```

4. Configure as variáveis de ambiente e credenciais necessárias para integração com Firebase/Firestore.

5. Inicie o servidor de desenvolvimento do CodeIgniter:

```bash
php spark serve
```

6. Acesse a aplicação no navegador:

```text
http://localhost:8080
```

## Requisitos

### Mobile

- Android Studio instalado.
- JDK compatível com a versão do Gradle usada pelo projeto.
- Projeto Firebase configurado.
- Firebase Authentication habilitado.
- Cloud Firestore habilitado.

### Web

- PHP compatível com CodeIgniter 4.
- Composer instalado.
- Extensões PHP exigidas pelo CodeIgniter.
- Credenciais de acesso ao Firebase/Firestore configuradas.

## Equipe

O projeto foi desenvolvido com organização baseada em Scrum e acompanhamento de tarefas por Kanban no GitHub.

| Função | Integrantes |
|---|---|
| Scrum Master | Akilanny Silva Cruz |
| Product Owner | Angelo dos Santos Teixeira |
| Time de Desenvolvimento | Akilanny Cruz, Angelo Teixeira, Arthur Loureiro Lima, Cecilia Tavares Barreto, Heitor Moura Rosi, Pedro Henrique Tavares Moraes |

## Status

Projeto acadêmico em desenvolvimento.

Funcionalidades, rotas, estrutura de dados e integrações podem mudar conforme a evolução do backlog e das entregas do Projeto Integrador.

Apresentação final: 01/07/2026
