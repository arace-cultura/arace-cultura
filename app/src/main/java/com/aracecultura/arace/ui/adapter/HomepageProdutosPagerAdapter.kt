package com.aracecultura.arace.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aracecultura.arace.databinding.ItemPageHomeBinding

// Se for passar apenas imagens, a lista será List<Int> em vez de List<Model>
class HomepageProdutosPagerAdapter(
    private val images: List<Int>,
    private val onItemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<HomepageProdutosPagerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(binding: ItemPageHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemPageHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        // Pega o ID da imagem na posição atual
        val imageResId = images[position]

        // Define a imagem na View
        (holder.itemView as ImageView).setImageResource(imageResId)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
}