package com.pegai.app.ui.screens.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalDatePickerSheet(
    dailyPrice: Double,
    diasCalculados: Int,
    totalCalculado: Double,
    onDismiss: () -> Unit,
    onDateSelectionChanged: (Long?, Long?) -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    val datePickerState = rememberDateRangePickerState()
    var showCalendarDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    val startMillis = datePickerState.selectedStartDateMillis
    val endMillis = datePickerState.selectedEndDateMillis

    LaunchedEffect(startMillis, endMillis) {
        onDateSelectionChanged(startMillis, endMillis)
    }

    val startDateString = if (startMillis != null) dateFormatter.format(Date(startMillis)) else ""
    val endDateString = if (endMillis != null) dateFormatter.format(Date(endMillis)) else ""
    val isButtonEnabled = startMillis != null && endMillis != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {

            // --- Header ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = Color(0xFF0A5C8A))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Definir Período", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Selecione as datas:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            OutlinedTextField(
                value = if (startDateString.isNotEmpty()) "$startDateString  até  $endDateString" else "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Toque para abrir o calendário") },
                trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showCalendarDialog = true },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Calculation Summary ---
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
                        Text("Cálculo Final", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = if (diasCalculados > 0) "$diasCalculados diárias x R$ ${String.format("%.2f", dailyPrice)}" else "--",
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", totalCalculado)}",
                        fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0A5C8A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isButtonEnabled) onConfirm(startDateString, endDateString, totalCalculado)
                },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A5C8A),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Proposta", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showCalendarDialog) {
        DatePickerDialog(
            onDismissRequest = { showCalendarDialog = false },
            confirmButton = {
                TextButton(onClick = { showCalendarDialog = false }) {
                    Text("Salvar", fontWeight = FontWeight.Bold, color = Color(0xFF0A5C8A))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendarDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                title = {
                    Text("Selecione o intervalo", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF0A5C8A),
                    todayDateBorderColor = Color(0xFF0A5C8A),
                    dayInSelectionRangeContainerColor = Color(0xFF0A5C8A).copy(alpha = 0.2f)
                )
            )
        }
    }
}