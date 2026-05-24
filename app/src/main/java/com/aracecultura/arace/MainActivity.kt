package com.aracecultura.arace

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
        setupBottomNavigation()
    }

    private fun setupWindowDecor() {
        //enableEdgeToEdge()

        /*ViewCompat.setOnApplyWindowInsetsListener(this.binding.mainActivity) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/


        WindowCompat.setDecorFitsSystemWindows(window,false)
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())
    }

    private fun setupBottomNavigation() {
        val navController =
            this.binding.fragmentContainerView.getFragment<NavHostFragment>().navController

        this.binding.bnvMenuInferiorNavegacao.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val showNav = arguments?.getBoolean("showBottomNav", false)
                ?: (destination.parent?.arguments?.get("showBottomNav") as NavArgument).defaultValue as Boolean

            this.binding.bnvMenuInferiorNavegacao.visibility =
                if (showNav) View.VISIBLE else View.GONE
        }
    }

}