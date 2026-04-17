package com.aracecultura.arace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentProdutoBinding
import com.aracecultura.arace.ui.adapter.ProdutoImagensPagerAdapter

// Passamos o layout no construtor do Fragment
class Produto : Fragment() {
    private var _binding: FragmentProdutoBinding? = null
    private val binding get() = this._binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var dotIndictor: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentProdutoBinding.inflate(
            inflater,
            container,
            false
        )
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBlurView()
        setupViewPagerImagensProduto()

        val setaVoltar = this.binding.setaVoltarMenu

        setaVoltar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupViewPagerImagensProduto() {
        viewPager = this.binding.viewPager
        dotIndictor = this.binding.dotIndictor

        val items = listOf(
            R.drawable.panela1,
            R.drawable.panelas2,
            R.drawable.panelas3
        )

        val adapter = ProdutoImagensPagerAdapter(items)
        viewPager.adapter = adapter

        createDotIndictor(items.size)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int){
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
    }

    private fun setupBlurView() {
        // IMPORTANTE: Em Fragments, usamos 'view.findViewById'
        val blurView = this.binding.header
        val targetView = this.binding.meuAlvo

        val radius = 5f

        // Em Fragments, precisamos acessar a Activity para pegar o decorView
        val decorView = this.binding.meuAlvo.background
        val windowBackground = decorView

        blurView.setupWith(targetView, 3f, false)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}