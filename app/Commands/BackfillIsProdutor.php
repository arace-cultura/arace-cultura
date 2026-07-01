<?php

namespace App\Commands;

use App\Libraries\AraceFirestore;
use CodeIgniter\CLI\BaseCommand;
use CodeIgniter\CLI\CLI;

/**
 * Comando de linha de comando (Spark) que percorre todos os usuarios da coleção
 * "Usuarios" e garante que cada um tenha o campo booleano "isProdutor" preenchido.
 *
 * Uso:
 *   php spark arace:backfill-produtor
 *   php spark arace:backfill-produtor --force   (regrava o campo mesmo se ja existir)
 */
final class BackfillIsProdutor extends BaseCommand
{
    protected $group       = 'Arace';
    protected $name        = 'arace:backfill-produtor';
    protected $description = 'Adiciona/atualiza o campo booleano isProdutor em todos os usuarios do Firestore.';
    protected $usage       = 'arace:backfill-produtor [--force]';
    protected $options     = [
        '--force' => 'Regrava o campo isProdutor em todos os usuarios, mesmo os que ja possuem o valor correto.',
    ];

    public function run(array $params): void
    {
        $force = array_key_exists('force', $params) || in_array('--force', $params, true);

        CLI::write('Iniciando backfill do campo "isProdutor" em todos os usuarios...', 'yellow');

        try {
            $resumo = (new AraceFirestore())->backfillIsProdutor($force);
        } catch (\Throwable $exception) {
            CLI::error('Falha ao executar o backfill: ' . $exception->getMessage());

            return;
        }

        CLI::write('Concluido.', 'green');
        CLI::table(
            [
                ['Usuarios encontrados', (string) $resumo['total']],
                ['Marcados como produtor', (string) $resumo['produtores']],
                ['Marcados como cliente', (string) $resumo['clientes']],
                ['Documentos atualizados', (string) $resumo['atualizados']],
            ],
            ['Metrica', 'Valor']
        );
    }
}
