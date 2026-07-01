<?php

namespace App\Libraries;

use CodeIgniter\HTTP\Files\UploadedFile;
use RuntimeException;

final class SupabaseStorage
{
    /** Tamanho maximo aceito por imagem (bytes). Evita estourar limites do PHP/Supabase. */
    private const MAX_FILE_SIZE = 8 * 1024 * 1024; // 8 MB

    /** Extensoes de imagem aceitas no bucket. */
    private const ALLOWED_EXTENSIONS = ['jpg', 'jpeg', 'png', 'webp', 'gif'];

    private string $url;
    private string $key;
    private string $bucket;

    public function __construct()
    {
        $this->url    = rtrim((string) env('SUPABASE_URL', ''), '/');
        $this->key    = (string) (env('SUPABASE_SERVICE_ROLE_KEY') ?: env('SUPABASE_ANON_KEY', ''));
        $this->bucket = (string) env('SUPABASE_AVATAR_BUCKET', 'avatars');
    }

    /**
     * Confere se as credenciais do Supabase estao configuradas no .env.
     */
    public function isConfigured(): bool
    {
        return $this->url !== '' && $this->key !== '';
    }

    public function uploadAvatar(UploadedFile $file, string $userId): string
    {
        return $this->uploadImage($file, $userId);
    }

    public function uploadProductImage(UploadedFile $file, string $producerId): string
    {
        return $this->uploadImage($file, 'produtos/' . $producerId);
    }

    public function uploadImage(UploadedFile $file, string $folder): string
    {
        if (! $this->isConfigured()) {
            throw new RuntimeException('Configure SUPABASE_URL e SUPABASE_SERVICE_ROLE_KEY no .env para enviar imagens.');
        }

        if (! $file->isValid() || $file->hasMoved()) {
            throw new RuntimeException('Arquivo de imagem invalido.');
        }

        if ($file->getSize() > self::MAX_FILE_SIZE) {
            throw new RuntimeException('A imagem excede o tamanho maximo de 8 MB.');
        }

        $mimeType = (string) ($file->getMimeType() ?: '');
        if ($mimeType !== '' && ! str_starts_with($mimeType, 'image/')) {
            throw new RuntimeException('Envie apenas arquivos de imagem (JPG, PNG, WEBP ou GIF).');
        }

        $extension = strtolower($file->getClientExtension() ?: $file->guessExtension() ?: 'jpg');
        $extension = in_array($extension, self::ALLOWED_EXTENSIONS, true) ? $extension : 'jpg';
        $safeFolder = preg_replace('/[^a-zA-Z0-9_\/-]/', '-', trim($folder, '/')) ?: 'imagens';
        $path      = $safeFolder . '/' . date('YmdHis') . '-' . bin2hex(random_bytes(6)) . '.' . $extension;
        $endpoint  = $this->url . '/storage/v1/object/' . rawurlencode($this->bucket) . '/' . str_replace('%2F', '/', rawurlencode($path));
        $contents  = file_get_contents($file->getTempName());

        if ($contents === false) {
            throw new RuntimeException('Nao foi possivel ler o arquivo de imagem.');
        }

        // Garante um Content-Type de imagem: usa o MIME real ou deriva da extensao,
        // evitando 'application/octet-stream' que impede a exibicao inline no navegador.
        $contentType = $mimeType !== '' ? $mimeType : ('image/' . ($extension === 'jpg' ? 'jpeg' : $extension));

        $headers = [
            'Authorization: Bearer ' . $this->key,
            'apikey: ' . $this->key,
            'Content-Type: ' . $contentType,
            'x-upsert: true',
        ];

        [$statusCode, $response] = $this->postObject($endpoint, $headers, $contents);

        if ($statusCode < 200 || $statusCode >= 300) {
            $detail = is_string($response) && $response !== '' ? ' Detalhe: ' . mb_substr($response, 0, 200) : '';
            throw new RuntimeException('Nao foi possivel enviar a imagem para o Supabase.' . $detail);
        }

        return $this->url . '/storage/v1/object/public/' . rawurlencode($this->bucket) . '/' . str_replace('%2F', '/', rawurlencode($path));
    }

    /**
     * @return array{0:int,1:string}
     */
    private function postObject(string $endpoint, array $headers, string $contents): array
    {
        if (function_exists('curl_init')) {
            $curl = curl_init($endpoint);
            curl_setopt_array($curl, [
                CURLOPT_POST           => true,
                CURLOPT_HTTPHEADER     => $headers,
                CURLOPT_POSTFIELDS     => $contents,
                CURLOPT_RETURNTRANSFER => true,
                CURLOPT_HEADER         => false,
            ]);

            $response = curl_exec($curl);
            $error    = curl_error($curl);
            $status   = (int) curl_getinfo($curl, CURLINFO_RESPONSE_CODE);
            curl_close($curl);

            if ($response === false) {
                throw new RuntimeException('Falha de conexao com o Supabase: ' . $error);
            }

            return [$status, (string) $response];
        }

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

        preg_match('/\s(\d{3})\s/', $status, $matches);

        return [(int) ($matches[1] ?? 0), (string) $response];
    }
}
