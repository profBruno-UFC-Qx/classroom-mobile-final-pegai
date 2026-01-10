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
import com.pegai.app.ui.theme.getFieldColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    //  BLOQUEIO DE DATAS PASSADAS
    val datePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Bloqueia datas anteriores a hoje (UTC)
                val calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                return utcTimeMillis >= calendar.timeInMillis
            }
        }
    )

    var showCalendarDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

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
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) }
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Definir Período", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Selecione as datas:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = if (startDateString.isNotEmpty()) "$startDateString  até  $endDateString" else "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Toque para abrir o calendário") },
                trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showCalendarDialog = true },
                shape = RoundedCornerShape(12.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = getFieldColor(),
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = getFieldColor(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cálculo Final", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(
                            text = if (diasCalculados > 0) "$diasCalculados diárias x R$ ${String.format("%.2f", dailyPrice)}" else "--",
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "R$ ${String.format("%.2f", totalCalculado)}",
                        fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { if (isButtonEnabled) onConfirm(startDateString, endDateString, totalCalculado) },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth().height(56.dp),
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
                    Text("Concluir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendarDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                title = {
                    Text("Período do Aluguel", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                },
                headline = {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = if (startMillis != null && endMillis != null)
                                "$startDateString - $endDateString"
                            else "Selecione as datas",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            )
        }
    }
}