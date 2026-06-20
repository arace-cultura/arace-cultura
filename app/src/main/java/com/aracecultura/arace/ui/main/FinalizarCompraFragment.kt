package com.aracecultura.arace.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.ui.components.carrinho.CheckoutPaymentScreen
import com.aracecultura.arace.ui.theme.AraceTheme
import com.google.firebase.auth.FirebaseAuth

class FinalizarCompraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AraceTheme {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                    CheckoutPaymentScreen(
                        navController = findNavController(),
                        uid = uid
                    )
                }
            }
        }
    }
}
