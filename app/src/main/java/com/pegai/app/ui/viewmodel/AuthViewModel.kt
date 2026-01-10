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

class AuthViewModel : ViewModel() {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _usuarioLogado = MutableStateFlow<User?>(null)
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

=======

    // Instâncias do Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estado do Usuário Global (Acessível por todo o app)
    private val _usuarioLogado = MutableStateFlow<User?>(null)
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    // --- BLOCO DE INICIALIZAÇÃO  ---
    // Assim que o app abre, ele verifica se já existe alguém logado no cache do Firebase
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
    init {
        verificarLoginSalvo()
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
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
=======
    // --- FUNÇÕES DE AÇÃO ---

    fun setUsuarioLogado(user: User) {
        _usuarioLogado.value = user
    }

    fun logout() {
        // Avisa o Firebase para destruir a sessão
        auth.signOut()

        // Limpa o estado local para a UI reagir (voltar para "Visitante")
        _usuarioLogado.value = null
    }

    // --- LÓGICA INTERNA ---

    private fun verificarLoginSalvo() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Se o Firebase diz que tem alguém logado, vamos buscar os dados dele no Banco
            // (Nome, Foto etc) para preencher a tela
            val uid = currentUser.uid

            db.collection("users")
                .document(uid)
                .get()
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            _usuarioLogado.value = user.copy(uid = uid)
                        }
                    }
                }
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
                .addOnFailureListener {
                    Log.e("AuthViewModel", "Erro ao recuperar dados.")
=======
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Erro ao recuperar dados do usuário: ${e.message}")
                    // Se falhar (ex: sem internet), mantém logado mas sem os dados completos por enquanto
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/AuthViewModel.kt
                }
        }
    }
}