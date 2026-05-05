package com.aracecultura.arace.ui.main.jetpack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*

*/

class SeletorModoBottomSheet (
    private val onModoselecionado: (Modo) -> Unit // Unit é análogo ao void; a val é um comportamento
) : BottomSheetDialogFragment() { // Herda Bottom... para as configurações estéticas dropdown
    enum class Modo { CLIENTE, PRODUTOR} // enum é uma classe passsiva cuja única especificação
                                         // é ter um universo de valores limitados. Só é acessível
                                         // Modo.Cliente e Modo.Produtor. Isso é mais seguro tendo
                                         // em vista que já esperamos o acesso via Modo.(...)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    OpcoesDeModo(
                        onSelecionar = { modo ->
                            onModoSelecionado(modo)
                            dismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OpcoesDeModo(onSelecionar: (SeletorModoBottomSheet.Modo) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(text = "Modo:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSelecionar(SeletorModoBottomSheet.Modo.CLIENTE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cliente")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onSelecionar(SeletorModoBottomSheet.Modo.PRODUTOR) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Produtor")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
