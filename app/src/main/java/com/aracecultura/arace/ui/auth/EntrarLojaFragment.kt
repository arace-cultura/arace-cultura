package com.aracecultura.arace.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EntrarLojaFragment : Fragment(R.layout.fragment_entrar_loja) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNome = view.findViewById<TextInputEditText>(R.id.etNomeLoja)
        val etSenha = view.findViewById<TextInputEditText>(R.id.etSenhaLoja)
        val tvErro = view.findViewById<TextView>(R.id.tvErroEntrarLoja)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarEntrada)

        btnConfirmar.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                tvErro.text = "Usuário não autenticado."
                tvErro.isVisible = true
                return@setOnClickListener
            }

            tvErro.isVisible = false
            btnConfirmar.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    LojaRepository.entrarEmLoja(
                        uid = uid,
                        nomeLoja = etNome.text.toString(),
                        senha = etSenha.text.toString()
                    )
                    // Mesmo sinal do cadastro concluído: troca o footer para
                    // produtor e volta à navegação principal.
                    // Barramento único: sinais entre telas sempre no FM da activity
                    requireActivity().supportFragmentManager.setFragmentResult(
                        "cadastro_produtor_request",
                        bundleOf("sucesso" to true)
                    )
                    findNavController().navigate(R.id.action_entrada_concluida)
                } catch (e: Exception) {
                    tvErro.text = e.message ?: "Não foi possível entrar na loja."
                    tvErro.isVisible = true
                    btnConfirmar.isEnabled = true
                }
            }
        }

        view.findViewById<TextView>(R.id.btnVoltarEntrar).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
