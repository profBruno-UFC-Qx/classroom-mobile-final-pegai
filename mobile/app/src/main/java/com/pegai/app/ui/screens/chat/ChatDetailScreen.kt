package com.pegai.app.ui.screens.chat

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.R
import com.pegai.app.model.ChatMessage
import com.pegai.app.model.RentalStatus
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.screens.chat.components.RentalDatePickerSheet
import com.pegai.app.ui.screens.chat.components.RentalStatusTicket
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.chat.ChatViewModel

@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String?,
    authViewModel: AuthViewModel,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.usuarioLogado.collectAsState()
    val context = LocalContext.current

    // --- CONFIGURAÇÃO DE SONS ---
    val sfxSend = remember { MediaPlayer.create(context, R.raw.send_message) }
    val sfxReceive = remember { MediaPlayer.create(context, R.raw.receive_message) }

    DisposableEffect(Unit) {
        onDispose {
            sfxSend?.release()
            sfxReceive?.release()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.playSoundEvent.collect { shouldPlay ->
            if (shouldPlay) {
                if (sfxReceive?.isPlaying == true) {
                    sfxReceive.seekTo(0)
                }
                sfxReceive?.start()
            }
        }
    }

    LaunchedEffect(chatId, currentUser) {
        if (chatId != null && currentUser != null) {
            viewModel.inicializarChat(chatId, currentUser!!.uid)
        }
    }

    var messageText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showProductReview by remember { mutableStateOf(false) }
    var showOwnerReview by remember { mutableStateOf(false) }
    var showRenterReview by remember { mutableStateOf(false) }

    val currentBrandGradient = brandGradient()
    val azulTema = MaterialTheme.colorScheme.primary
    val chatRoom = uiState.chatRoom

    val statusEnum = try {
        if (chatRoom != null) RentalStatus.valueOf(chatRoom.status) else RentalStatus.PENDING
    } catch (e: Exception) { RentalStatus.PENDING }

    val isOwner = chatRoom?.ownerId == uiState.currentUserId
    val productName = chatRoom?.productName ?: "Carregando..."
    val userName = uiState.otherUserName.ifEmpty { "Carregando..." }
    val userPhoto = uiState.otherUserPhoto

    Box(modifier = Modifier.fillMaxSize().background(currentBrandGradient)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Header ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    Column {
                        ChatHeaderInternal(
                            navController = navController,
                            userName = userName,
                            productName = productName,
                            userPhoto = userPhoto,
                            gradient = currentBrandGradient,
                            onProfileClick = {
                                if (uiState.otherUserId.isNotEmpty()) {
                                    navController.navigate(Screen.PublicProfile.createRoute(uiState.otherUserId))
                                }
                            }
                        )

                        RentalStatusTicket(
                            status = statusEnum,
                            isOwner = isOwner,
                            isProductReviewed = chatRoom?.isProductReviewed ?: false,
                            isOwnerReviewed = chatRoom?.isOwnerReviewed ?: false,
                            isRenterReviewed = chatRoom?.isRenterReviewed ?: false,
                            startDate = chatRoom?.contract?.startDate ?: "",
                            endDate = chatRoom?.contract?.endDate ?: "",
                            totalValue = chatRoom?.contract?.totalPrice ?: 0.0,
                            onOwnerAcceptRequest = { viewModel.aceitarSolicitacao() },
                            onOwnerRejectRequest = { viewModel.recusarSolicitacao() },
                            onOwnerSetDates = { showDatePicker = true },
                            onRenterAcceptDates = { viewModel.aceitarDatas() },
                            onOwnerConfirmDelivery = { viewModel.confirmarEntregaDono() },
                            onRenterConfirmReceipt = { viewModel.confirmarRecebimentoLocatario() },
                            onRenterSignalReturn = { viewModel.sinalizarDevolucao() },
                            onOwnerConfirmReturn = { viewModel.confirmarDevolucaoFinal() },
                            onRateProduct = { showProductReview = true },
                            onRateOwner = { showOwnerReview = true },
                            onRateRenter = { showRenterReview = true }
                        )
                    }
                }
            }

            // --- Chat Content ---
            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.ime,
                bottomBar = {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        if (statusEnum.isChatUnlocked) {
                            MessageInputBarRelative(
                                text = messageText,
                                onTextChange = { messageText = it },
                                onSend = {
                                    if (messageText.isNotBlank()) {
                                        if (sfxSend?.isPlaying == true) {
                                            sfxSend.seekTo(0)
                                        }
                                        sfxSend?.start()

                                        viewModel.enviarMensagem(messageText)
                                        messageText = ""
                                    }
                                },
                                azulTema = azulTema
                            )
                        } else {
                            ChatLockedBar()
                        }
                    }
                }
            ) { paddingValues ->
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = azulTema)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                        reverseLayout = true,
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.messages.reversed()) { msg ->
                            val isMe = msg.senderId == uiState.currentUserId
                            MessageBubbleRelative(msg, isMe, azulTema)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // --- MODAIS EXISTENTES ---
        if (showDatePicker) {
            RentalDatePickerSheet(
                dailyPrice = chatRoom?.contract?.price ?: 0.0,
                diasCalculados = uiState.diasCalculados,
                totalCalculado = uiState.totalCalculado,
                onDismiss = { showDatePicker = false },
                onDateSelectionChanged = { start, end ->
                    viewModel.simularValoresAluguel(start, end, chatRoom?.contract?.price ?: 0.0)
                },
                onConfirm = { start, end, _ ->
                    viewModel.definirDatas(start, end)
                    showDatePicker = false
                }
            )
        }

        // --- MODAIS DE AVALIAÇÃO ---
        if (showProductReview) {
            ReviewBottomSheet(
                title = "Avaliar Produto",
                subtitle = "O que você achou do item alugado?",
                onDismiss = { showProductReview = false },
                onConfirm = { rating, comment ->
                    viewModel.enviarAvaliacaoProduto(rating, comment)
                    showProductReview = false
                }
            )
        }
        if (showOwnerReview) {
            ReviewBottomSheet(
                title = "Avaliar Proprietário",
                subtitle = "Como foi a negociação com $userName?",
                onDismiss = { showOwnerReview = false },
                onConfirm = { rating, comment ->
                    viewModel.enviarAvaliacaoDono(rating, comment)
                    showOwnerReview = false
                }
            )
        }
        if (showRenterReview) {
            ReviewBottomSheet(
                title = "Avaliar Locatário",
                subtitle = "Como foi a experiência com $userName?",
                onDismiss = { showRenterReview = false },
                onConfirm = { rating, comment ->
                    viewModel.enviarAvaliacaoLocatario(rating, comment)
                    showRenterReview = false
                }
            )
        }
    }
}

@Composable
fun ChatHeaderInternal(
    navController: NavController,
    userName: String,
    userPhoto: String,
    productName: String,
    gradient: Brush,
    onProfileClick: () -> Unit
) {
    val mainColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(44.dp).border(2.dp, gradient, CircleShape).padding(2.dp).clip(CircleShape).clickable { onProfileClick() }) {
            if (userPhoto.isNotEmpty()) {
                AsyncImage(model = userPhoto, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Text(userName.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable { onProfileClick() }, maxLines = 1)
            Text(text = productName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1)
        }
        Spacer(modifier = Modifier.width(8.dp))

        // --- Botão Ver Perfil  ---
        Surface(
            onClick = onProfileClick,
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, gradient)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, null, tint = mainColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Ver Perfil", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = mainColor)
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun MessageBubbleRelative(msg: ChatMessage, isMe: Boolean, azulTema: Color) {
    val bubbleShape = if (isMe)
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    else
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMe) azulTema else MaterialTheme.colorScheme.surfaceVariant,
            shape = bubbleShape,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = msg.text,
                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun MessageInputBarRelative(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit, azulTema: Color) {
    Row(modifier = Modifier.navigationBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = text, onValueChange = onTextChange, placeholder = { Text("Mensagem...") },
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = getFieldColor(),
                unfocusedContainerColor = getFieldColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(azulTema).clickable { onSend() }, contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp), tint = Color.White)
        }
    }
}

@Composable
fun ChatLockedBar() {
    Row(modifier = Modifier.navigationBarsPadding().padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Chat bloqueado nesta etapa.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 13.sp)
    }
}

// --- ReviewBottomSheet ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewBottomSheet(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val primaryColor = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Estrelas Interativas
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Estrela $i",
                        tint = if (i <= rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { rating = i }
                            .padding(4.dp)
                    )
                }
            }

            Text(
                text = if (rating > 0) "$rating/5" else "Toque para avaliar",
                style = MaterialTheme.typography.labelMedium,
                color = if (rating > 0) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de Comentário
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Deixe um comentário (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Confirmar
            Button(
                onClick = { onConfirm(rating, comment) },
                enabled = rating > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text("Enviar Avaliação", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}