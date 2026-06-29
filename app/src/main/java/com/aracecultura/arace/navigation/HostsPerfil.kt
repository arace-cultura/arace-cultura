package com.aracecultura.arace.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aracecultura.arace.AppViewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.ui.components.explorar.TelaConfiguracoes
import com.aracecultura.arace.ui.components.perfil.cliente.EditarPerfilUsuario
import com.aracecultura.arace.ui.components.perfil.cliente.MeusDados
import com.aracecultura.arace.ui.components.perfil.cliente.PerfilCliente
import com.aracecultura.arace.ui.components.perfil.cliente.TelaMeusPedidos
import com.aracecultura.arace.ui.components.perfil.produtor.PerfilProdutor
import com.aracecultura.arace.ui.components.perfil.produtor.TelaDestaques
import com.aracecultura.arace.ui.components.produto.TelaEditarProdutos
import com.aracecultura.arace.ui.main.jetpack.Modo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private enum class TelaPerfil { PERFIL, EDITAR, CONFIGURACOES, MEUS_DADOS, MEUS_PEDIDOS }

/**
 * Host do perfil do cliente. Reúne as sub-telas (perfil, edição, configurações,
 * meus dados) num estado interno.
 */
@Composable
fun HostPerfilCliente(
    uid: String,
    rootNav: NavController,
    appVm: AppViewModel,
    onFooterVisibleChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    var telaAtual by remember { mutableStateOf(TelaPerfil.PERFIL) }

    LaunchedEffect(telaAtual) {
        onFooterVisibleChanged(telaAtual != TelaPerfil.EDITAR)
    }

    DisposableEffect(Unit) {
        onDispose { onFooterVisibleChanged(true) }
    }

    fun sairParaAuth() {
        FirebaseAuth.getInstance().signOut()
        rootNav.navigate(Rota.AuthGraph) {
            popUpTo(Rota.MainGraph) { inclusive = true }
        }
    }

    fun deletarConta() {
        val usuario = FirebaseAuth.getInstance().currentUser ?: return
        escopo.launch {
            try {
                usuario.delete().await()
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("Usuarios").document(usuario.uid).delete().await()
                }
                sairParaAuth()
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                Toast.makeText(
                    context, R.string.erro_deletar_conta_login_recente, Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.erro_deletar_conta, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (telaAtual) {
        TelaPerfil.PERFIL -> PerfilCliente(
            uid = uid,
            onEditClick = { telaAtual = TelaPerfil.EDITAR },
            onSettingsClick = { telaAtual = TelaPerfil.CONFIGURACOES },
            onLogoutClick = { sairParaAuth() },
            onModoChanged = { isProdutor ->
                if (isProdutor) entrarModoProdutor(uid, appVm, rootNav, escopo)
                else appVm.definirModo(Modo.CLIENTE)
            },
        )
        TelaPerfil.EDITAR -> EditarPerfilUsuario(
            uid = uid,
            onVoltarClick = { telaAtual = TelaPerfil.PERFIL },
        )
        TelaPerfil.CONFIGURACOES -> TelaConfiguracoes(
            onBackClick = { telaAtual = TelaPerfil.PERFIL },
            onMeusDadosClick = { telaAtual = TelaPerfil.MEUS_DADOS },
            onMeusPedidosClick = { telaAtual = TelaPerfil.MEUS_PEDIDOS },
            onDeletarContaClick = { deletarConta() },
        )
        TelaPerfil.MEUS_DADOS -> MeusDados(
            uid = uid,
            onVoltarClick = { telaAtual = TelaPerfil.CONFIGURACOES },
            onEditarClick = { telaAtual = TelaPerfil.EDITAR },
        )
        TelaPerfil.MEUS_PEDIDOS -> TelaMeusPedidos(
            uid = uid,
            onVoltar = { telaAtual = TelaPerfil.CONFIGURACOES },
        )
    }
}

/**
 * Host do perfil do produtor: alterna entre perfil e edição de produtos num
 * estado interno.
 */
private enum class TelaProdutor { PERFIL, EDITAR, DESTAQUES }

@Composable
fun HostPerfilProdutor(
    uid: String,
    appVm: AppViewModel,
) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    var telaAtual by remember { mutableStateOf(TelaProdutor.PERFIL) }

    when (telaAtual) {
        TelaProdutor.EDITAR -> TelaEditarProdutos(
            uid = uid,
            onBack = { telaAtual = TelaProdutor.PERFIL }
        )
        TelaProdutor.DESTAQUES -> TelaDestaques(
            uid = uid,
            onBack = { telaAtual = TelaProdutor.PERFIL }
        )
        TelaProdutor.PERFIL -> PerfilProdutor(
            uid = uid,
            onModoChanged = { isProdutor ->
                if (!isProdutor) appVm.definirModo(Modo.CLIENTE)
            },
            onEditarProdutos = { telaAtual = TelaProdutor.EDITAR },
            onMeusDestaques = { telaAtual = TelaProdutor.DESTAQUES },
            onSairLojaClick = {
                escopo.launch {
                    try {
                        LojaRepository.sairDaLoja(uid)
                        appVm.definirModo(Modo.CLIENTE)
                    } catch (e: Exception) {
                        Toast.makeText(context, R.string.erro_sair_loja, Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
    }
}

/** Leva ao modo produtor: se já tem loja, troca o modo; senão, abre o cadastro de loja. */
private fun entrarModoProdutor(
    uid: String,
    appVm: AppViewModel,
    rootNav: NavController,
    escopo: CoroutineScope,
) {
    escopo.launch {
        val temLoja = try {
            LojaRepository.resolverLojaId(uid) != null
        } catch (e: Exception) {
            false
        }
        if (temLoja) appVm.definirModo(Modo.PRODUTOR)
        else rootNav.navigate(Rota.CadastroProdutorGraph)
    }
}
