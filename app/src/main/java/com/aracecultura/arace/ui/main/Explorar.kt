package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aracecultura.arace.databinding.FragmentExplorarBinding
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.ui.adapter.ExplorarProdutosAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Explorar : Fragment() {
    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = this._binding!!

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentExplorarBinding.inflate(
            inflater,
            null,
            false
        )
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.db.collection("Produtos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val produtos: MutableList<Produto> = ArrayList()

                for(produto in querySnapshot) {
                    val prod = Produto(
                        id = produto.id,
                        nome = produto.getString("nome")!!,
                        descricao = produto.getString("descricao")!!,
                        avaliacao = produto.getDouble("avaliacao")!!.toFloat(),
                        preco = produto.getDouble("preco")!!
                    )
                    produtos.add(prod)
                }

                loadRVProdutos(produtos)
            }
    }

    fun loadRVProdutos(
        produtos: List<Produto>
    ) {
        val adapter = ExplorarProdutosAdapter(produtos)

        this.binding.rvProdutos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}