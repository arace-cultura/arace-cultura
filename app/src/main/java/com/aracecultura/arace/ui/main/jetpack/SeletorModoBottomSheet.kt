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
import com.aracecultura.arace.ui.theme.AraceTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
O botão é criado em compose pelo fato da função que é chamada na NavegacaoPrincipal
ter instanciado a classe SeletorModoBottomSheet (ativada pelo clique no btnMenuModo),
cuja primeira função invocada é a @Composable OpcoesDeModo.

O Compose OpcoesDeModo recebe como parâmetro um comportamento onSelecionar.
Mas consideremos o comportamente como o que foi chamado na class
(fora da meradescrição funcional do Compose): passamos esse comportamento
(onSelecionar) por uma função que recebe modo (como na pópria sintaxe em OpcoesDeModo)
e o passa à var onModoSelecionado, que é uma Unit de comportamento
(a sintaxe particular ocorre por poder ser null, no caso, o onModoSelecionado é null).
O modo é mudado dinamicamente na OpcoesModo por meio da passagem do Modo.CLIENTE
ou Modo.PRODUTOR, ou seja, é a partir dessa função que o modo é determinado positivamente.

Agora possuímos um onModoSelecionado, e o invocamos na NavegacaoPrincipal via o bottomSheet.onModoSelecionado. Como Unit (a rigor, type (Modo) -> Unit), podemos prescrever qualquer comportamente a onModoSelecionado: nesse caso, como obriga o type, passamos o (Modo) via modoEscolhido, e o Unit a função quandoMudar, que efetivamente implementa quaisquer adaptações de interface e manutenção de mudanças. Nesse sentido, a atuação da classe na NavegacaoPrincipal.kt é terminal quando a mecânica de comportamentos está determinada, os casos null tratados e a parametrização rigorosa impõe plena existência de conteúdo.

*/

enum class Modo { CLIENTE, PRODUTOR} // enum é uma classe passsiva cuja única especificação
// é ter um universo de valores limitados. Só é acessível
// Modo.Cliente e Modo.Produtor. Isso é mais seguro tendo
// em vista que já esperamos o acesso via Modo.(...)

class SeletorModoBottomSheet (
    var onModoSelecionado: ((Modo) -> Unit)? = null // Unit é análogo ao void; a val é um comportamento
) : BottomSheetDialogFragment() { // Herda Bottom... para as configurações estéticas dropdown

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AraceTheme {
                    OpcoesDeModo(
                        onSelecionar = { modo ->
                            onModoSelecionado?.invoke(modo)
                            dismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OpcoesDeModo(onSelecionar: (Modo) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(text = "Modo:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSelecionar(Modo.CLIENTE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cliente")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onSelecionar(Modo.PRODUTOR) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Produtor")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}