package com.aracecultura.arace.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aracecultura.arace.R

class Favoritos : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Desloga do Firebase
        // FirebaseAuth.getInstance().signOut()

        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }
}