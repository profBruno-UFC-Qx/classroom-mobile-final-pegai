package com.pegai.app.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
//para o FireBase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pegai.app.model.User

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //cadastrar usuario
    fun cadastrarUsuario(
        nome: String,
        sobrenome: String,
        email: String,
        senha: String,
        telefone: String,
        curso: String,
        matricula: String,
        onSucess: () -> Unit,
        onError:(String) -> Unit
    ){
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userMap = hashMapOf(
                    "nome" to nome,
                    "sobrenome" to sobrenome,
                    "email" to email,
                    "telefone" to telefone,
                    "curso" to curso,
                    "matricula" to matricula,
                )
                db.collection("Users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener { onSucess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Erro ao salvar usuario") }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Erro no cadastro")
            }
    }

    //função para verificar o login
    fun login(
        email: String,
        password: String,
        onSucess: (User) -> Unit,
        onError: (String) -> Unit
    ){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        Log.d("FIRESTORE","Documento: ${document.data}")
                        if(document.exists()) {
                            val user = document.toObject(User::class.java)
                            if (user != null) {
                                onSucess(user.copy(uid = uid))
                            } else {
                                onError("Erro ao carregar dados")
                            }
                        } else {
                            onError("Usuario sem dados cadastrados no sistema")
                        }
                    }
                    .addOnFailureListener { e ->
                        onError("Erro ao buscar usuario: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                val msg = when {    
                    "password" in e.message.toString().lowercase() -> "Senha incorreta."
                    "no user record" in e.message.toString().lowercase() -> "Usuário não encontrado."
                    "badly formatted" in e.message.toString().lowercase() -> "E-mail inválido."
                    else -> "Erro ao fazer login. Tente novamente."
                }
                onError(msg)
            }
    }
}