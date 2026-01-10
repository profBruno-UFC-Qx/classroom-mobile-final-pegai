package com.pegai.app.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

enum class RentalStatus(
    val label: String,
    val color: Color,
    val isChatUnlocked: Boolean,
    val step: Int
) {
    PENDING("Solicitação Enviada", Color(0xFFFFA000), false, 1),
    APPROVED_WAITING_DATES("Definir Datas", Color(0xFF0288D1), true, 2),
    DATES_PROPOSED("Confirmar Agendamento", Color(0xFF0288D1), true, 3),
    AWAITING_DELIVERY("Aguardando Entrega", Color(0xFF7B1FA2), true, 4),
    DELIVERY_CONFIRMED("Confirmar Recebimento", Color(0xFF7B1FA2), true, 5),
    ONGOING("Em Uso (Alugado)", Color(0xFF2E7D32), true, 6),
    RETURN_SIGNALED("Confirmar Devolução", Color(0xFFF57C00), true, 7),
    COMPLETED("Finalizado", Color(0xFF757575), false, 8),
    CANCELLED("Cancelado", Color(0xFFD32F2F), false, 0),
    DECLINED("Recusado", Color(0xFFD32F2F), false, 0)
}

data class Rental(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val productPrice: Double = 0.0,
    val chatId: String = "",
    val locadorId: String = "",
    val locadorNome: String = "",
    val locadorFoto: String = "",
    val locatarioId: String = "",
    val locatarioNome: String = "",
    val locatarioFoto: String = "",
    val status: RentalStatus = RentalStatus.PENDING,
    val dataInicio: Timestamp = Timestamp.now(),
    val dataFim: Timestamp = Timestamp.now(),
    val dataCriacao: Timestamp = Timestamp.now()
)