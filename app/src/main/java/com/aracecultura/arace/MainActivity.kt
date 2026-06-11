package com.aracecultura.arace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.aracecultura.arace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        this.binding = ActivityMainBinding.inflate(
            layoutInflater
        )

        setContentView(this.binding.root)

        setupWindowDecor()
        //setupBottomNavigation()
    }

    private fun setupWindowDecor() {
        // Modo padrão: o decor acomoda as barras do sistema e o conteúdo do
        // app nunca fica sob elas. O modo anterior (edge-to-edge com
        // decorFitsSystemWindows=false + hide(statusBars) e NENHUM consumo de
        // insets) deixava a janela inteira sob as barras — provável raiz das
        // "correções" espúrias de scroll nas listas (carrinho/explorar/home),
        // cuja magnitude coincidia com a altura das barras inferiores.
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    /*private fun setupBottomNavigation() {
        val navController =
            this.binding.fragmentContainerView.getFragment<NavHostFragment>().navController

        this.binding.bnvMenuInferiorNavegacao.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val showNav = arguments?.getBoolean("showBottomNav", false)
                ?: (destination.parent?.arguments?.get("showBottomNav") as NavArgument).defaultValue as Boolean

            this.binding.bnvMenuInferiorNavegacao.visibility =
                if (showNav) View.VISIBLE else View.GONE
        }
    }*/

}