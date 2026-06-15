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
    }

    private fun setupWindowDecor() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

}
