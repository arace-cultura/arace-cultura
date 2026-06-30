<?php

namespace Tests\Session;

use Config\Services;
use CodeIgniter\Test\CIUnitTestCase;
use CodeIgniter\Test\FeatureTestTrait;

final class AuthenticationPageTest extends CIUnitTestCase
{
    use FeatureTestTrait;

    public function testLoginButtonSubmitsTheCredentialForm(): void
    {
        $result = $this->get('/login');

        $result->assertStatus(200);
        $result->assertSeeElement('#formLogin');
        $result->assertSeeElement('#btnEntrar');
        $this->assertStringContainsString(
            'action="' . site_url('login') . '"',
            (string) $result->getBody(),
        );
        $this->assertStringNotContainsString(
            '<a href="/usuario/arace-perfil">Entrar</a>',
            (string) $result->getBody(),
        );
    }

    public function testProfileRedirectsGuestsToLogin(): void
    {
        $this->get('/usuario/arace-perfil')
            ->assertRedirectTo('/login');
    }

    public function testTraditionalLoginFormDoesNotTryToParseJson(): void
    {
        $result = $this->post('/login', [
            'email' => '',
            'senha' => '',
        ]);

        $result->assertRedirect();
        $result->assertSessionHas('erro');
    }

    public function testValidTraditionalLoginCreatesAuthenticatedSession(): void
    {
        Services::injectMock('araceFirestore', new class () {
            public function authenticateUser(string $email, string $password): ?array
            {
                if ($email !== 'usuario@teste.com' || $password !== 'senha-correta') {
                    return null;
                }

                return [
                    'id'    => 'usuario-teste',
                    'nome'  => 'Usuario Teste',
                    'email' => $email,
                ];
            }
        });

        $result = $this->post('/login', [
            'email' => 'usuario@teste.com',
            'senha' => 'senha-correta',
        ]);

        $result->assertRedirectTo('/usuario/arace-perfil');
        $result->assertSessionHas('arace_authenticated', true);
        $result->assertSessionHas('arace_user');
    }

    public function testAuthenticatedSessionCanOpenProfile(): void
    {
        $result = $this->withSession([
            'arace_authenticated' => true,
            'arace_user' => [
                'id'    => 'usuario-teste',
                'nome'  => 'Usuario Teste',
                'email' => 'usuario@teste.com',
            ],
        ])->get('/usuario/arace-perfil');

        $result->assertStatus(200);
        $result->assertSee('Usuario Teste');
        $result->assertSee('usuario@teste.com');
        $this->assertStringContainsString(
            '<form class="logout-form" action="' . site_url('sair') . '" method="post">',
            (string) $result->getBody(),
        );
    }

    public function testLogoutClearsAuthenticationAndReturnsToLogin(): void
    {
        $result = $this->withSession([
            'arace_authenticated' => true,
            'arace_user' => [
                'id'    => 'usuario-teste',
                'nome'  => 'Usuario Teste',
                'email' => 'usuario@teste.com',
            ],
        ])->post('/sair');

        $result->assertRedirectTo('/login');
        $result->assertSessionMissing('arace_authenticated');
        $result->assertSessionMissing('arace_user');
    }
}
