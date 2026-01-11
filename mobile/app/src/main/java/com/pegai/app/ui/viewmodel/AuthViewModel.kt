package com.pegai.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _usuarioLogado = MutableStateFlow<User?>(null)
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            val uid = firebaseUser.uid
            Log.d("AUTH_DEBUG", "AuthState mudou: ${firebaseUser?.uid}")
            viewModelScope.launch {
                try {
                    val document = db.collection("users").document(uid).get().await()
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        _usuarioLogado.value = user?.copy(uid = uid)
                    } else {
                        val novo = User(uid = uid, email = firebaseUser.email ?: "")
                        db.collection("users").document(uid).set(novo)
                        _usuarioLogado.value = novo
                    }
                } catch (e: Exception) {
                    _erro.value = "Erro ao recuperar usuário"
                }
            }
        } else {
            _usuarioLogado.value = null
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun cadastrarUsuario(
        nome: String,
        email: String,
        senha: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        _erro.value = null
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    val novoUsuario = User(
                        uid = uid,
                        nome = nome,
                        email = email,
                    )

                    db.collection("users")
                        .document(uid)
                        .set(novoUsuario)
                        .addOnSuccessListener {
                            _usuarioLogado.value = novoUsuario
                            _isLoading.value = false
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _erro.value = "Conta criada, mas erro ao salvar dados: ${e.message}"
                            _usuarioLogado.value = novoUsuario
                        }
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _erro.value = exception.message ?: "Erro ao cadastrar."
            }
    }

    //  FAZER LOGIN
    fun fazerLogin(email: String, senha: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _erro.value = null

        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            _isLoading.value = false
                            if (document.exists()) {
                                val user = document.toObject(User::class.java)
                                _usuarioLogado.value = user?.copy(uid = uid)
                                onSuccess()
                            } else {
                                val usuarioRecuperado = User(uid = uid, email = email, nome = "Usuário")
                                db.collection("users").document(uid).set(usuarioRecuperado)
                                _usuarioLogado.value = usuarioRecuperado
                                onSuccess()
                            }
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            onSuccess()
                        }
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _erro.value = "Erro ao logar: ${exception.message}"
            }
    }

    fun logout() {
        auth.signOut()
        _usuarioLogado.value = null
    }

    private fun verificarLoginSalvo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            _usuarioLogado.value = user.copy(uid = uid)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("AuthViewModel", "Erro ao recuperar dados.")
                }
        }
    }
}