<?php

namespace App\Libraries;

use CodeIgniter\HTTP\Files\UploadedFile;
use RuntimeException;

final class SupabaseStorage
{
    private string $url;
    private string $key;
    private string $bucket;

    public function __construct()
    {
        $this->url    = rtrim((string) env('SUPABASE_URL', ''), '/');
        $this->key    = (string) (env('SUPABASE_SERVICE_ROLE_KEY') ?: env('SUPABASE_ANON_KEY', ''));
        $this->bucket = (string) env('SUPABASE_AVATAR_BUCKET', 'avatars');
    }

    public function uploadAvatar(UploadedFile $file, string $userId): string
    {
        if ($this->url === '' || $this->key === '') {
            throw new RuntimeException('Configure SUPABASE_URL e SUPABASE_SERVICE_ROLE_KEY no .env.');
        }

        if (! $file->isValid() || $file->hasMoved()) {
            throw new RuntimeException('Arquivo de avatar invalido.');
        }

        $extension = strtolower($file->getClientExtension() ?: $file->guessExtension() ?: 'jpg');
        $safeUser  = preg_replace('/[^a-zA-Z0-9_-]/', '-', $userId) ?: 'usuario';
        $path      = $safeUser . '/' . date('YmdHis') . '-' . bin2hex(random_bytes(6)) . '.' . $extension;
        $endpoint  = $this->url . '/storage/v1/object/' . rawurlencode($this->bucket) . '/' . str_replace('%2F', '/', rawurlencode($path));
        $contents  = file_get_contents($file->getTempName());

        if ($contents === false) {
            throw new RuntimeException('Nao foi possivel ler o arquivo de avatar.');
        }

        $headers = [
            'Authorization: Bearer ' . $this->key,
            'apikey: ' . $this->key,
            'Content-Type: ' . ($file->getMimeType() ?: 'application/octet-stream'),
            'x-upsert: true',
        ];

        $context = stream_context_create([
            'http' => [
                'method'        => 'POST',
                'header'        => implode("\r\n", $headers),
                'content'       => $contents,
                'ignore_errors' => true,
            ],
        ]);

        $response = file_get_contents($endpoint, false, $context);
        $status   = $http_response_header[0] ?? '';

        if (! str_contains($status, '200') && ! str_contains($status, '201')) {
            throw new RuntimeException('Supabase nao aceitou o upload do avatar: ' . (string) $response);
        }

        return $this->url . '/storage/v1/object/public/' . rawurlencode($this->bucket) . '/' . str_replace('%2F', '/', rawurlencode($path));
    }
}
