package com.pegai.app.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp

enum class RentalStatus(val label: String, val color: Color, val isActive: Boolean) {
    PENDING("Solicitado", Color(0xFFFFA000), true),
    APPROVED("Em Negociação", Color(0xFF0288D1), true),
    PAID("Aguardando Entrega", Color(0xFF7B1FA2), true),
    ONGOING("Em Uso", Color(0xFF2E7D32), true),
    COMPLETED("Finalizado", Color(0xFF757575), false),
    CANCELLED("Cancelado", Color(0xFFD32F2F), false),
    DECLINED("Recusado", Color(0xFFD32F2F), false)
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