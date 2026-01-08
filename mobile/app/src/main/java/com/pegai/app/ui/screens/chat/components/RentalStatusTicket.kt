package com.pegai.app.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pegai.app.model.RentalStatus

@Composable
fun RentalStatusTicket(
    status: RentalStatus,
    isOwner: Boolean,
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
    onOwnerConfirmReturn: () -> Unit = {}
) {
    val azulTema = Color(0xFF0A5C8A)
    val verdeSucesso = Color(0xFF2E7D32)
    val laranjaAcao = Color(0xFFF57C00)

    val (icon, ticketColor) = when (status) {
        RentalStatus.PENDING -> Pair(Icons.Default.Schedule, Color(0xFFFFF3E0))
        RentalStatus.COMPLETED -> Pair(Icons.Default.CheckCircle, Color(0xFFE8F5E9))
        RentalStatus.CANCELLED, RentalStatus.DECLINED -> Pair(Icons.Default.Cancel, Color(0xFFFFEBEE))
        else -> Pair(Icons.Default.Info, Color(0xFFE1F5FE))
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Header ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(ticketColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color.Black.copy(alpha = 0.6f))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = getStatusTitle(status),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = getStatusDescription(status, isOwner),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }

            // --- Details (Dates and Price) ---
            if (startDate.isNotEmpty() && status.step >= 3 && status.step < 8) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Período", fontSize = 11.sp, color = Color.Gray)
                        Text("$startDate até $endDate", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor Total", fontSize = 11.sp, color = Color.Gray)
                        Text("R$ ${String.format("%.2f", totalValue)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = azulTema)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Action Buttons ---
            when {
                status == RentalStatus.PENDING && isOwner -> {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onOwnerRejectRequest,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                        ) { Text("Recusar") }
                        Button(
                            onClick = onOwnerAcceptRequest,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(azulTema)
                        ) { Text("Aceitar") }
                    }
                }

                status == RentalStatus.APPROVED_WAITING_DATES && isOwner -> {
                    Button(
                        onClick = onOwnerSetDates,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(azulTema)
                    ) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selecionar Datas")
                    }
                }

                status == RentalStatus.DATES_PROPOSED && !isOwner -> {
                    Button(
                        onClick = onRenterAcceptDates,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(verdeSucesso)
                    ) { Text("Aceitar Datas e Valor") }
                }

                status == RentalStatus.AWAITING_DELIVERY && isOwner -> {
                    Button(
                        onClick = onOwnerConfirmDelivery,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(azulTema)
                    ) { Text("Confirmo que Entreguei") }
                }

                status == RentalStatus.DELIVERY_CONFIRMED && !isOwner -> {
                    Button(
                        onClick = onRenterConfirmReceipt,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(verdeSucesso)
                    ) { Text("Confirmo que Recebi") }
                }

                status == RentalStatus.ONGOING && !isOwner -> {
                    Button(
                        onClick = onRenterSignalReturn,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(laranjaAcao)
                    ) { Text("Informar Devolução") }
                }

                status == RentalStatus.RETURN_SIGNALED && isOwner -> {
                    Button(
                        onClick = onOwnerConfirmReturn,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(verdeSucesso)
                    ) { Text("Confirmar Devolução e Finalizar") }
                }
            }
        }
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
        RentalStatus.PENDING ->
            if (isOwner) "O locatário deseja alugar este item. Verifique o perfil e decida."
            else "Aguardando o dono aceitar sua solicitação."

        RentalStatus.APPROVED_WAITING_DATES ->
            if (isOwner) "Agora, defina as datas de entrega e devolução no botão abaixo."
            else "O dono aceitou! Aguarde ele definir as datas e o valor final."

        RentalStatus.DATES_PROPOSED ->
            if (isOwner) "Aguardando o locatário aceitar as datas e o valor."
            else "O dono propôs estas datas. Confira se está de acordo."

        RentalStatus.AWAITING_DELIVERY ->
            if (isOwner) "Combine o local de entrega. Após entregar, clique abaixo."
            else "Combine o local de encontro no chat para pegar o produto."

        RentalStatus.DELIVERY_CONFIRMED ->
            if (isOwner) "Aguardando o locatário confirmar que está com o produto."
            else "O dono informou a entrega. Confirme abaixo se já está com o item."

        RentalStatus.ONGOING ->
            if (isOwner) "Produto alugado. Aguarde a data de devolução."
            else "Aproveite! Quando for devolver, clique no botão abaixo."

        RentalStatus.RETURN_SIGNALED ->
            if (isOwner) "O locatário informou a devolução. Após receber o item, confirme abaixo."
            else "Aguardando o dono confirmar o recebimento do item."

        RentalStatus.COMPLETED -> "Tudo certo! O ciclo de aluguel foi concluído."
        RentalStatus.CANCELLED -> "Este processo foi cancelado."
        RentalStatus.DECLINED -> "Esta solicitação foi recusada."
    }
}