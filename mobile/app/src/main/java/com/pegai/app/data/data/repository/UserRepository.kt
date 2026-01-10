package com.pegai.app.data.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao
import kotlinx.coroutines.tasks.await
import java.util.UUID // Importante para a lógica igual a de produtos

object UserRepository {
    private val cacheNomes = mutableMapOf<String, String>()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun getNomeUsuario(userId: String): String {
        return cacheNomes[userId] ?: run {
            try {
                val doc = db.collection("users").document(userId).get().await()
                val nome = doc.getString("nome") ?: "Usuário Pegaí"
                cacheNomes[userId] = nome
                nome
            } catch (e: Exception) {
                "Usuário"
            }
        }
    }

    suspend fun getUsuarioPorId(userId: String): User? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            if (!doc.exists()) null
            else doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTodasAvaliacoes(userId: String): List<UserAvaliacao> {
        return try {
            val snapshot = db.collection("userAval")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.toObjects(UserAvaliacao::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun atualizarFotoPerfil(userId: String, imageUri: Uri): String {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("profile_images/$filename.jpg")
            ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            // 3. Atualiza o campo no Firestore
            val data = mapOf("fotoUrl" to downloadUrl)

            db.collection("users").document(userId)
                .set(data, SetOptions.merge())
                .await()
            
            downloadUrl

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Falha ao atualizar foto de perfil: ${e.message}")
        }
    }

    suspend fun atualizarChavePix(userId: String, novaChave: String) {
        try {
            val data = mapOf("chavePix" to novaChave)
            db.collection("users").document(userId)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun salvarUsuarioInicial(user: User) {
        try {
            db.collection("users").document(user.uid)
                .set(user, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}