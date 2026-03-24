package com.aracecultura.arace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aracecultura.arace.databinding.ActivityMainBinding
import com.aracecultura.arace.ui.Favoritos
import com.aracecultura.arace.ui.HomePage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        val dbAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val currentUser = dbAuth.currentUser

            if (currentUser != null) {
                // Usuário logado: Inicia na HomePage
                replaceFragment(HomePage())
                binding.bottomNavigationView.selectedItemId = R.id.home
            } else {
                // usuario deslogado, volta pra entrada
                replaceFragment(Entrada())
            }
        }
        if (savedInstanceState == null) {
            replaceFragment(HomePage())
            binding.bottomNavigationView.selectedItemId = R.id.home
        }


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                // TODO: implementar outras paginas conforme forem sendo criadas
                // R.id.explorar -> replaceFragment(fragmentoExplorar())
                R.id.home -> replaceFragment(HomePage())
                R.id.fav -> replaceFragment(Favoritos())

                else -> {

                }
            }
            true
        }

    }


    // Função que faz a troca de telas no FrameLayout
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // Certifique-se de que o ID do seu container no XML seja @+id/frame_layout
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}