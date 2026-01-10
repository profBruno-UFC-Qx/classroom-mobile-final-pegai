package com.pegai.app.ui.screens.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pegai.app.model.RentalStatus

@Composable
fun RentalStatusTicket(
    status: RentalStatus,
    isOwner: Boolean,

    isProductReviewed: Boolean = false,
    isOwnerReviewed: Boolean = false,
    isRenterReviewed: Boolean = false,

    startDate: String = "",
    endDate: String = "",
    totalValue: Double = 0.0,
    onOwnerAcceptRequest: () -> Unit = {},
    onOwnerRejectRequest: () -> Unit = {},
    onOwnerSetDates: () -> Unit = {},
    onRenterAcceptDates: () -> Unit = {},
    onOwnerConfirmDelivery: () -> Unit = {},
    onRenterConfirmReceipt: () -> Unit = {},
    onRenterSignalReturn: () -> Unit = {},
    onOwnerConfirmReturn: () -> Unit = {},
    onRateProduct: () -> Unit = {},
    onRateOwner: () -> Unit = {},
    onRateRenter: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val successColor = Color(0xFF2E7D32)
    val actionColor = Color(0xFFF57C00)
    val reviewColor = Color(0xFFFFB300)
    val completedReviewColor = Color(0xFF4CAF50)

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val (icon, iconBgColor) = when (status) {
        RentalStatus.PENDING -> Pair(Icons.Default.Schedule, MaterialTheme.colorScheme.surfaceVariant)
        RentalStatus.COMPLETED -> Pair(Icons.Default.CheckCircle, successColor.copy(alpha = 0.1f))
        RentalStatus.CANCELLED, RentalStatus.DECLINED -> Pair(Icons.Default.Cancel, errorColor.copy(alpha = 0.1f))
        else -> Pair(Icons.Default.Info, primaryColor.copy(alpha = 0.1f))
    }

    val tintColor = when(status) {
        RentalStatus.COMPLETED -> successColor
        RentalStatus.CANCELLED, RentalStatus.DECLINED -> errorColor
        else -> primaryColor
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Header ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconBgColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = tintColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(getStatusTitle(status), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(getStatusDescription(status, isOwner), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), lineHeight = 14.sp)
                }
            }

            // --- Details ---
            if (startDate.isNotEmpty() && status.step >= 3 && status.step < 8) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Período", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("$startDate até $endDate", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("R$ ${String.format("%.2f", totalValue)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Action Buttons ---
            when {
                status == RentalStatus.PENDING && isOwner -> {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onOwnerRejectRequest, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor), border = BorderStroke(1.dp, errorColor.copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp)) { Text("Recusar") }
                        Button(onClick = onOwnerAcceptRequest, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(primaryColor), shape = RoundedCornerShape(12.dp)) { Text("Aceitar") }
                    }
                }
                status == RentalStatus.APPROVED_WAITING_DATES && isOwner -> {
                    Button(onClick = onOwnerSetDates, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(primaryColor), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.DateRange, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Selecionar Datas") }
                }
                status == RentalStatus.DATES_PROPOSED && !isOwner -> {
                    Button(onClick = onRenterAcceptDates, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(successColor), shape = RoundedCornerShape(12.dp)) { Text("Aceitar Datas e Valor") }
                }
                status == RentalStatus.AWAITING_DELIVERY && isOwner -> {
                    Button(onClick = onOwnerConfirmDelivery, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(primaryColor), shape = RoundedCornerShape(12.dp)) { Text("Confirmo que Entreguei") }
                }
                status == RentalStatus.DELIVERY_CONFIRMED && !isOwner -> {
                    Button(onClick = onRenterConfirmReceipt, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(successColor), shape = RoundedCornerShape(12.dp)) { Text("Confirmo que Recebi") }
                }
                status == RentalStatus.ONGOING && !isOwner -> {
                    Button(onClick = onRenterSignalReturn, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(actionColor), shape = RoundedCornerShape(12.dp)) { Text("Informar Devolução") }
                }
                status == RentalStatus.RETURN_SIGNALED && isOwner -> {
                    Button(onClick = onOwnerConfirmReturn, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(successColor), shape = RoundedCornerShape(12.dp)) { Text("Confirmar Devolução e Finalizar") }
                }

                // --- BOTÕES DE AVALIAÇÃO  ---
                status == RentalStatus.COMPLETED -> {
                    Text("Avalie sua experiência:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 8.dp))

                    if (isOwner) {
                        // DONO AVALIA LOCATÁRIO
                        ReviewButtonStyled(
                            text = if (isRenterReviewed) "Locatário Avaliado" else "Avaliar Locatário",
                            isReviewed = isRenterReviewed,
                            onClick = onRateRenter,
                            reviewColor = reviewColor,
                            completedColor = completedReviewColor,
                            pulseScale = pulseScale
                        )
                    } else {
                        // LOCATÁRIO AVALIA
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Produto
                            ReviewButtonStyled(
                                text = if (isProductReviewed) "Produto Avaliado" else "Avaliar Produto",
                                isReviewed = isProductReviewed,
                                onClick = onRateProduct,
                                reviewColor = reviewColor,
                                completedColor = completedReviewColor,
                                pulseScale = pulseScale,
                                modifier = Modifier.weight(1f)
                            )

                            // Dono
                            ReviewButtonStyled(
                                text = if (isOwnerReviewed) "Dono Avaliado" else "Avaliar Dono",
                                isReviewed = isOwnerReviewed,
                                onClick = onRateOwner,
                                reviewColor = reviewColor,
                                completedColor = completedReviewColor,
                                pulseScale = pulseScale,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewButtonStyled(
    text: String,
    isReviewed: Boolean,
    onClick: () -> Unit,
    reviewColor: Color,
    completedColor: Color,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isReviewed,
        modifier = modifier
            .fillMaxWidth()
            .then(if (!isReviewed) Modifier.scale(pulseScale) else Modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isReviewed) completedColor else reviewColor,
            contentColor = Color.White,
            disabledContainerColor = completedColor,
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isReviewed) 0.dp else 4.dp)
    ) {
        if (isReviewed) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
        } else {
            Icon(Icons.Default.Star, null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
    }
}

private fun getStatusTitle(status: RentalStatus): String = when (status) {
    RentalStatus.PENDING -> "Solicitação de Aluguel"
    RentalStatus.APPROVED_WAITING_DATES -> "Solicitação Aceita"
    RentalStatus.DATES_PROPOSED -> "Proposta de Datas"
    RentalStatus.AWAITING_DELIVERY -> "Aguardando Entrega"
    RentalStatus.DELIVERY_CONFIRMED -> "Confirmação de Recebimento"
    RentalStatus.ONGOING -> "Em Andamento"
    RentalStatus.RETURN_SIGNALED -> "Devolução Iniciada"
    RentalStatus.COMPLETED -> "Aluguel Finalizado"
    RentalStatus.CANCELLED -> "Cancelado"
    RentalStatus.DECLINED -> "Recusado"
}

private fun getStatusDescription(status: RentalStatus, isOwner: Boolean): String {
    return when (status) {
        RentalStatus.PENDING -> if (isOwner) "O locatário deseja alugar este item. Verifique o perfil e decida." else "Aguardando o dono aceitar sua solicitação."
        RentalStatus.APPROVED_WAITING_DATES -> if (isOwner) "Agora, defina as datas de entrega e devolução no botão abaixo." else "O dono aceitou! Aguarde ele definir as datas e o valor final."
        RentalStatus.DATES_PROPOSED -> if (isOwner) "Aguardando o locatário aceitar as datas e o valor." else "O dono propôs estas datas. Confira se está de acordo."
        RentalStatus.AWAITING_DELIVERY -> if (isOwner) "Combine o local de entrega. Após entregar, clique abaixo." else "Combine o local de encontro no chat para pegar o produto."
        RentalStatus.DELIVERY_CONFIRMED -> if (isOwner) "Aguardando o locatário confirmar que está com o produto." else "O dono informou a entrega. Confirme abaixo se já está com o item."
        RentalStatus.ONGOING -> if (isOwner) "Produto alugado. Aguarde a data de devolução." else "Aproveite! Quando for devolver, clique no botão abaixo."
        RentalStatus.RETURN_SIGNALED -> if (isOwner) "O locatário informou a devolução. Após receber o item, confirme abaixo." else "Aguardando o dono confirmar o recebimento do item."
        RentalStatus.COMPLETED -> "Tudo certo! O ciclo de aluguel foi concluído."
        RentalStatus.CANCELLED -> "Este processo foi cancelado."
        RentalStatus.DECLINED -> "Esta solicitação foi recusada."
    }
}