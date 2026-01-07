package com.pegai.app.ui.screens.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDatePickerSheet(
    dailyPrice: Double,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    // Estados para as datas
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // Simulação de cálculo de dias
    val days = 3
    val totalPrice = dailyPrice * days

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Período do Aluguel",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Inputs de Data
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Retirada") },
                    placeholder = { Text("DD/MM") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Devolução") },
                    placeholder = { Text("DD/MM") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resumo do Valor (Cálculo Automático)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF1F3F4),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Resumo do Valor", fontSize = 12.sp, color = Color.Gray)
                        Text("$days diárias x R$ ${String.format("%.2f", dailyPrice)}", fontSize = 14.sp)
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", totalPrice)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF0A5C8A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onConfirm(startDate, endDate, totalPrice) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C8A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Período", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}