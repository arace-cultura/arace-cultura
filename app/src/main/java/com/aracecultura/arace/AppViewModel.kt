package com.aracecultura.arace

import androidx.lifecycle.ViewModel
import com.aracecultura.arace.ui.main.jetpack.Modo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {
    private val _modo = MutableStateFlow(Modo.CLIENTE)
    val modo: StateFlow<Modo> = _modo.asStateFlow()

    fun definirModo(m: Modo) { _modo.value = m }
    // logout, "cadastro concluído", etc. — chamados direto pelas telas,
    // sem precisar de chave de string nem listener
}