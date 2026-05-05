package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.databinding.FragmentNavegacaoPrincipalBinding
import com.aracecultura.arace.ui.main.jetpack.SeletorModoBottomSheet


class NavegacaoPrincipal : Fragment() {

    private var _binding: FragmentNavegacaoPrincipalBinding? = null
    private val binding get() = this._binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentNavegacaoPrincipalBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getFragment é necessário pois o acesso ao navcontroller é da
        // fragment dentro do fcvNavegacaoPrincipal, que é, na verdade,
        // uma view que pertence à main activity.
        this.binding.bnvMenuInferiorNavegacao.setupWithNavController(
            this.binding.fcvNavegacaoPrincipal.getFragment<NavHostFragment>().navController
        )

        this.binding.btnMenuModo.setOnClickListener {
            val bottomSheet = SeletorModoBottomSheet { modoSelecionado ->
                quandoModoMudar(modoSelecionado)
            }
            bottomSheet.show(childFragmentManager, "SeletorModo")
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

}