package com.aracecultura.arace.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aracecultura.arace.R

// Se for passar apenas imagens, a lista será List<Int> em vez de List<Model>
class MyPagerAdapterHome(private val images: List<Int>) :
    RecyclerView.Adapter<MyPagerAdapterHome.PagerViewHolder>() {

    // Dica de performance: Faça o findViewById aqui dentro do ViewHolder, não no onBind
    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_page_home, parent, false)
        return PagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        // Pega o ID da imagem na posição atual
        val imageResId = images[position]

        // Define a imagem na View
        holder.imageView.setImageResource(imageResId)
    }
}