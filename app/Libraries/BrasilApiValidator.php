<?php

namespace App\Libraries;

use Config\Services;
use CodeIgniter\HTTP\CURLRequest;

final class BrasilApiValidator
{
    private const BASE_URL = 'https://brasilapi.com.br/api';
    private ?CURLRequest $httpClient = null;

    /**
     * Inicializa o cliente HTTP apenas quando necessário (Lazy Loading)
     */
    private function getHttpClient(): CURLRequest
    {
        if ($this->httpClient === null) {
            $this->httpClient = Services::curlrequest([
                'baseURI'     => self::BASE_URL,
                'timeout'     => 4,
                'http_errors' => false,
                'headers'     => ['Accept' => 'application/json'],
            ]);
        }
        return $this->httpClient;
    }

    public function validCpf(string $cpf): bool
    {
        $numbers = preg_replace('/\D/', '', $cpf) ?? '';

        if (strlen($numbers) !== 11 || preg_match('/^(\d)\1+$/', $numbers)) {
            return false;
        }

        for ($t = 9; $t < 11; $t++) {
            $sum = 0;
            for ($c = 0; $c < $t; $c++) {
                $sum += (int) $numbers[$c] * (($t + 1) - $c);
            }

            $digit = ((10 * $sum) % 11) % 10;
            if ((int) $numbers[$t] !== $digit) {
                return false;
            }
        }

        return true;
    }

    public function validPhone(string $phone, bool $required = false): bool
    {
        $digits = preg_replace('/\D/', '', $phone) ?? '';

        if ($digits === '') {
            return ! $required;
        }

        if (! in_array(strlen($digits), [10, 11], true)) {
            return false;
        }

        // Se tem 11 dígitos (celular), o primeiro dígito após o DDD obrigatoriamente deve ser 9
        if (strlen($digits) === 11 && $digits[2] !== '9') {
            return false;
        }

        return $this->exists('/ddd/v1/' . substr($digits, 0, 2));
    }

    public function validCnpj(string $cnpj, bool $required = false): bool
    {
        $digits = preg_replace('/\D/', '', $cnpj) ?? '';

        if ($digits === '') {
            return ! $required;
        }

        if (strlen($digits) !== 14 || preg_match('/^(\d)\1+$/', $digits)) {
            return false;
        }

        // Validação matemática local antes de gastar requisição na API
        if (! $this->validateCnpjDigits($digits)) {
            return false;
        }

        return $this->exists('/cnpj/v1/' . $digits);
    }

    private function validateCnpjDigits(string $cnpj): bool
    {
        // Validação do primeiro dígito verificador
        for ($i = 0, $j = 5, $sum = 0; $i < 12; $i++) {
            $sum += (int) $cnpj[$i] * $j;
            $j = ($j === 2) ? 9 : $j - 1;
        }
        $rest = $sum % 11;
        if ((int) $cnpj[12] !== ($rest < 2 ? 0 : 11 - $rest)) {
            return false;
        }

        // Validação do segundo dígito verificador
        for ($i = 0, $j = 6, $sum = 0; $i < 13; $i++) {
            $sum += (int) $cnpj[$i] * $j;
            $j = ($j === 2) ? 9 : $j - 1;
        }
        $rest = $sum % 11;
        return (int) $cnpj[13] === ($rest < 2 ? 0 : 11 - $rest);
    }

    private function exists(string $path): bool
    {
        try {
            // Reutiliza o cliente configurado
            $response = $this->getHttpClient()->get($path);
            $status   = $response->getStatusCode();

            if ($status === 404 || $status === 400) {
                return false;
            }

            return $status >= 200 && $status < 300;
        } catch (\Throwable $exception) {
            log_message('warning', 'Brasil API indisponível durante validação: {message}', [
                'message' => $exception->getMessage(),
            ]);

            // Se a API cair, assume como válido (mecanismo de fallback seguro que você já usava)
            return true;
        }
    }
}