package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentHomePageBinding
import com.aracecultura.arace.ui.adapter.HomepageProdutosPagerAdapter

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = this._binding!!

    private lateinit var vpProdutosSlider: ViewPager2
    private lateinit var dotIndicator: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setupViewPagerProdutos()
    }

    private fun setupViewPagerProdutos() {
        this.vpProdutosSlider = this.binding.viewPagerProdutos
        dotIndicator = this.binding.dotIndicator

        val images = listOf(
            R.drawable.panela_home,
            R.drawable.passaro_croche,
            R.drawable.escultura_preguica
        )

        val adapter = HomepageProdutosPagerAdapter(images) { position ->
            findNavController().navigate(R.id.produto)
        }
        vpProdutosSlider.adapter = adapter

        setupDotIndicator(images.size)

        vpProdutosSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
    }

    private fun setupDotIndicator(count: Int) {
        dotIndicator.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_selector)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }

            dot.layoutParams = params
            dotIndicator.addView(dot)
        }
        updateIndicator(0)
    }

    private fun updateIndicator(position: Int) {
        for (i in 0 until dotIndicator.childCount) {
            val dot = dotIndicator.getChildAt(i) as ImageView
            dot.isSelected = (i == position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

}