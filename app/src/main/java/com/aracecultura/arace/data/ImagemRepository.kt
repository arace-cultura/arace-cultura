package com.aracecultura.arace.data

import android.content.Context
import android.net.Uri
import com.aracecultura.arace.supabase
import io.github.jan.supabase.storage.storage
import java.util.UUID

/** Upload de imagens para o bucket padrão do app (Supabase). */
object ImagemRepository {

    /**
     * Sobe a imagem para "imagens/{pasta}/{prefixo}-{uuid}.jpg" e retorna a URL pública.
     */
    suspend fun upload(context: Context, pasta: String, prefixo: String, uri: Uri): String {
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            ?: throw Exception("Não foi possível processar a imagem.")

        val bucket = supabase.storage.from("imagens")
        val caminho = "$pasta/$prefixo-${UUID.randomUUID()}.jpg"
        bucket.upload(path = caminho, data = bytes) { upsert = true }
        return bucket.publicUrl(caminho)
    }
}
