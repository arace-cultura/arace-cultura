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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


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
        this.binding.bnvMenuInferiorNavegacao.setupWithNavController(
            this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        )

        this.binding.btnMenuModo.setOnClickListener {
            val bottomSheet = SeletorModoBottomSheet()


            bottomSheet.onModoSelecionado = { modoSelecionado ->
                quandoModoMudar(modoSelecionado)
            }
            bottomSheet.show(childFragmentManager, "SeletorModo")
        }

        // Verificação do cadastro de produtor bem sucedido!

        setFragmentResultListener("cadastro_produtor_request") { _, bundle ->
            val cadastroConcluido = bundle.getBoolean("sucesso", false)

            if (cadastroConcluido) {
                // Se o cadastro deu certo, troca o footer para o modo produtor automaticamente
                Log.d("ModoArace", "Ouvinte disparado: Cadastro concluído. Trocando footer.")
                configurarMenuProdutor()
            }
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "desconhecido"

        // Lemos a chave com o ID atrelado
        val isProdutorCadastrado = sharedPref.getBoolean("STATUS_PRODUTOR_$userId", false)

        if (isProdutorCadastrado) {
            Log.d("ModoArace", "Trocando footer para Produtor.")
            configurarMenuProdutor()
        } else {
            Log.d("ModoArace", "Redirecionando para Cadastro.")
            iniciarFluxoCadastroProdutor()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

    private fun configurarMenuCliente() {
        val bottomNav = this.binding.bnvMenuInferiorNavegacao

        // 1. Apaga os ícones atuais
        bottomNav.menu.clear()

        // 2. Infla (injeta) o XML do Cliente
        bottomNav.inflateMenu(R.menu.bottom_nav)

        // 3. Reconecta o controller para ele reconhecer os "novos" botões
        bottomNav.setupWithNavController(
            this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        )
    }

    private fun configurarMenuProdutor() {
        val bottomNav = this.binding.bnvMenuInferiorNavegacao

        // 1. Apaga os ícones atuais (do cliente)
        bottomNav.menu.clear()

        // 2. Infla (injeta) o XML do Produtor que possui 4 ícones
        bottomNav.inflateMenu(R.menu.bottom_nav_produtor)

        // 3. Reconecta o controller para ele reconhecer os "novos" botões
        bottomNav.setupWithNavController(
            this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        )
    }




}