package com.pegai.app.model

import com.google.firebase.firestore.PropertyName

data class RentalContract(
    val startDate: String = "",
    val endDate: String = "",
    val price: Double = 0.0,
    val totalPrice: Double = 0.0
)

data class ChatRoom(
    var id: String = "",
    val ownerId: String = "",
    val renterId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val status: String = "PENDING",
    val lastMessage: String = "",
    val updatedAt: Long = 0,
    val contract: RentalContract = RentalContract(),
    val unreadCounts: Map<String, Int> = emptyMap(),

    @get:PropertyName("isProductReviewed")
    val isProductReviewed: Boolean = false,

    @get:PropertyName("isOwnerReviewed")
    val isOwnerReviewed: Boolean = false,

    @get:PropertyName("isRenterReviewed")
    val isRenterReviewed: Boolean = false
)

data class ChatMessage(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)