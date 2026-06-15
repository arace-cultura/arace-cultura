package com.aracecultura.arace.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.databinding.FragmentNavegacaoPrincipalBinding
import com.aracecultura.arace.ui.main.jetpack.Modo
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class NavegacaoPrincipal : Fragment() {

    private var _binding: FragmentNavegacaoPrincipalBinding? = null
    private val binding get() = this._binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentNavegacaoPrincipalBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.bnvMenuInferiorNavegacao.itemIconTintList = null
        super.onViewCreated(view, savedInstanceState)

        // getFragment é necessário pois é só uma view (um componente visual);
        // o comportamento de permanência e navegação é administrado pelo
        // NavHostFragment (fragment_navegacao_principal, embora apareça só na view

        val navController = this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController

        configurarNavegacaoInferior(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val esconderFooter = destination.id == R.id.produto ||
                destination.id == R.id.finalizarCompraFragment

            this.binding.bnvMenuInferiorNavegacao.visibility =
                if (esconderFooter) View.GONE else View.VISIBLE
        }

        // ── Barramento único de sinais entre telas ──────────────────────
        // Todos os fragment results do app trafegam pelo FragmentManager da
        // ACTIVITY. Motivo: fragments em NavHosts aninhados não compartilham
        // parentFragmentManager (cada NavHost tem seu próprio childFM), então
        // o FM da activity é o único alcançável por todas as telas via
        // requireActivity(). Quem emite deve usar
        // requireActivity().supportFragmentManager.setFragmentResult(...).
        //
        // Regras do mecanismo (FragmentResult API):
        //  - 1 listener por chave por FM; registrar de novo SUBSTITUI o anterior
        //  - o resultado fica guardado no FM até um listener STARTED consumi-lo
        //  - com viewLifecycleOwner, o listener morre junto com a view (sem leak)
        val barramento = requireActivity().supportFragmentManager

        // Cadastro de loja concluído ou entrada em loja existente:
        // troca o footer para o modo produtor
        barramento.setFragmentResultListener(
            "cadastro_produtor_request",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean("sucesso", false)) {
                Log.d("ModoArace", "Cadastro/entrada de loja concluído. Trocando footer.")
                configurarMenuProdutor()
            }
        }

        // Pedido de troca de modo vindo dos botões "Visualização" dos perfis
        barramento.setFragmentResultListener(
            "mudanca_modo_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val modo = if (bundle.getBoolean("isProdutor", false)) Modo.PRODUTOR else Modo.CLIENTE
            quandoModoMudar(modo)
        }

        // Logout disparado pelas telas de perfil/configurações
        barramento.setFragmentResultListener(
            "logout_request",
            viewLifecycleOwner
        ) { _, _ ->
            // A NavegacaoPrincipal está no grafo raiz, então encontra a action
            findNavController().navigate(R.id.action_main_to_auth)
        }
    }

    // Função de verificar a mudança no Logcat
    private fun quandoModoMudar(modo: Modo) {
        // O "d" significa Debug. É o nível de prioridade da mensagem
        Log.d("ModoArace", "O usuário trocou para o modo: ${modo.name}")

        when (modo) {
            Modo.CLIENTE -> configurarMenuCliente()
            Modo.PRODUTOR -> verificarEEntrarModoProdutor()
        }
    }

    private fun iniciarFluxoCadastroProdutor(){

        findNavController().navigate(R.id.action_global_cadastroProdutorFragment)
    }

    private fun verificarEEntrarModoProdutor() {
        val sharedPref = requireActivity().getSharedPreferences("AracePrefs", android.content.Context.MODE_PRIVATE)

        // Pegamos o ID do usuário atual para verificar a chave correta
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            // Conta é produtora se está vinculada a uma loja (Usuarios.lojaId);
            // o repositório também migra cadastros legados (Produtores/{uid})
            val temLoja = try {
                LojaRepository.resolverLojaId(userId) != null
            } catch (e: Exception) {
                Log.e("ModoArace", "Falha ao buscar vínculo de loja no banco.", e)
                sharedPref.getBoolean("STATUS_PRODUTOR_$userId", false)
            }

            sharedPref.edit()
                .putBoolean("STATUS_PRODUTOR_$userId", temLoja)
                .apply()

            if (temLoja) {
                Log.d("ModoArace", "Trocando footer para Produtor.")
                configurarMenuProdutor()
            } else {
                Log.d("ModoArace", "Redirecionando para escolha de loja.")
                iniciarFluxoCadastroProdutor()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

    private fun configurarNavegacaoInferior(navController: NavController) {
        val bottomNav = binding.bnvMenuInferiorNavegacao

        bottomNav.setupWithNavController(navController)
        bottomNav.setOnItemSelectedListener { item ->
            val estavaNaCategoria = navController.currentDestination?.id == R.id.categoria

            if (estavaNaCategoria) {
                navController.popBackStack(R.id.homePage, false)
            }

            if (estavaNaCategoria && item.itemId == R.id.homePage) {
                true
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
        bottomNav.setOnItemReselectedListener { item ->
            if (navController.currentDestination?.id == R.id.categoria) {
                navController.popBackStack(R.id.homePage, false)

                if (item.itemId != R.id.homePage) {
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            } else {
                navController.popBackStack(item.itemId, false)
            }
        }
    }

    private fun configurarMenuCliente() {
        val bottomNav = this.binding.bnvMenuInferiorNavegacao
        val navController = this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        val destinoAtual = navController.currentDestination?.id

        // 1. Apaga os ícones atuais
        bottomNav.menu.clear()

        // 2. Infla (injeta) o XML do Cliente
        bottomNav.inflateMenu(R.menu.bottom_nav)

        // 3. Reconecta o controller para ele reconhecer os "novos" botões
        configurarNavegacaoInferior(navController)
        if (destinoAtual == R.id.perfilprodutor) {
            navController.navigate(R.id.perfilcliente)
        } else {
            destinoAtual?.let { bottomNav.menu.findItem(it)?.isChecked = true }
        }
    }

    private fun configurarMenuProdutor() {
        val bottomNav = this.binding.bnvMenuInferiorNavegacao
        val navController = this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        val destinoAtual = navController.currentDestination?.id

        // 1. Apaga os ícones atuais (do cliente)
        bottomNav.menu.clear()

        // 2. Infla (injeta) o XML do Produtor que possui 4 ícones
        bottomNav.inflateMenu(R.menu.bottom_nav_produtor)

        // 3. Reconecta o controller para ele reconhecer os "novos" botões
        configurarNavegacaoInferior(navController)
        if (destinoAtual == R.id.perfilcliente) {
            navController.navigate(R.id.perfilprodutor)
        } else {
            destinoAtual?.let { bottomNav.menu.findItem(it)?.isChecked = true }
        }
    }




}
