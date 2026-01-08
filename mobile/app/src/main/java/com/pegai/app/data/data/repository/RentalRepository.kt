package com.pegai.app.data.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.ChatRoom
import com.pegai.app.model.Rental
import com.pegai.app.model.RentalStatus
import kotlinx.coroutines.tasks.await
import java.util.Date

object RentalRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getRentalsForUser(userId: String): List<Rental> {
        return try {
            val ownerQuery = db.collection("chats")
                .whereEqualTo("ownerId", userId)
                .get().await()

            val renterQuery = db.collection("chats")
                .whereEqualTo("renterId", userId)
                .get().await()

            val ownerChats = ownerQuery.toObjects(ChatRoom::class.java)
            val renterChats = renterQuery.toObjects(ChatRoom::class.java)

            val listaFinal = mutableListOf<Rental>()

            ownerChats.forEach { chat ->
                val nomeLocatario = UserRepository.getNomeUsuario(chat.renterId)
                listaFinal.add(converterChatParaRental(chat, userId, "Você", nomeLocatario))
            }

            renterChats.forEach { chat ->
                val nomeDono = UserRepository.getNomeUsuario(chat.ownerId)
                listaFinal.add(converterChatParaRental(chat, userId, nomeDono, "Você"))
            }

            listaFinal.sortedByDescending { it.dataCriacao }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun converterChatParaRental(
        chat: ChatRoom,
        currentUserId: String,
        nomeDonoDisplay: String,
        nomeLocatarioDisplay: String
    ): Rental {

        val statusEnum = try {
            RentalStatus.valueOf(chat.status)
        } catch (e: Exception) {
            RentalStatus.PENDING
        }

        val dataRef = Timestamp(Date(chat.updatedAt))

        return Rental(
            id = chat.id,
            productId = chat.productId,
            productName = chat.productName,
            productImageUrl = chat.productImage,
            productPrice = chat.contract.totalPrice,
            locadorId = chat.ownerId,
            locadorNome = nomeDonoDisplay,
            locatarioId = chat.renterId,
            locatarioNome = nomeLocatarioDisplay,
            status = statusEnum,
            dataInicio = dataRef,
            dataFim = dataRef,
            dataCriacao = dataRef
        )
    }
}