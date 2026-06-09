package com.aracecultura.arace.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentNavegacaoPrincipalBinding
import com.aracecultura.arace.ui.main.jetpack.Modo
import com.aracecultura.arace.ui.main.jetpack.SeletorModoBottomSheet
import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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

        // getFragment é necessário pois o acesso ao navcontroller é da
        // fragment dentro do fcvNavegacaoPrincipal, que é, na verdade,
        // uma view que pertence à main activity.
        val navController = this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController

        this.binding.bnvMenuInferiorNavegacao.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val esconderFooter = destination.id == R.id.produto ||
                destination.id == R.id.finalizarCompraFragment

            this.binding.bnvMenuInferiorNavegacao.visibility =
                if (esconderFooter) View.GONE else View.VISIBLE
        }

        // Backdoor desativada — troca de modo agora é feita pelo BotaoVisualizacao nos perfis
        // this.binding.btnMenuModo.setOnClickListener {
        //     val bottomSheet = SeletorModoBottomSheet()
        //     bottomSheet.onModoSelecionado = { modoSelecionado ->
        //         quandoModoMudar(modoSelecionado)
        //     }
        //     bottomSheet.show(childFragmentManager, "SeletorModo")
        // }

        // Verificação do cadastro de produtor bem sucedido!

        setFragmentResultListener("cadastro_produtor_request") { _, bundle ->
            val cadastroConcluido = bundle.getBoolean("sucesso", false)

            if (cadastroConcluido) {
                // Se o cadastro deu certo, troca o footer para o modo produtor automaticamente
                Log.d("ModoArace", "Ouvinte disparado: Cadastro concluído. Trocando footer.")
                configurarMenuProdutor()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "mudanca_modo_request",
            viewLifecycleOwner
        ) { _, bundle ->

            val isProdutor = bundle.getBoolean("isProdutor", false)

            // Converte o booleano do Firebase/Compose para o seu Enum (Modo)
            val modoSelecionado = if (isProdutor) Modo.PRODUTOR else Modo.CLIENTE

            quandoModoMudar(modoSelecionado)
        }

        setFragmentResultListener("mudanca_modo_request") { _, bundle ->
            val querSerProdutor = bundle.getBoolean("isProdutor", false)

            val modoSelecionado = if (querSerProdutor) Modo.PRODUTOR else Modo.CLIENTE
            quandoModoMudar(modoSelecionado)
        }

        // Dentro do onViewCreated de NavegacaoPrincipal.kt

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "logout_request",
            viewLifecycleOwner
        ) { _, _ ->
            // A NavegacaoPrincipal está no root, então ela ENCONTRA a action sem dar crash!
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
            val isProdutorCadastrado = try {
                FirebaseFirestore.getInstance()
                    .collection("Produtores")
                    .document(userId)
                    .get()
                    .await()
                    .exists()
            } catch (e: Exception) {
                Log.e("ModoArace", "Falha ao buscar status de produtor no banco.", e)
                sharedPref.getBoolean("STATUS_PRODUTOR_$userId", false)
            }

            sharedPref.edit()
                .putBoolean("STATUS_PRODUTOR_$userId", isProdutorCadastrado)
                .apply()

            if (isProdutorCadastrado) {
                Log.d("ModoArace", "Trocando footer para Produtor.")
                configurarMenuProdutor()
            } else {
                Log.d("ModoArace", "Redirecionando para Cadastro.")
                iniciarFluxoCadastroProdutor()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
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
        bottomNav.setupWithNavController(navController)
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
        bottomNav.setupWithNavController(navController)
        if (destinoAtual == R.id.perfilcliente) {
            navController.navigate(R.id.perfilprodutor)
        } else {
            destinoAtual?.let { bottomNav.menu.findItem(it)?.isChecked = true }
        }
    }




}
