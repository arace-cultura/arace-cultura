<?php

// Define a "pasta virtual" (namespace) onde este arquivo está localizado no projeto.
// Isso ajuda a organizar o código e evitar conflitos de nomes.
namespace App\Collections;

// Importa arquivos necessários de outras partes do sistema
use App\Entities\Producer; // Classe que representa um único registro (um produtor individual)
use Tatter\Firebase\Firestore\Collection; // Classe base que fornece os métodos para lidar com o banco de dados Firestore

/**
 * A classe ProducerCollection gerencia a coleção (o equivalente a uma "tabela") de produtores.
 * O termo "final" significa que esta classe não pode ser estendida por outras classes.
 */
final class ProducerCollection extends Collection
{
    // Define o nome exato da coleção lá no banco de dados Firestore.
    public const NAME   = 'produtores';
    
    // Define qual classe será usada para transformar os dados do banco em objetos no código.
    // Cada documento retornado do banco será transformado em um objeto "Producer".
    public const ENTITY = Producer::class;

    // Define se a validação automática de dados deve ser ignorada ao salvar no banco.
    // Como está 'true', o sistema salvará os dados diretamente sem checar as regras abaixo automaticamente.
    protected bool $skipValidation = true;

    // Lista de campos (colunas) que têm permissão para serem salvos ou atualizados no banco.
    // É uma medida de segurança para evitar que usuários mal-intencionados injetem dados indesejados no banco (Mass Assignment).
    // Nota: Há algumas duplicações (ex: 'nomeLoja' e 'nome_loja'), o que indica suporte a diferentes padrões de nomenclatura antigos/novos.
    protected array $allowedFields = [
        'nome',
        'nomeLoja',
        'nome_loja',
        'lojaBio',
        'bio',
        'email',
        'email_comercial',
        'telefone',
        'telefone_comercial',
        'cpf',
        'cnpj',
        'categoria',
        'categoria_principal',
        'distritoId',
        'distrito_id',
        'fotoUrl',
        'bannerUrl',
        'cepOrigem',
        'cidade',
        'estado',
        'endereco',
        'retiradaLocal',
        'envioCorreios',
        'entregaLocal',
        'pix',
        'horarioSemanaInicio',
        'horarioSemanaFim',
        'horarioSabadoInicio',
        'horarioSabadoFim',
        'produtos',
        'iniciais',
        'tipo',
        'cadastrado',
        'createdAt',
        'pedidos',
    ];

    // Regras de validação para garantir que os dados estejam corretos antes de irem para o banco.
    // Como $skipValidation está 'true' lá em cima, essas regras só serão aplicadas se a validação for chamada manualmente pelo desenvolvedor.
    protected array $validationRules = [
        // O nome é obrigatório, deve ter no mínimo 2 caracteres e no máximo 120.
        'nome'     => 'required|min_length[2]|max_length[120]',
        
        // O email é obrigatório e precisa ser um formato de email válido (ex: ter '@' e '.com').
        'email'    => 'required|valid_email',
        
        // O telefone não é obrigatório (pode ficar vazio), mas se for preenchido não pode passar de 30 caracteres.
        'telefone' => 'permit_empty|max_length[30]',
    ];
}