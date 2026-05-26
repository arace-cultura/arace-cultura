package com.aracecultura.arace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // A HERANÇA DA APP-DEV: Isso desenha a logo do app com base no themes.xml
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // LÓGICA DE ROTEAMENTO INICIAL DA SPLASH:
        // Como o app sempre inicia no auth_graph (Login), a Splash Screen
        // é o lugar perfeito para verificar o Firebase.

        // Exemplo:
        // Se (FirebaseAuth.getInstance().currentUser != null) {
        // val navController = findNavController(R.id.nav_host_fragment_container_da_activity)
        // navController.navigate(R.id.action_global_to_main)
        // }
    }
}