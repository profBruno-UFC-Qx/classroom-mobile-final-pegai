package com.pegai.app.data.data.repository

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
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
=======
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val cache = mutableMapOf<String, String>()
    private val cacheUsuarios = mutableMapOf<String, User>()
    private val avaliacoesCache = mutableMapOf<String, List<UserAvaliacao>>()


    suspend fun getNomeUsuario(userId: String): String {
        return cache[userId] ?: run {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            val nome = doc.getString("nome") ?: "Usuário"
            cache[userId] = nome
            nome
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
        }
    }

    suspend fun getUsuarioPorId(userId: String): User? {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
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
=======
        // Cache primeiro
        cacheUsuarios[userId]?.let { return it }

        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .await()

        if (!doc.exists()) return null

        val usuario = doc.toObject(User::class.java)
        if (usuario != null) {
            cacheUsuarios[userId] = usuario
        }

        return usuario
    }

    suspend fun getAvaliacoesDoUsuario(userId: String): List<UserAvaliacao> {
        return avaliacoesCache[userId] ?: run {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("userAval")
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
                .whereEqualTo("userId", userId)
                .get()
                .await()

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
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
=======
            val avaliacoes = snapshot.toObjects(UserAvaliacao::class.java)
            avaliacoesCache[userId] = avaliacoes
            avaliacoes
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/data/data/repository/UserRepository.kt
        }
    }
}