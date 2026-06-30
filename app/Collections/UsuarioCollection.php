<?php
// Define a "pasta virtual" (namespace) onde este arquivo está localizado no projeto.
// Isso ajuda a organizar o código e evitar conflitos de nomes.
namespace App\Collections;
// Importa arquivos necessários de outras partes do sistema
use App\Entities\User; // Classe que representa um único registro (Usuário)
use Tatter\Firebase\Firestore\Collection;// Classe base que fornece os métodos para lidar com o banco de dados Firestore

final class UsuarioCollection extends Collection
{
    public const NAME   = 'Usuarios';
    public const ENTITY = User::class;

    protected bool $skipValidation = true;

    // Mantendo a proteção dos campos necessária
    protected array $allowedFields = [
        'nome',
        'username',
        'email',
        'telefone',
        'cidade',
        'estado',
        'cpf',
        'fotoUrl',
        'bio',
        'nascimento',
        'sexo',
        'genero',
        'firebaseUid',
        'emailVerified',
        'disabled',
        'createdAt',
        'favoritos',
        'carrinho',
    ];

    protected array $validationRules = [
        'nome'          => 'required|min_length[2]|max_length[120]',
        'email'         => 'required|valid_email',
        'telefone'      => 'permit_empty|max_length[30]',
    ];
}
