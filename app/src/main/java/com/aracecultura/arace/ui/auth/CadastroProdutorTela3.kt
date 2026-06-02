package com.aracecultura.arace.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth // <-- ADICIONE ESTE IMPORT
import com.google.firebase.firestore.FirebaseFirestore

class CadastroProdutorTela3 : Fragment(R.layout.fragment_cadastro_produtor_tela3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val produtorRecebido = arguments?.getParcelable<Produtor>("produtorData") ?: Produtor()

        val etCep = view.findViewById<TextInputEditText>(R.id.etCep)
        val etEndereco = view.findViewById<TextInputEditText>(R.id.etEndereco)
        val etTipoArt = view.findViewById<TextInputEditText>(R.id.etTipoArtesanato)
        val acCategoria = view.findViewById<AutoCompleteTextView>(R.id.acCategoria)

        val categorias = arrayOf("Artesanato", "Têxteis", "Cosméticos", "Casa", "Cerâmica", "Acessórios")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias)
        acCategoria.setAdapter(adapter)

        view.findViewById<Button>(R.id.btnFinalizar).setOnClickListener {
            val produtorFinal = produtorRecebido.copy(
                cep = etCep.text.toString(),
                endereco = etEndereco.text.toString(),
                tipoArtesanato = etTipoArt.text.toString(),
                categoriaProduto = acCategoria.text.toString()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("Produtores")
                .add(produtorFinal)
                .addOnSuccessListener {

                    // 1. Pegamos o ID do usuário atualmente logado
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "desconhecido"

                    // 2. Salvamos a preferência atrelada a ESSE usuário
                    val sharedPref = requireActivity().getSharedPreferences(
                        "AracePrefs",
                        android.content.Context.MODE_PRIVATE
                    )
                    sharedPref.edit().putBoolean("STATUS_PRODUTOR_$userId", true).apply()

                    parentFragmentManager.setFragmentResult(
                        "cadastro_produtor_request",
                        bundleOf("sucesso" to true)
                    )

                    findNavController().navigate(R.id.action_cadastro_concluido)
                }
        }

        view.findViewById<TextView>(R.id.btnVoltar3).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}