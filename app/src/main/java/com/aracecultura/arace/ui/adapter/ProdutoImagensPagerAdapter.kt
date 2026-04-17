package com.aracecultura.arace.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.ItemPageBinding

// Se for passar apenas imagens, a lista será List<Int> em vez de List<Model>
class ProdutoImagensPagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ProdutoImagensPagerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(binding: ItemPageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ItemPageBinding.inflate(
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
    }
}