package com.pegai.app.data.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao
import kotlinx.coroutines.tasks.await

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
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        storageRef.putFile(imageUri).await()

        val downloadUrl = storageRef.downloadUrl.await().toString()
        db.collection("users").document(userId)
            .update("fotoUrl", downloadUrl)
            .await()

        return downloadUrl
    }

    suspend fun atualizarChavePix(userId: String, novaChave: String) {
        try {
            db.collection("users").document(userId)
                .update("chavePix", novaChave)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}