package com.aracecultura.arace.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.databinding.FragmentCadastroProdutorTela1Binding

class CadastroProdutorTela1 : Fragment() {

    private var _binding: FragmentCadastroProdutorTela1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCadastroProdutorTela1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lógica de "Criar Conta" (Continuar)
        binding.cadastroBtn.setOnClickListener {
            // 1. Salva o status de produtor localmente (BD local)
            val sharedPref = requireActivity().getSharedPreferences("AracePrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("STATUS_PRODUTOR", true)
                apply() // apply() salva em segundo plano sem travar o celular
            }

            // 2. Dispara um sinal de "Sucesso" para a NavegacaoPrincipal
            setFragmentResult("cadastro_produtor_request", Bundle().apply {
                putBoolean("sucesso", true)
            })

            // 3. Volta para a tela principal
            findNavController().popBackStack()
        }

        // Lógica de Cancelar/Voltar (Não salva nada)
        binding.cadastroProdutorBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


