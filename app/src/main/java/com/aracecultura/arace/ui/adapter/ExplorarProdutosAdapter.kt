package com.aracecultura.arace.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aracecultura.arace.data.model.Produto
import com.aracecultura.arace.databinding.ViewholderExplorarProdutosBinding

class ExplorarProdutosAdapter(
    private val produtos: List<Produto>
) : RecyclerView.Adapter<ExplorarProdutosAdapter.VH>() {
    class VH(val binding: ViewholderExplorarProdutosBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(
            ViewholderExplorarProdutosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        val produto = this.produtos[pos]

        holder.binding.apply {
            nome.text = produto.nome
            descricao.text = produto.descricao
            avaliacao.rating = produto.avaliacao
            preco.text = produto.preco.toString()
        }
    }

    override fun getItemCount(): Int = this.produtos.size
}