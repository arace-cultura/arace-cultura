<?php

namespace App\Libraries;

use Config\Services;

final class BrasilApiValidator
{
    private const BASE_URL = 'https://brasilapi.com.br/api';

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

        return $this->exists('/cnpj/v1/' . $digits);
    }

    private function exists(string $path): bool
    {
        try {
            $response = Services::curlrequest([
                'baseURI'     => self::BASE_URL,
                'timeout'     => 4,
                'http_errors' => false,
                'headers'     => ['Accept' => 'application/json'],
            ])->get($path);

            $status = $response->getStatusCode();

            if ($status === 404 || $status === 400) {
                return false;
            }

            return $status >= 200 && $status < 300;
        } catch (\Throwable $exception) {
            log_message('warning', 'Brasil API indisponivel durante validacao: {message}', [
                'message' => $exception->getMessage(),
            ]);

            return true;
        }
    }
}
