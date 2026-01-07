package com.pegai.app.ui.screens.chat.components

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
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onManageDates: () -> Unit = {},
    onConfirmProposal: () -> Unit = {}
) {
    val azulTema = Color(0xFF0A5C8A)

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = azulTema)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = getStatusTitle(status), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    if (startDate.isNotEmpty()) {
                        Text("Período: $startDate a $endDate", fontSize = 12.sp)
                        Text("Valor: R$ ${String.format("%.2f", totalValue)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        val desc = if (isOwner) "Aguardando sua decisão." else "Aguardando o dono."
                        Text(text = desc, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isOwner) {
                when (status) {
                    RentalStatus.PENDING -> {
                        Row(Modifier.fillMaxWidth(), Arrangement.End) {
                            TextButton(onClick = onReject) { Text("Recusar", color = Color.Red) }
                            Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(azulTema)) { Text("Aceitar") }
                        }
                    }
                    RentalStatus.APPROVED -> {
                        Button(onClick = onManageDates, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(azulTema)) {
                            Text("Definir Datas e Preço")
                        }
                    }
                    else -> {}
                }
            } else {
                if (status == RentalStatus.APPROVED && startDate.isNotEmpty()) {
                    Button(onClick = onConfirmProposal, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFF2E7D32))) {
                        Text("Confirmar Aluguel e Pagar")
                    }
                }
            }
        }
    }
}

private fun getStatusTitle(status: RentalStatus): String = when (status) {
    RentalStatus.PENDING -> "Interesse no Item"
    RentalStatus.APPROVED -> "Em Negociação"
    RentalStatus.PAID -> "Aluguel Confirmado"
    RentalStatus.DECLINED -> "Recusado"
    else -> "Status: ${status.name}"
}