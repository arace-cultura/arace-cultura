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
            return $this->uploadAvatarLocally($file, $userId);
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
            return $this->uploadAvatarLocally($file, $userId);
        }

        return $this->url . '/storage/v1/object/public/' . rawurlencode($this->bucket) . '/' . str_replace('%2F', '/', rawurlencode($path));
    }

    private function uploadAvatarLocally(UploadedFile $file, string $userId): string
    {
        if (! $file->isValid() || $file->hasMoved()) {
            throw new RuntimeException('Arquivo de avatar invalido.');
        }

        $extension = strtolower($file->getClientExtension() ?: $file->guessExtension() ?: 'jpg');
        $extension = in_array($extension, ['jpg', 'jpeg', 'png', 'webp', 'gif'], true) ? $extension : 'jpg';
        $safeUser  = preg_replace('/[^a-zA-Z0-9_-]/', '-', $userId) ?: 'usuario';
        $directory = FCPATH . 'uploads' . DIRECTORY_SEPARATOR . 'avatars' . DIRECTORY_SEPARATOR . $safeUser;

        if (! is_dir($directory) && ! mkdir($directory, 0775, true) && ! is_dir($directory)) {
            throw new RuntimeException('Nao foi possivel criar a pasta local de avatares.');
        }

        $filename = date('YmdHis') . '-' . bin2hex(random_bytes(6)) . '.' . $extension;
        $file->move($directory, $filename, true);

        return rtrim((string) config('App')->baseURL, '/') . '/uploads/avatars/' . rawurlencode($safeUser) . '/' . rawurlencode($filename);
    }
}
