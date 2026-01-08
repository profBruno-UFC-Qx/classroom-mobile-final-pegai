package com.pegai.app.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.repository.ChatRepository
import com.pegai.app.ui.screens.chat.ChatSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatListUiState(
    val isLoading: Boolean = true,
    val chatsAsOwner: List<ChatSummary> = emptyList(),
    val chatsAsRenter: List<ChatSummary> = emptyList()
)

class ChatListViewModel : ViewModel() {

    private val repository = ChatRepository()
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun carregarConversas(userId: String) {
        _uiState.update { it.copy(isLoading = true) }

        // Carregar conversas como Proprietário
        viewModelScope.launch {
            repository.getChatsAsOwner(userId).collect { rooms ->
                val summaries = rooms.map { room ->
                    async {
                        val (nome, foto) = repository.getUserData(room.renterId)

                        ChatSummary(
                            chatId = room.id,
                            otherUserName = nome,
                            otherUserPhoto = foto,
                            productName = room.productName,
                            lastMessage = room.lastMessage,
                            time = formatarData(room.updatedAt),
                            isMeOwner = true
                        )
                    }
                }.awaitAll()

                _uiState.update { it.copy(chatsAsOwner = summaries, isLoading = false) }
            }
        }

        // Carregar conversas como Locatário
        viewModelScope.launch {
            repository.getChatsAsRenter(userId).collect { rooms ->
                val summaries = rooms.map { room ->
                    async {
                        val (nome, foto) = repository.getUserData(room.ownerId)

                        ChatSummary(
                            chatId = room.id,
                            otherUserName = nome,
                            otherUserPhoto = foto,
                            productName = room.productName,
                            lastMessage = room.lastMessage,
                            time = formatarData(room.updatedAt),
                            isMeOwner = false
                        )
                    }
                }.awaitAll()

                _uiState.update { it.copy(chatsAsRenter = summaries, isLoading = false) }
            }
        }
    }

    private fun formatarData(timestamp: Long): String {
        if (timestamp == 0L) return ""
        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}