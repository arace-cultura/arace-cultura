package com.aracecultura.arace.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.adapter.MyPagerAdapter
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.BlurTarget

// Passamos o layout no construtor do Fragment
class ProdutoFragment : Fragment(R.layout.fragment_produto) {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotIndictor: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IMPORTANTE: Em Fragments, usamos 'view.findViewById'
        val blurView = view.findViewById<BlurView>(R.id.header)
        val targetView = view.findViewById<BlurTarget>(R.id.meuAlvo)

        val radius = 5f

        // Em Fragments, precisamos acessar a Activity para pegar o decorView
        val decorView = requireActivity().window.decorView
        val windowBackground = decorView.background

        blurView.setupWith(targetView, 3f, false)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)

        viewPager = view.findViewById(R.id.viewPager)
        dotIndictor = view.findViewById(R.id.dotIndictor)

        val items = listOf(
            R.drawable.panela1,
            R.drawable.panelas2,
            R.drawable.panelas3
        )

        val adapter = MyPagerAdapter(items)
        viewPager.adapter = adapter

        createDotIndictor(items.size)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int){
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
    }

    private fun createDotIndictor(count: Int) {
        // 'requireContext()' é usado no lugar de 'this' dentro de um Fragment
        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_selector)
            dotIndictor.addView(dot)
        }
    }

    private fun updateIndicator(position: Int){
        for(i in 0 until dotIndictor.childCount){
            val dot = dotIndictor.getChildAt(i) as ImageView
            dot.isSelected = i == position
        }
    }
}