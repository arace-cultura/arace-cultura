package com.aracecultura.arace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.aracecultura.arace.databinding.ActivityMainBinding
import com.aracecultura.arace.ui.components.ensureMinimumTouchTargets

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            MinimumTouchTargetCallbacks,
            true
        )

        this.binding = ActivityMainBinding.inflate(
            layoutInflater
        )

        setContentView(this.binding.root)

        setupWindowDecor()
    }

    private fun setupWindowDecor() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private object MinimumTouchTargetCallbacks :
        FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewCreated(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            view: android.view.View,
            savedInstanceState: Bundle?
        ) {
            view.ensureMinimumTouchTargets()
        }
    }
}
