package com.aracecultura.arace.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.model.Produtor
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class CadastroProdutorTela3 : Fragment(R.layout.fragment_cadastro_produtor_tela3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val produtorRecebido = arguments?.getParcelable<Produtor>("produtorData")
            ?: Produtor()

        val etCep = view.findViewById<TextInputEditText>(R.id.etCep)
        val etEndereco = view.findViewById<TextInputEditText>(R.id.etEndereco)
        val etTipoArt = view.findViewById<TextInputEditText>(R.id.etTipoArtesanato)
        val acCategoria = view.findViewById<AutoCompleteTextView>(R.id.acCategoria)

        val categorias = arrayOf("Artesanato", "Têxteis", "Cosméticos", "Casa", "Cerâmica", "Acessórios")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias)
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
                .addOnSuccessListener { documentReference ->
                    findNavController().navigate(R.id.action_cadastro_concluido)
                }
                .addOnFailureListener { e ->
                    // Sem lidar com erro ainda
                }
        }

        view.findViewById<TextView>(R.id.btnVoltar3).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}