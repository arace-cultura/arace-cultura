package com.aracecultura.arace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.aracecultura.arace.R
import com.aracecultura.arace.ui.adapter.MyPagerAdapterHome // Certifique-se que o adapter está neste caminho

class HomePage : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotIndicator: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Infla o layout do projeto Arace
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        // 2. Inicializa os componentes
        viewPager = view.findViewById(R.id.viewPagerProdutos)
        dotIndicator = view.findViewById(R.id.dotIndicator)

        // 3. Lista de imagens (Certifique-se que estas imagens existem no seu drawable)
        val images = listOf(
            R.drawable.panela_home,
            R.drawable.passaro_croche,
            R.drawable.escultura_preguica
        )

        // 4. Configura o Adapter (Importado do novo pacote)
        val adapter = MyPagerAdapterHome(images)
        viewPager.adapter = adapter

        // 5. Configura os Dots Indicadores
        setupDotIndicator(images.size)

        // 6. Listener de mudança de página
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })

        return view
    }

    private fun setupDotIndicator(count: Int) {
        dotIndicator.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            // Verifique se o drawable 'dot_selector' foi copiado para o novo projeto
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

    // Mantive o Companion Object caso você precise criar instâncias com parâmetros no futuro
    companion object {
        @JvmStatic
        fun newInstance() = HomePage()
    }
}