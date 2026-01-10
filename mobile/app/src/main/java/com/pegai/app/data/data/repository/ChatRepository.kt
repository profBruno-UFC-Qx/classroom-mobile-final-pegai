package com.pegai.app.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.pegai.app.model.ChatMessage
import com.pegai.app.model.ChatRoom
import com.pegai.app.model.Product
import com.pegai.app.model.RentalContract
import com.pegai.app.model.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getChatRoom(chatId: String): Flow<ChatRoom?> = callbackFlow {
        val docRef = db.collection("chats").document(chatId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ChatRepo", "Erro ao ouvir chat", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val chat = snapshot.toObject<ChatRoom>()
                chat?.id = snapshot.id
                trySend(chat)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val collRef = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val listener = collRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val messages = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<ChatMessage>()
            } ?: emptyList()
            trySend(messages)
        }
        awaitClose { listener.remove() }
    }

    fun sendMessage(chatId: String, message: ChatMessage) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .add(message)
    }

    fun markChatAsRead(chatId: String, userId: String) {
        val updates = mapOf(
            "unreadCounts.$userId" to 0
        )
        db.collection("chats").document(chatId).update(updates)
            .addOnFailureListener { e -> Log.e("ChatRepo", "Erro ao marcar lido", e) }
    }

    fun updateLastMessage(chatId: String, lastMessage: String, receiverId: String?) {
        val updates = mutableMapOf<String, Any>(
            "lastMessage" to lastMessage,
            "updatedAt" to System.currentTimeMillis()
        )

        if (receiverId != null) {
            updates["unreadCounts.$receiverId"] = FieldValue.increment(1)
        }

        db.collection("chats").document(chatId).update(updates)
    }

    fun updateStatus(chatId: String, newStatus: String) {
        db.collection("chats").document(chatId).update("status", newStatus)
    }

    fun updateContract(chatId: String, startDate: String, endDate: String, totalPrice: Double) {
        val docRef = db.collection("chats").document(chatId)

        val updates = hashMapOf<String, Any>(
            "contract.startDate" to startDate,
            "contract.endDate" to endDate,
            "contract.totalPrice" to totalPrice
        )

        docRef.update(updates)
            .addOnSuccessListener { android.util.Log.d("ChatRepo", "Preço $totalPrice salvo com sucesso!") }
            .addOnFailureListener { e -> android.util.Log.e("ChatRepo", "Erro no Firestore", e) }
    }

    fun getChatsAsOwner(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val query = db.collection("chats").whereEqualTo("ownerId", userId)
        val listener = query.addSnapshotListener { snapshot, _ ->
            val chats = snapshot?.toObjects(ChatRoom::class.java) ?: emptyList()
            chats.forEachIndexed { index, chat ->
                chat.id = snapshot?.documents?.get(index)?.id ?: ""
            }
            trySend(chats)
        }
        awaitClose { listener.remove() }
    }

    fun getChatsAsRenter(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val query = db.collection("chats").whereEqualTo("renterId", userId)
        val listener = query.addSnapshotListener { snapshot, _ ->
            val chats = snapshot?.toObjects(ChatRoom::class.java) ?: emptyList()
            chats.forEachIndexed { index, chat ->
                chat.id = snapshot?.documents?.get(index)?.id ?: ""
            }
            trySend(chats)
        }
        awaitClose { listener.remove() }
    }

    suspend fun iniciarNegociacao(renterId: String, ownerId: String, product: Product): String {
        val query = db.collection("chats")
            .whereEqualTo("renterId", renterId)
            .whereEqualTo("ownerId", ownerId)
            .whereEqualTo("productId", product.pid)
            .get().await()

        if (!query.isEmpty) {
            return query.documents[0].id
        } else {
            val novoChat = ChatRoom(
                id = "",
                ownerId = ownerId,
                renterId = renterId,
                productId = product.pid,
                productName = product.titulo,
                productImage = product.imageUrl,
                status = "PENDING",
                lastMessage = "Tenho interesse no aluguel.",
                updatedAt = System.currentTimeMillis(),
                contract = RentalContract(
                    price = product.preco // AQUI: Agora o multiplicador não será 0
                ),
                unreadCounts = mapOf(ownerId to 1, renterId to 0)
            )
            val docRef = db.collection("chats").add(novoChat).await()
            val msgInicial = ChatMessage(
                text = "Olá! Tenho interesse no aluguel do item: ${product.titulo}",
                senderId = renterId,
                timestamp = System.currentTimeMillis()
            )
            docRef.collection("messages").add(msgInicial)
            return docRef.id
        }
    }

    suspend fun getUserData(userId: String): Pair<String, String> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            val nome = doc.getString("nome") ?: doc.getString("name") ?: "Usuário"
            val foto = doc.getString("fotoUrl") ?: doc.getString("foto") ?: ""
            Pair(nome, foto)
        } catch (e: Exception) {
            Pair("Usuário", "")
        }
    }

    suspend fun saveProductReview(productId: String, chatId: String, review: Review) {
        val productRef = db.collection("products").document(productId)
        val chatRef = db.collection("chats").document(chatId)
        val reviewRef = productRef.collection("reviews").document()
        val reviewMap = hashMapOf(
            "autorId" to review.autorId,
            "autorNome" to review.autorNome,
            "autorFoto" to review.autorFoto,
            "nota" to review.nota,
            "comentario" to review.comentario,
            "data" to review.data,
            "papel" to review.papel
        )

        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val currentNota = snapshot.getDouble("nota") ?: 0.0
                val currentTotal = snapshot.getLong("totalAvaliacoes") ?: 0L
                val newTotal = currentTotal + 1
                val newAverage = ((currentNota * currentTotal) + review.nota.toDouble()) / newTotal.toDouble()

                transaction.set(reviewRef, reviewMap)
                transaction.update(productRef, "nota", newAverage)
                transaction.update(productRef, "totalAvaliacoes", newTotal)
                transaction.update(chatRef, "isProductReviewed", true)
            }.await()
        } catch (e: Exception) {
            Log.e("ChatRepo", "Erro ao salvar avaliação de produto", e)
        }
    }

    suspend fun saveUserReview(targetUserId: String, chatId: String, review: Review) {
        val userRef = db.collection("users").document(targetUserId)
        val chatRef = db.collection("chats").document(chatId)
        val reviewRef = userRef.collection("avaliacoes").document()

        val reviewMap = hashMapOf(
            "autorId" to review.autorId,
            "autorNome" to review.autorNome,
            "autorFoto" to review.autorFoto,
            "nota" to review.nota,
            "comentario" to review.comentario,
            "data" to review.data,
            "papel" to review.papel
        )

        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val fieldToUpdateInChat = if (review.papel == "LOCADOR") {
                    val notaAtual = snapshot.getDouble("notaLocador") ?: 0.0
                    val totalAtual = snapshot.getLong("totalAvaliacoesLocador") ?: 0L
                    val newTotal = totalAtual + 1
                    val newAverage = ((notaAtual * totalAtual) + review.nota.toDouble()) / newTotal.toDouble()
                    transaction.update(userRef, "notaLocador", newAverage)
                    transaction.update(userRef, "totalAvaliacoesLocador", newTotal)
                    "isOwnerReviewed"
                } else {
                    val notaAtual = snapshot.getDouble("notaLocatario") ?: 0.0
                    val totalAtual = snapshot.getLong("totalAvaliacoesLocatario") ?: 0L
                    val newTotal = totalAtual + 1
                    val newAverage = ((notaAtual * totalAtual) + review.nota.toDouble()) / newTotal.toDouble()
                    transaction.update(userRef, "notaLocatario", newAverage)
                    transaction.update(userRef, "totalAvaliacoesLocatario", newTotal)
                    "isRenterReviewed"
                }
                transaction.set(reviewRef, reviewMap)
                transaction.update(chatRef, fieldToUpdateInChat, true)
            }.await()

        } catch (e: Exception) {
            Log.e("ChatRepo", "Erro ao salvar avaliação de usuário", e)
        }
    }

    suspend fun getUserReviews(userId: String): List<Review> {
        return try {
            val snapshot = db.collection("users").document(userId).collection("avaliacoes").orderBy("data", Query.Direction.DESCENDING).get().await()
            snapshot.toObjects(Review::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}