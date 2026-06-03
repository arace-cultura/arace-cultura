package com.aracecultura.arace

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.tasks.await

val supabase = createSupabaseClient(
    supabaseUrl = "https://tliyhytinombxvsmdphm.supabase.co",
    supabaseKey = "tliyhytinombxvsmdphm"
) {
    install(Storage)

    accessToken = {
        Firebase.auth.currentUser?.getIdToken(false)?.await()?.token
    }
}