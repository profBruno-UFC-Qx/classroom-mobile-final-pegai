package com.pegai.app.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp

enum class RentalStatus(
    val label: String,
    val color: Color,
    val isChatUnlocked: Boolean,
    val step: Int
) {
    // --- Phase 1: Negotiation ---
    PENDING(
        label = "Solicitação Enviada",
        color = Color(0xFFFFA000),
        isChatUnlocked = false,
        step = 1
    ),

    APPROVED_WAITING_DATES(
        label = "Definir Datas",
        color = Color(0xFF0288D1),
        isChatUnlocked = true,
        step = 2
    ),

    DATES_PROPOSED(
        label = "Confirmar Agendamento",
        color = Color(0xFF0288D1),
        isChatUnlocked = true,
        step = 3
    ),

    // --- Phase 2: Delivery ---
    AWAITING_DELIVERY(
        label = "Aguardando Entrega",
        color = Color(0xFF7B1FA2),
        isChatUnlocked = true,
        step = 4
    ),

    DELIVERY_CONFIRMED(
        label = "Confirmar Recebimento",
        color = Color(0xFF7B1FA2),
        isChatUnlocked = true,
        step = 5
    ),

    // --- Phase 3: Usage and Return ---
    ONGOING(
        label = "Em Uso (Alugado)",
        color = Color(0xFF2E7D32),
        isChatUnlocked = true,
        step = 6
    ),

    RETURN_SIGNALED(
        label = "Confirmar Devolução",
        color = Color(0xFFF57C00),
        isChatUnlocked = true,
        step = 7
    ),

    // --- Phase 4: Finalization ---
    COMPLETED(
        label = "Finalizado",
        color = Color(0xFF757575),
        isChatUnlocked = false,
        step = 8
    ),

    CANCELLED(
        label = "Cancelado",
        color = Color(0xFFD32F2F),
        isChatUnlocked = false,
        step = 0
    ),

    DECLINED(
        label = "Recusado",
        color = Color(0xFFD32F2F),
        isChatUnlocked = false,
        step = 0
    )
}

data class Rental(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val productPrice: Double = 0.0,
    val locadorId: String = "",
    val locadorNome: String = "",
    val locatarioId: String = "",
    val locatarioNome: String = "",
    val status: RentalStatus = RentalStatus.PENDING,
    val dataInicio: Timestamp = Timestamp.now(),
    val dataFim: Timestamp = Timestamp.now(),
    val dataCriacao: Timestamp = Timestamp.now()
)