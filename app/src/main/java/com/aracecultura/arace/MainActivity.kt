package com.aracecultura.arace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    /**
     * Por referência futura: binding desnecessário na activity host.
     **/
    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(R.layout.activity_main)

        /**
         * Padrão do template lida com a barra de status superior e
         * inferior automaticamente. Eliminar foi um erro.
         **/
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Para recurso futuro. Já inicia a conexão com o Firebase.
    // Por enquando, google-services.json está em um dev bd com acesso livre.
    private fun loadFirebase() {
        //FirebaseApp.initializeApp(this)
        //val dbAuth = FirebaseAuth.getInstance()
    }
}