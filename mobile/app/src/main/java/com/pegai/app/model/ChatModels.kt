package com.pegai.app.model

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
    val contract: RentalContract = RentalContract()
)

data class ChatMessage(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)