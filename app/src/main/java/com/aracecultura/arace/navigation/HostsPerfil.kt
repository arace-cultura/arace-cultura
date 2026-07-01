package com.aracecultura.arace.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aracecultura.arace.AppViewModel
import com.aracecultura.arace.R
import com.aracecultura.arace.data.LojaRepository
import com.aracecultura.arace.ui.auth.CampoArace
import com.aracecultura.arace.ui.components.explorar.TelaConfiguracoes
import com.aracecultura.arace.ui.components.perfil.cliente.EditarPerfilUsuario
import com.aracecultura.arace.ui.components.perfil.cliente.MeusDados
import com.aracecultura.arace.ui.components.perfil.cliente.PerfilCliente
import com.aracecultura.arace.ui.components.perfil.cliente.TelaMeusPedidos
import com.aracecultura.arace.ui.components.perfil.produtor.PerfilProdutor
import com.aracecultura.arace.ui.components.perfil.produtor.TelaDestaques
import com.aracecultura.arace.ui.components.produto.TelaEditarProdutos
import com.aracecultura.arace.ui.main.jetpack.Modo
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
    // Fluxo de deleção: ao confirmar, pedimos login (reautenticação) antes de
    // apagar — o Firebase exige credencial recente para delete().
    var pedindoReautenticacao by remember { mutableStateOf(false) }
    var reautenticando by remember { mutableStateOf(false) }

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

    // Reautentica com a senha informada e só então deleta. A ordem importa:
    // apagamos o doc no Firestore ENQUANTO ainda autenticado (a regra exige
    // request.auth) e só depois removemos a conta do Auth — senão o doc fica órfão.
    fun reautenticarEDeletar(senha: String) {
        val usuario = FirebaseAuth.getInstance().currentUser ?: return
        val email = usuario.email
        if (email.isNullOrBlank()) {
            Toast.makeText(context, R.string.erro_deletar_conta, Toast.LENGTH_SHORT).show()
            return
        }
        reautenticando = true
        escopo.launch {
            try {
                usuario.reauthenticate(EmailAuthProvider.getCredential(email, senha)).await()
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("Usuarios").document(usuario.uid).delete().await()
                }
                usuario.delete().await()
                pedindoReautenticacao = false
                sairParaAuth()
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, R.string.senha_incorreta, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.erro_deletar_conta, Toast.LENGTH_SHORT).show()
            } finally {
                reautenticando = false
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
            onDeletarContaClick = { pedindoReautenticacao = true },
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

    if (pedindoReautenticacao) {
        DialogoReautenticacao(
            email = FirebaseAuth.getInstance().currentUser?.email.orEmpty(),
            carregando = reautenticando,
            onConfirmar = { senha -> reautenticarEDeletar(senha) },
            onCancelar = { if (!reautenticando) pedindoReautenticacao = false },
        )
    }
}

/**
 * Pede o login (senha) do usuário antes de deletar a conta. O Firebase Auth
 * exige credencial recente para operações sensíveis como delete(); esta é a
 * reautenticação. Enquanto [carregando], os botões ficam inativos.
 */
@Composable
private fun DialogoReautenticacao(
    email: String,
    carregando: Boolean,
    onConfirmar: (senha: String) -> Unit,
    onCancelar: () -> Unit,
) {
    var senha by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!carregando) onCancelar() },
        containerColor = Color(0xFFFAF7F2),
        title = {
            Text(
                text = stringResource(R.string.reautenticar_titulo),
                color = Color(0xFF2E2B27),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.reautenticar_msg),
                    color = Color(0xFF7A7168),
                )
                Spacer(Modifier.height(12.dp))
                Text(text = email, color = Color(0xFF2E2B27), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                CampoArace(
                    valor = senha,
                    aoMudar = { senha = it },
                    rotulo = stringResource(R.string.senha),
                    senha = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(senha) },
                enabled = !carregando && senha.isNotBlank(),
            ) {
                Text(
                    text = stringResource(R.string.sim_deletar_conta),
                    color = Color(0xFFCE5A14),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, enabled = !carregando) {
                Text(stringResource(R.string.cancelar), color = Color(0xFF7A7168))
            }
        },
    )
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
