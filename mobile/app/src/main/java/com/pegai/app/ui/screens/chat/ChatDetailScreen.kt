package com.pegai.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.RentalStatus
import com.pegai.app.ui.screens.chat.components.RentalStatusTicket
import com.pegai.app.ui.screens.chat.components.RentalDatePickerSheet

data class ChatMessageLocal(val text: String, val isMe: Boolean)

@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String?
) {
    // 1. Definição se é Dono ou Cliente baseado no ID
    val isOwner = chatId == "chat_dono_123"
    val otherUserName = if (isOwner) "João Interessado" else "Edineide Silva (Dona)"

    // 2. Estados Locais (Tela Burra)
    var status by remember { mutableStateOf(RentalStatus.PENDING) }
    var messageText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedStart by remember { mutableStateOf("") }
    var selectedEnd by remember { mutableStateOf("") }
    var totalValue by remember { mutableDoubleStateOf(0.0) }

    val messages = remember {
        mutableStateListOf(ChatMessageLocal("Olá! Tenho interesse na calculadora.", false))
    }

    val brandGradient = Brush.horizontalGradient(listOf(Color(0xFF0A5C8A), Color(0xFF2ED1B2)))
    val azulTema = Color(0xFF0A5C8A)

    Box(modifier = Modifier.fillMaxSize().background(brandGradient)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Topo (Header + Ticket)
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8F9FA),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    Column {
                        ChatHeaderInternal(
                            navController = navController,
                            userName = otherUserName,
                            userPhoto = "https://media-for2-2.cdn.whatsapp.net/v/t61.24694-24/537374086_697212073422555_5417296598778872192_n.jpg",
                            productName = "Calculadora HP 12c",
                            gradient = brandGradient,
                            azulTema = azulTema
                        )

                        RentalStatusTicket(
                            status = status,
                            isOwner = isOwner,
                            startDate = selectedStart,
                            endDate = selectedEnd,
                            totalValue = totalValue,
                            onAccept = { status = RentalStatus.APPROVED },
                            onReject = { status = RentalStatus.DECLINED },
                            onManageDates = { showDatePicker = true },
                            onConfirmProposal = { status = RentalStatus.PAID }
                        )
                    }
                }
            }

            // Corpo do Chat (Mensagens)
            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFF8F9FA),
                contentWindowInsets = WindowInsets.ime,
                bottomBar = {
                    Surface(color = Color(0xFFF8F9FA)) {
                        if (status == RentalStatus.PENDING) {
                            ChatLockedBar()
                        } else {
                            MessageInputBarRelative(
                                text = messageText,
                                onTextChange = { messageText = it },
                                onSend = {
                                    if (messageText.isNotBlank()) {
                                        messages.add(ChatMessageLocal(messageText, true))
                                        messageText = ""
                                    }
                                },
                                azulTema = azulTema,
                                isOwner = isOwner
                            )
                        }
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messages.reversed()) { msg ->
                        MessageBubbleRelative(msg, azulTema, isOwner)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Modal de Datas
        if (showDatePicker && isOwner) {
            RentalDatePickerSheet(
                dailyPrice = 35.0,
                onDismiss = { showDatePicker = false },
                onConfirm = { start, end, total ->
                    selectedStart = start
                    selectedEnd = end
                    totalValue = total
                    showDatePicker = false
                }
            )
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ChatHeaderInternal(navController: NavController, userName: String, userPhoto: String, productName: String, gradient: Brush, azulTema: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF333333)) }
        Box(modifier = Modifier.size(44.dp).border(2.dp, gradient, CircleShape).padding(2.dp).clip(CircleShape)) {
            AsyncImage(model = userPhoto, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF333333))
            Text(text = "Sobre: $productName", fontSize = 11.sp, color = Color.Gray)
        }
    }
    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun MessageBubbleRelative(msg: ChatMessageLocal, azulTema: Color, isOwner: Boolean) {
    val bubbleShape = if (msg.isMe) RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp) else RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start) {
        Surface(
            color = if (msg.isMe) (if (isOwner) azulTema else Color.White) else Color.White,
            shape = bubbleShape,
            shadowElevation = 1.dp,
            modifier = if (msg.isMe && !isOwner) Modifier.border(1.5.dp, azulTema, bubbleShape) else Modifier
        ) {
            Text(text = msg.text, color = if (msg.isMe && isOwner) Color.White else (if (msg.isMe) azulTema else Color(0xFF333333)), modifier = Modifier.padding(12.dp), fontSize = 15.sp)
        }
    }
}

@Composable
fun MessageInputBarRelative(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit, azulTema: Color, isOwner: Boolean) {
    Row(modifier = Modifier.navigationBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(value = text, onValueChange = onTextChange, placeholder = { Text("Mensagem...") }, modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)), colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent))
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isOwner) azulTema else Color.White).then(if (!isOwner) Modifier.border(1.5.dp, azulTema, CircleShape) else Modifier).clickable { onSend() }, contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp), tint = if (isOwner) Color.White else azulTema)
        }
    }
}

@Composable
fun ChatLockedBar() {
    Row(modifier = Modifier.navigationBarsPadding().padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("O chat será liberado após o aceite.", color = Color.Gray, fontSize = 13.sp)
    }
}