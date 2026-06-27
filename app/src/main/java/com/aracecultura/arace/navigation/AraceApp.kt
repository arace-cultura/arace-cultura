package com.aracecultura.arace.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aracecultura.arace.AppViewModel
import com.aracecultura.arace.ui.auth.CadastroProdutorPasso1
import com.aracecultura.arace.ui.auth.CadastroProdutorPasso2
import com.aracecultura.arace.ui.auth.CadastroProdutorPasso3
import com.aracecultura.arace.ui.auth.CadastroProdutorViewModel
import com.aracecultura.arace.ui.auth.PassoCadastro
import com.aracecultura.arace.ui.auth.TelaCadastro
import com.aracecultura.arace.ui.auth.TelaEntrada
import com.aracecultura.arace.ui.auth.TelaEntrarLoja
import com.aracecultura.arace.ui.auth.TelaEscolhaLoja
import com.aracecultura.arace.ui.auth.TelaLogin
import com.aracecultura.arace.ui.auth.TelaRecuperarSenha
import com.aracecultura.arace.ui.main.jetpack.Modo
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AraceApp() {
    val nav = rememberNavController()
    val appVm: AppViewModel = viewModel()
    val logado = FirebaseAuth.getInstance().currentUser != null

    NavHost(nav, startDestination = if (logado) Rota.MainGraph else Rota.AuthGraph) {

        // ---- Autenticação ----
        navigation<Rota.AuthGraph>(startDestination = Rota.Entrada) {
            composable<Rota.Entrada> {
                TelaEntrada(
                    onCadastro = { nav.navigate(Rota.Cadastro) },
                    onLogin = { nav.navigate(Rota.Login) },
                )
            }
            composable<Rota.Login> {
                TelaLogin(
                    onSucesso = {
                        nav.navigate(Rota.MainGraph) {
                            popUpTo(Rota.AuthGraph) { inclusive = true }
                        }
                    },
                    onEsqueciSenha = { nav.navigate(Rota.RecuperarSenha) },
                    onVoltar = { nav.popBackStack() },
                )
            }
            composable<Rota.RecuperarSenha> {
                TelaRecuperarSenha(onVoltar = { nav.popBackStack() })
            }
            composable<Rota.Cadastro> {
                TelaCadastro(
                    onSucesso = {
                        nav.navigate(Rota.MainGraph) {
                            popUpTo(Rota.AuthGraph) { inclusive = true }
                        }
                    },
                    onVoltar = { nav.popBackStack() },
                )
            }
        }

        // ---- App principal: tem o seu próprio NavHost interno dentro do AppScaffold ----
        composable<Rota.MainGraph> { AppScaffold(nav, appVm) }

        // ---- Fluxo de virar produtor ----
        navigation<Rota.CadastroProdutorGraph>(startDestination = Rota.EscolhaLoja) {

            composable<Rota.EscolhaLoja> {
                TelaEscolhaLoja(
                    onCriarNova = { nav.navigate(Rota.CadastroProdutor) },
                    onEntrarExistente = { nav.navigate(Rota.EntrarLoja) },
                    onVoltar = { nav.popBackStack() },
                )
            }

            composable<Rota.EntrarLoja> {
                TelaEntrarLoja(
                    onSucesso = {
                        appVm.definirModo(Modo.PRODUTOR)
                        nav.navigate(Rota.MainGraph) {
                            popUpTo(Rota.CadastroProdutorGraph) { inclusive = true }
                        }
                    },
                    onVoltar = { nav.popBackStack() },
                )
            }

            composable<Rota.CadastroProdutor> {
                val vm: CadastroProdutorViewModel = viewModel()        // UMA instância pro fluxo inteiro
                val passo by vm.passo.collectAsStateWithLifecycle()

                // Voltar do sistema (gesto/botão): recua o passo; no primeiro, sai da rota
                BackHandler {
                    if (!vm.passoAnterior()) nav.popBackStack()
                }

                when (passo) {
                    PassoCadastro.DADOS_LOJA -> CadastroProdutorPasso1(
                        viewModel = vm,
                        onProximo = vm::proximoPasso,
                        onVoltar  = { nav.popBackStack() }              // sai do fluxo
                    )
                    PassoCadastro.DADOS_FISCAIS -> CadastroProdutorPasso2(
                        viewModel = vm,
                        onProximo = vm::proximoPasso,
                        onVoltar  = { vm.passoAnterior() }
                    )
                    PassoCadastro.DETALHES -> CadastroProdutorPasso3(
                        viewModel = vm,
                        onVoltar  = { vm.passoAnterior() },
                        onConcluir = {
                            appVm.definirModo(Modo.PRODUTOR)
                            nav.navigate(Rota.MainGraph) {
                                // limpa o fluxo de produtor inteiro da pilha
                                popUpTo(Rota.CadastroProdutorGraph) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
