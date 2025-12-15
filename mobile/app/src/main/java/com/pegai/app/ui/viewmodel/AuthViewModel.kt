package com.pegai.app.ui.viewmodel

import androidx.lifecycle.ViewModel

//para o FireBase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel: ViewModel() {
    private val _usuarioLogado = MutableStateFlow<User?>(null)
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    fun setUsuarioLogado(user: User) {
        _usuarioLogado.value = user
    }


}