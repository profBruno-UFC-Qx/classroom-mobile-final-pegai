package com.pegai.app.ui.viewmodel.chat

import com.pegai.app.model.ChatMessage
import com.pegai.app.model.ChatRoom

data class ChatUiState(
    val isLoading: Boolean = true,
    val chatRoom: ChatRoom? = null,
    val messages: List<ChatMessage> = emptyList(),
    val currentUserId: String = "",
    val otherUserName: String = "",
    val otherUserPhoto: String = "",
    val otherUserId: String = "",
    val diasCalculados: Int = 0,
    val totalCalculado: Double = 0.0
)