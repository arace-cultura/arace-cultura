package com.aracecultura.arace.ui.auth

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aracecultura.arace.R
import com.aracecultura.arace.data.ImagemRepository
import com.aracecultura.arace.databinding.FragmentCadastroBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class Cadastro : Fragment() {
    private var _binding: FragmentCadastroBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db = Firebase.firestore

    private var fotoPerfilUri: Uri? = null
    private var bannerUri: Uri? = null

    private val fotoPerfilPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        fotoPerfilUri = uri
        binding.tvFotoPerfilSelecionada.text =
            if (uri == null) "Nenhuma foto selecionada" else "Foto de perfil selecionada"
    }

    private val bannerPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        bannerUri = uri
        binding.tvBannerPerfilSelecionado.text =
            if (uri == null) "Nenhum banner selecionado" else "Banner selecionado"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.cadastroBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cadastroBtn.setOnClickListener {
            this.cadastrar()
        }

        binding.btnSelecionarFotoPerfil.setOnClickListener {
            fotoPerfilPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnSelecionarBannerPerfil.setOnClickListener {
            bannerPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun cadastrar() {
        val senha = this.binding.cadastroSenha.text.toString().trim()
        val email = this.binding.cadastroInput.text.toString().trim()
        val nome = this.binding.nomeInput.text.toString().trim()

        if(!validarCredenciais(nome, email, senha)){
            Toast
                .makeText(
                    requireContext(),
                    "Preencha todos os dados corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
            return
        }

        this.auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { cadastro ->
            if (cadastro.isSuccessful) {
                val userUID = cadastro.result.user?.uid

                if(userUID != null) {
                    val novoUsuario = hashMapOf(
                        "nome" to nome,
                        "isProdutor" to false
                    )

                    // Grava o documento e sobe as imagens em segundo plano.
                    // A navegação NÃO espera o ack do servidor: a conta já
                    // existe e o cache offline do Firestore reflete a escrita
                    // localmente, então a home já lê os dados.
                    this.db.collection("Usuarios")
                        .document(userUID)
                        .set(novoUsuario)
                        .addOnFailureListener {
                            android.util.Log.e("Cadastro", "Falha ao salvar dados do usuário", it)
                        }

                    // Precisa ser chamado enquanto o fragment está anexado
                    // (captura o contexto); o upload em si roda no escopo da activity
                    enviarImagensPerfil(userUID)

                    findNavController().navigate(R.id.action_global_to_main)
                }
            }
        }.addOnFailureListener { exception ->
            val mensagemErro = when(exception) {
                is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
                is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
                is FirebaseAuthUserCollisionException -> "Conta já cadastrada. Faça login"
                is FirebaseNetworkException -> "Verifique sua conexão com a internet e tente novamente!"
                else -> "Erro ao cadastrar usuário"
            }

            Toast
                .makeText(
                    requireContext(),
                    mensagemErro,
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    // Upload em segundo plano: a navegação não espera as imagens — se algum
    // upload falhar, o usuário segue com o perfil sem imagem e pode adicionar
    // depois pela edição de perfil
    private fun enviarImagensPerfil(uid: String) {
        val foto = fotoPerfilUri
        val banner = bannerUri
        if (foto == null && banner == null) return

        // lifecycleScope do fragment morre na navegação; o escopo da activity
        // sobrevive, e o applicationContext é capturado antes do detach
        val appContext = requireContext().applicationContext
        requireActivity().lifecycleScope.launch {
            try {
                val updates = mutableMapOf<String, Any>()
                foto?.let {
                    updates["fotoUrl"] = ImagemRepository.upload(appContext, uid, "perfil", it)
                }
                banner?.let {
                    updates["bannerUrl"] = ImagemRepository.upload(appContext, uid, "banner", it)
                }
                if (updates.isNotEmpty()) {
                    db.collection("Usuarios").document(uid)
                        .set(updates, SetOptions.merge())
                }
            } catch (_: Exception) {
                // sem imagem: estado válido; edição de perfil cobre depois
            }
        }
    }

    private fun validarCredenciais(nome: String, email: String, senha: String): Boolean {
        val nomeValido = nome.isNotEmpty()
        val emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val senhaValida = senha.length >= 6

        return nomeValido && emailValido && senhaValida
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}