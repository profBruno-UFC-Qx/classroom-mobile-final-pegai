package com.pegai.app.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.pegai.app.model.ChatMessage
import com.pegai.app.model.ChatRoom
import com.pegai.app.model.Product
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
            .addOnFailureListener { e ->
                Log.e("ChatRepo", "Erro ao enviar mensagem", e)
            }
    }

    fun updateStatus(chatId: String, newStatus: String) {
        db.collection("chats").document(chatId)
            .update("status", newStatus)
            .addOnFailureListener { e ->
                Log.e("ChatRepo", "Erro ao atualizar status", e)
            }
    }

    fun updateContract(chatId: String, startDate: String, endDate: String, totalPrice: Double) {
        val updates = mapOf(
            "contract.startDate" to startDate,
            "contract.endDate" to endDate,
            "contract.totalPrice" to totalPrice
        )

        db.collection("chats").document(chatId)
            .update(updates)
            .addOnFailureListener { e ->
                Log.e("ChatRepo", "Erro ao atualizar contrato", e)
            }
    }

    fun getChatsAsOwner(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val query = db.collection("chats")
            .whereEqualTo("ownerId", userId)

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
        val query = db.collection("chats")
            .whereEqualTo("renterId", userId)

        val listener = query.addSnapshotListener { snapshot, _ ->
            val chats = snapshot?.toObjects(ChatRoom::class.java) ?: emptyList()
            chats.forEachIndexed { index, chat ->
                chat.id = snapshot?.documents?.get(index)?.id ?: ""
            }
            trySend(chats)
        }
        awaitClose { listener.remove() }
    }

    suspend fun iniciarNegociacao(
        renterId: String,
        ownerId: String,
        product: Product
    ): String {
        val query = db.collection("chats")
            .whereEqualTo("renterId", renterId)
            .whereEqualTo("ownerId", ownerId)
            .whereEqualTo("productId", product.pid)
            .get()
            .await()

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
                updatedAt = System.currentTimeMillis()
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

    fun updateLastMessage(chatId: String, lastMessage: String) {
        val updates = mapOf(
            "lastMessage" to lastMessage,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("chats").document(chatId)
            .update(updates)
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}