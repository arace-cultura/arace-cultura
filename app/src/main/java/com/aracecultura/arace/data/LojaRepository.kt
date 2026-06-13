package com.aracecultura.arace.data

import com.aracecultura.arace.data.model.Produtor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

/**
 * Fonte única para o vínculo conta ↔ loja.
 *
 * Modelo: uma loja (documento em Produtores/{lojaId}) pode ser compartilhada
 * por várias contas. O vínculo fica em Usuarios/{uid}.lojaId. Lojas têm senha
 * própria (hash em Produtores/{lojaId}.senhaHash) para permitir que outras
 * contas entrem nelas.
 *
 * Compatibilidade: cadastros antigos usavam Produtores/{uid}; ao resolver o
 * vínculo de um usuário legado, o uid é adotado como lojaId e o vínculo é
 * gravado, migrando a conta de forma transparente.
 */
object LojaRepository {

    private val db get() = FirebaseFirestore.getInstance()

    /** Retorna o lojaId vinculado à conta, ou null se a conta não é produtora. */
    suspend fun resolverLojaId(uid: String): String? {
        if (uid.isBlank()) return null

        val usuario = db.collection("Usuarios").document(uid).get().await()
        val lojaId = usuario.getString("lojaId")
        if (!lojaId.isNullOrBlank()) return lojaId

        // Migração de cadastro legado (Produtores keyed por uid)
        val legado = db.collection("Produtores").document(uid).get().await()
        if (legado.exists()) {
            vincularUsuario(uid, uid)
            return uid
        }
        return null
    }

    /**
     * Cria uma nova loja com senha e vincula a conta criadora.
     * Falha se já existir loja com o mesmo nome (o nome é a credencial de login).
     */
    suspend fun criarLoja(uid: String, produtor: Produtor, senha: String): String {
        val nome = produtor.nomeLoja.trim()
        require(nome.isNotBlank()) { "A loja precisa de um nome." }
        require(senha.length >= 4) { "A senha da loja deve ter pelo menos 4 caracteres." }

        val existente = db.collection("Produtores")
            .whereEqualTo("nomeLoja", nome)
            .limit(1)
            .get()
            .await()
        if (!existente.isEmpty) {
            throw IllegalStateException("Já existe uma loja com esse nome. Escolha outro ou use \"Entrar em loja existente\".")
        }

        val docRef = db.collection("Produtores").document()
        docRef.set(produtor.copy(nomeLoja = nome)).await()
        docRef.set(mapOf("senhaHash" to hashSenha(nome, senha)), SetOptions.merge()).await()
        vincularUsuario(uid, docRef.id)
        return docRef.id
    }

    /** Vincula a conta a uma loja existente, validando nome + senha. */
    suspend fun entrarEmLoja(uid: String, nomeLoja: String, senha: String): String {
        val nome = nomeLoja.trim()
        val resultado = db.collection("Produtores")
            .whereEqualTo("nomeLoja", nome)
            .limit(1)
            .get()
            .await()
        val doc = resultado.documents.firstOrNull()
            ?: throw IllegalStateException("Nenhuma loja encontrada com esse nome.")

        val hashArmazenado = doc.getString("senhaHash").orEmpty()
        if (hashArmazenado.isBlank()) {
            throw IllegalStateException("Esta loja ainda não tem senha configurada. Peça ao dono para recadastrá-la.")
        }
        if (hashArmazenado != hashSenha(nome, senha)) {
            throw IllegalStateException("Senha incorreta.")
        }

        vincularUsuario(uid, doc.id)
        return doc.id
    }

    private suspend fun vincularUsuario(uid: String, lojaId: String) {
        db.collection("Usuarios")
            .document(uid)
            .set(mapOf("lojaId" to lojaId, "isProdutor" to true), SetOptions.merge())
            .await()
    }

    // SHA-256 com o nome da loja como salt. Validação client-side: adequada
    // para o escopo do app, mas não substitui regras de segurança no Firestore.
    private fun hashSenha(nomeLoja: String, senha: String): String {
        val texto = "arace:${nomeLoja.trim().lowercase()}:$senha"
        return MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
