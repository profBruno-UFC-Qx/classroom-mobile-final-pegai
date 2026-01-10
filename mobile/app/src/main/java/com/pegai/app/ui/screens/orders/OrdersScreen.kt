package com.pegai.app.ui.screens.orders

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.Rental
import com.pegai.app.model.RentalStatus
import com.pegai.app.model.User
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.orders.OrdersViewModel

@Composable
fun OrdersScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: OrdersViewModel = viewModel()
) {
    val authUser by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // --- VERIFICAÇÃO DE USUÁRIO LOGADO ---
    if (authUser == null) {
        GuestPlaceholder(
            title = "Acesse seus Pedidos",
            subtitle = "Faça login para acompanhar seus aluguéis em andamento e histórico.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val azulPrimario = MaterialTheme.colorScheme.primary

    val tabs = listOf(
        "Em Andamento" to Icons.Default.Sync,
        "Histórico" to Icons.Default.History
    )
    val currentBrandGradient = brandGradient()

    LaunchedEffect(authUser) {
        authUser?.let { viewModel.carregarAlugueis(it.uid) }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // --- Header ---
        Column(modifier = Modifier.fillMaxWidth().background(currentBrandGradient)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Meus Aluguéis", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = azulPrimario,
                    indicator = { tabPositions ->
                        Box(Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(3.dp).background(currentBrandGradient))
                    },
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    tabs.forEachIndexed { index, (title, icon) ->
                        val isSelected = selectedTab == index
                        val contentColor = if (isSelected) azulPrimario else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = contentColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = contentColor
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = azulPrimario) }
        } else {
            val lista = if (selectedTab == 0) uiState.activeRentals else uiState.inactiveRentals

            if (lista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum aluguel encontrado.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    items(lista) { rental ->
                        RentalTicketCard(
                            rental = rental,
                            currentUser = authUser,
                            onClick = {
                                navController.navigate(Screen.ChatDetail.createRoute(rental.id))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun RentalTicketCard(rental: Rental, currentUser: User?, onClick: () -> Unit) {
    val currentUserId = currentUser?.uid ?: ""
    val isLocador = rental.locadorId == currentUserId

    val nomeLocadorDisplay = if (isLocador) "Você" else rental.locadorNome.split(" ").firstOrNull() ?: "Dono"
    val nomeLocatarioDisplay = if (!isLocador) "Você" else rental.locatarioNome.split(" ").firstOrNull() ?: "Cliente"

    val currentBrandGradient = brandGradient()

    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), shadowElevation = 4.dp) {
            Box(modifier = Modifier.fillMaxWidth().background(currentBrandGradient).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    // --- LADO ESQUERDO: DONO (LOCADOR) ---
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "Dono", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TicketAvatar(
                                isMe = isLocador,
                                // Se eu sou o locador, usa minha foto. Se não, usa a foto que veio no Rental.
                                userPhoto = if (isLocador) currentUser?.fotoUrl else rental.locadorFoto,
                                userName = nomeLocadorDisplay,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = nomeLocadorDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 100.dp))
                        }
                    }

                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp))

                    // --- LADO DIREITO: CLIENTE (LOCATÁRIO) ---
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Locatário", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = nomeLocatarioDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 100.dp))
                            Spacer(Modifier.width(8.dp))
                            TicketAvatar(
                                isMe = !isLocador,
                                userPhoto = if (!isLocador) currentUser?.fotoUrl else rental.locatarioFoto,
                                userName = nomeLocatarioDisplay,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
            RentalCardBody(rental)
        }
    }
}

@Composable
fun RentalCardBody(rental: Rental) {
    val mainColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val successColor = Color(0xFF2E7D32)
    val warningColor = Color(0xFFF57C00)

    val (textoPreco, textoPrecoSub, textoStatus, icone, corStatus) = when (rental.status) {
        RentalStatus.COMPLETED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Total Pago", "Concluído", Icons.Default.CheckCircle, successColor)
        RentalStatus.CANCELLED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Cancelado", "Aluguel Cancelado", Icons.Default.Cancel, errorColor)
        RentalStatus.DECLINED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Recusado", "Solicitação Recusada", Icons.Default.Cancel, errorColor)
        RentalStatus.PENDING -> listOf("R$ --", "Aguardando", "Solicitação Enviada", Icons.Default.HourglassEmpty, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        RentalStatus.APPROVED_WAITING_DATES -> listOf("R$ --", "Aguardando", "Dono definindo datas", Icons.Default.DateRange, mainColor)
        RentalStatus.DATES_PROPOSED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Proposta", "Aguardando Aceite", Icons.Default.EditCalendar, mainColor)
        RentalStatus.AWAITING_DELIVERY -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Combinado", "Aguardando Entrega", Icons.Default.LocalShipping, warningColor)
        RentalStatus.DELIVERY_CONFIRMED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Combinado", "Confirmando Recebimento", Icons.Default.FactCheck, warningColor)
        RentalStatus.ONGOING -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Em Curso", "Em Uso", Icons.Default.Timer, successColor)
        RentalStatus.RETURN_SIGNALED -> listOf("R$ ${String.format("%.2f", rental.productPrice)}", "Em Curso", "Devolução Iniciada", Icons.Default.AssignmentReturn, warningColor)
    }

    Column {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            AsyncImage(model = rental.productImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().background(getFieldColor()))
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shape = RoundedCornerShape(50), shadowElevation = 2.dp) {
                    StatusChip(statusLabel = rental.status.label, color = rental.status.color)
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(10.dp, 6.dp)) {
                Column {
                    @Suppress("UNCHECKED_CAST")
                    Text(text = textoPreco as String, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    @Suppress("UNCHECKED_CAST")
                    Text(text = textoPrecoSub as String, color = Color.LightGray, fontSize = 10.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = rental.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                @Suppress("UNCHECKED_CAST")
                Icon(imageVector = icone as ImageVector, contentDescription = null, tint = corStatus as Color, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    @Suppress("UNCHECKED_CAST")
                    Text(text = "Status da Reserva", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    @Suppress("UNCHECKED_CAST")
                    Text(text = textoStatus as String, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = corStatus as Color)
                }
            }
        }
    }
}

@Composable
fun TicketAvatar(isMe: Boolean, userPhoto: String?, userName: String, color: Color) {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse)
    )
    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(if(isMe) scale else 1f)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .then(if(isMe) Modifier.border(2.dp, Color.White, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!userPhoto.isNullOrEmpty()) {
            AsyncImage(model = userPhoto, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
        } else {
            Text(text = if (isMe) "EU" else userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatusChip(statusLabel: String, color: Color) {
    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = statusLabel, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}