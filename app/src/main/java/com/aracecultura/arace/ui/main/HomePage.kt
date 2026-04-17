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

        setupViewPagerProdutos()
    }

    private fun setupViewPagerProdutos() {
        /**
         * Todo código abaixo precisa de refatoração e explicação.
         *
         * Ao invés de utilizar todo esse boilerplate, não seria melhor
         * utilizar um tablayout? Ele já possui integração nativa com o
         * ViewPager.
         *
         * Vi que o fragment_produto_home está com uma meia implementação disso.
         * O responsável: por favor, limpe se não for utilizar ou integre
         * adequadamente.
         **/
        // 1. Infla o layout do projeto Arace

        // 2. Inicializa os componentes
        this.vpProdutosSlider = this.binding.viewPagerProdutos
        dotIndicator = this.binding.dotIndicator

        // 3. Lista de imagens (Certifique-se que estas imagens existem no seu drawable)
        val images = listOf(
            R.drawable.panela_home,
            R.drawable.passaro_croche,
            R.drawable.escultura_preguica
        )

        // 4. Configura o Adapter (Importado do novo pacote)
        val adapter = HomepageProdutosPagerAdapter(images) { position ->
            requireActivity().findNavController(R.id.main).navigate(R.id.produto)
        }
        vpProdutosSlider.adapter = adapter

        // 5. Configura os Dots Indicadores
        setupDotIndicator(images.size)

        // 6. Listener de mudança de página
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

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

    // Preciso de explicação sobre isso.
    // Mantive o Companion Object caso você precise criar instâncias com parâmetros no futuro
    companion object {
        @JvmStatic
        fun newInstance() = HomePage()
    }
}