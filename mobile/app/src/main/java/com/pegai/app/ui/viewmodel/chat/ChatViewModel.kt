package com.pegai.app.ui.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.model.ChatMessage
import com.pegai.app.model.RentalStatus
import com.pegai.app.model.Review
import com.pegai.app.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _playSoundEvent = MutableSharedFlow<Boolean>()
    val playSoundEvent: SharedFlow<Boolean> = _playSoundEvent.asSharedFlow()

    private var currentChatId: String? = null
    private var jobsEscuta: Job? = null

    private var isFirstLoad = true

    fun inicializarChat(chatId: String, userId: String) {
        if (currentChatId == chatId && _uiState.value.currentUserId == userId) return

        currentChatId = chatId
        jobsEscuta?.cancel()
        isFirstLoad = true

        viewModelScope.launch {
            repository.markChatAsRead(chatId, userId)
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                currentUserId = userId,
                messages = emptyList(),
                chatRoom = null,
                otherUserName = "",
                otherUserId = ""
            )
        }

        jobsEscuta = viewModelScope.launch {
            launch {
                repository.getChatRoom(chatId).collect { chatRoom ->
                    var nomeOutro = "Usuário"
                    var fotoOutro = ""
                    var idOutro = ""

                    if (chatRoom != null) {
                        val otherId = if (chatRoom.ownerId == userId) chatRoom.renterId else chatRoom.ownerId
                        idOutro = otherId

                        val dados = repository.getUserData(otherId)
                        nomeOutro = dados.first
                        fotoOutro = dados.second
                    }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            chatRoom = chatRoom,
                            otherUserName = nomeOutro,
                            otherUserPhoto = fotoOutro,
                            otherUserId = idOutro
                        )
                    }
                }
            }
            launch {
                repository.getMessages(chatId).collect { msgs ->
                    val currentMessages = _uiState.value.messages

                    if (!isFirstLoad && msgs.size > currentMessages.size) {
                        val lastMsg = msgs.lastOrNull()
                        if (lastMsg != null && lastMsg.senderId != userId) {
                            _playSoundEvent.emit(true)
                            repository.markChatAsRead(chatId, userId)
                        }
                    }

                    _uiState.update { state -> state.copy(messages = msgs) }

                    if (msgs.isNotEmpty()) {
                        isFirstLoad = false
                    }
                }
            }
        }
    }

    fun enviarMensagem(texto: String) {
        val chatId = currentChatId ?: return
        val user = _uiState.value.currentUserId
        val receiverId = _uiState.value.otherUserId.takeIf { it.isNotEmpty() }
            ?: _uiState.value.chatRoom?.let {
                if(it.ownerId == user) it.renterId else it.ownerId
            }

        val novaMensagem = ChatMessage(
            text = texto,
            senderId = user,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.sendMessage(chatId, novaMensagem)
            repository.updateLastMessage(chatId, texto, receiverId)
        }
    }

    // --- RENTAL FLOW ---
    fun aceitarSolicitacao() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.APPROVED_WAITING_DATES.name)
                enviarMensagem("Solicitação aceita! Vou definir as datas agora.")
            }
        }
    }

    fun recusarSolicitacao() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.DECLINED.name)
                enviarMensagem("Infelizmente não poderei aceitar a solicitação agora.")
            }
        }
    }

    fun definirDatas(start: String, end: String) {
        val chatId = currentChatId ?: return
        val totalParaSalvar = _uiState.value.totalCalculado // Pega direto do estado do ViewModel

        if (totalParaSalvar <= 0.0) {
            android.util.Log.e("ChatViewModel", "Tentativa de salvar preço zerado!")
        }

        viewModelScope.launch {
            try {
                repository.updateContract(chatId, start, end, totalParaSalvar)
                repository.updateStatus(chatId, RentalStatus.DATES_PROPOSED.name)
                enviarMensagem("Proposta: $start até $end. Total: R$ ${String.format("%.2f", totalParaSalvar)}")
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Erro ao atualizar contrato", e)
            }
        }
    }

    fun aceitarDatas() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.AWAITING_DELIVERY.name)
                enviarMensagem("Datas e valor combinados! Aguardando entrega.")
            }
        }
    }

    fun confirmarEntregaDono() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.DELIVERY_CONFIRMED.name)
                enviarMensagem("O produto foi entregue. Aguardando confirmação de recebimento.")
            }
        }
    }

    fun confirmarRecebimentoLocatario() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.ONGOING.name)
                enviarMensagem("Produto recebido! O aluguel começou.")
            }
        }
    }

    fun sinalizarDevolucao() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.RETURN_SIGNALED.name)
                enviarMensagem("Vou devolver o produto. Vamos combinar o encontro.")
            }
        }
    }

    fun confirmarDevolucaoFinal() {
        currentChatId?.let { id ->
            viewModelScope.launch {
                repository.updateStatus(id, RentalStatus.COMPLETED.name)
                enviarMensagem("Produto recebido de volta. Aluguel finalizado com sucesso!")
            }
        }
    }

    // --- REVIEWS ---

    fun enviarAvaliacaoProduto(rating: Int, comment: String) {
        val currentUserId = _uiState.value.currentUserId
        val productId = _uiState.value.chatRoom?.productId ?: return
        val chatId = currentChatId ?: return

        viewModelScope.launch {
            val autorData = repository.getUserData(currentUserId)

            val review = Review(
                autorId = currentUserId,
                autorNome = autorData.first,
                autorFoto = autorData.second,
                nota = rating,
                comentario = comment,
                data = System.currentTimeMillis()
            )
            repository.saveProductReview(productId, chatId, review)
            enviarMensagem("⭐ Avaliei o produto com nota $rating!")
        }
    }

    fun enviarAvaliacaoDono(rating: Int, comment: String) {
        val currentUserId = _uiState.value.currentUserId
        val ownerId = _uiState.value.chatRoom?.ownerId ?: return
        val chatId = currentChatId ?: return

        viewModelScope.launch {
            val autorData = repository.getUserData(currentUserId)

            val review = Review(
                autorId = currentUserId,
                autorNome = autorData.first,
                autorFoto = autorData.second,
                nota = rating,
                comentario = comment,
                papel = "LOCADOR",
                data = System.currentTimeMillis()
            )

            repository.saveUserReview(ownerId, chatId, review)
            enviarMensagem("⭐ Avaliei o proprietário com nota $rating!")
        }
    }

    fun enviarAvaliacaoLocatario(rating: Int, comment: String) {
        val currentUserId = _uiState.value.currentUserId
        val renterId = _uiState.value.chatRoom?.renterId ?: return
        val chatId = currentChatId ?: return

        viewModelScope.launch {
            val autorData = repository.getUserData(currentUserId)

            val review = Review(
                autorId = currentUserId,
                autorNome = autorData.first,
                autorFoto = autorData.second,
                nota = rating,
                comentario = comment,
                papel = "LOCATARIO",
                data = System.currentTimeMillis()
            )

            repository.saveUserReview(renterId, chatId, review)
            enviarMensagem("⭐ Avaliei o locatário com nota $rating!")
        }
    }

    // --- CALCULATIONS ---
    fun simularValoresAluguel(startMillis: Long?, endMillis: Long?, pricePerDay: Double) {
        if (startMillis != null && endMillis != null) {
            val diff = endMillis - startMillis
            val dias = (TimeUnit.MILLISECONDS.toDays(diff) + 1).coerceAtLeast(1).toInt()
            val total = dias * pricePerDay

            _uiState.update { it.copy(diasCalculados = dias, totalCalculado = total) }
        } else {
            _uiState.update { it.copy(diasCalculados = 0, totalCalculado = 0.0) }
        }
    }
}