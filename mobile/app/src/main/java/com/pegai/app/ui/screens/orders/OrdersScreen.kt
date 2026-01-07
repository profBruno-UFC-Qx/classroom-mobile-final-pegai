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
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.orders.OrdersViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun OrdersScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: OrdersViewModel = viewModel()
) {
    val authUser by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Em Andamento", "Histórico")
    val brandGradient = Brush.horizontalGradient(colors = listOf(Color(0xFF0A5C8A), Color(0xFF2ED1B2)))

    LaunchedEffect(authUser) {
        authUser?.let { viewModel.carregarAlugueis(it.uid) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // --- HEADER ---
        Column(modifier = Modifier.fillMaxWidth().background(brandGradient)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Meus Aluguéis", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFF5F5F5), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF0E8FC6),
                    indicator = { tabPositions ->
                        Box(Modifier.tabIndicatorOffset(tabPositions[selectedTab]).height(3.dp).background(brandGradient))
                    },
                    divider = { HorizontalDivider(color = Color(0xFFEEEEEE)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp) }
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF0E8FC6)) }
        } else {
            val lista = if (selectedTab == 0) uiState.activeRentals else uiState.inactiveRentals
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                items(lista) { rental ->
                    RentalTicketCard(rental = rental, currentUser = authUser, onClick = { })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun RentalTicketCard(rental: Rental, currentUser: User?, onClick: () -> Unit) {
    val currentUserId = currentUser?.uid ?: ""
    val isLocador = rental.locadorId == currentUserId
    val nomeLocadorDisplay = if (isLocador) "Você" else rental.locadorNome.split(" ").first()
    val nomeLocatarioDisplay = if (!isLocador) "Você" else rental.locatarioNome.split(" ").first()
    val brandGradient = Brush.horizontalGradient(colors = listOf(Color(0xFF0A5C8A), Color(0xFF2ED1B2)))

    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), shadowElevation = 4.dp) {
            Box(modifier = Modifier.fillMaxWidth().background(brandGradient).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "Dono", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TicketAvatar(isMe = isLocador, userPhoto = currentUser?.fotoUrl, userName = nomeLocadorDisplay, color = Color(0xFF0A5C8A))
                            Spacer(Modifier.width(8.dp))
                            Text(text = nomeLocadorDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 100.dp))
                        }
                    }
                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Locatário", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = nomeLocatarioDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 100.dp))
                            Spacer(Modifier.width(8.dp))
                            TicketAvatar(isMe = !isLocador, userPhoto = currentUser?.fotoUrl, userName = nomeLocatarioDisplay, color = Color(0xFF2ED1B2))
                        }
                    }
                }
            }
        }
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp), color = Color.White, shadowElevation = 4.dp) {
            RentalCardBody(rental)
        }
    }
}

@Composable
fun RentalCardBody(rental: Rental) {
    val mainColor = Color(0xFF0E8FC6)
    val errorColor = Color(0xFFD32F2F)

    val (textoPreco, textoPrecoSub, textoStatus, icone, corStatus) = when (rental.status) {
        RentalStatus.COMPLETED -> {
            listOf("R$ ${String.format("%.0f", rental.productPrice)}", "Aluguel encerrado", "Concluído com sucesso!", Icons.Default.CheckCircle, Color(0xFF2E7D32))
        }
        RentalStatus.CANCELLED, RentalStatus.DECLINED -> {
            listOf(
                "R$ ${String.format("%.0f", rental.productPrice)}",
                "Solicitação encerrada",
                if (rental.status == RentalStatus.CANCELLED) "Aluguel cancelado" else "Solicitação recusada",
                Icons.Default.Close,
                errorColor
            )
        }
        RentalStatus.PAID, RentalStatus.ONGOING -> {
            val diff = rental.dataFim.toDate().time - rental.dataInicio.toDate().time
            val dias = TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1)
            val sdf = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
            listOf("R$ ${String.format("%.0f", rental.productPrice * dias)}", "Total ($dias dias)", "${sdf.format(rental.dataInicio.toDate())} - ${sdf.format(rental.dataFim.toDate())}", Icons.Default.CalendarMonth, Color(0xFF444444))
        }
        RentalStatus.APPROVED -> {
            listOf("R$ ${String.format("%.0f", rental.productPrice)}", "por diária", "Negociar datas no chat", Icons.Default.Chat, mainColor)
        }
        else -> {
            listOf("R$ ${String.format("%.0f", rental.productPrice)}", "por diária", "Aguardando aceite do dono", Icons.Default.HourglassEmpty, Color.Gray)
        }
    }

    Column {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            AsyncImage(model = rental.productImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0)))
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                Surface(color = Color.White, shape = RoundedCornerShape(50), shadowElevation = 2.dp) {
                    StatusChip(statusLabel = rental.status.label, color = rental.status.color)
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(10.dp, 6.dp)) {
                Column {
                    Text(text = textoPreco as String, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = textoPrecoSub as String, color = Color.LightGray, fontSize = 10.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = rental.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF333333), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icone as ImageVector,
                    contentDescription = null,
                    tint = if(corStatus == Color(0xFF444444)) mainColor else corStatus as Color,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Status da Reserva", fontSize = 10.sp, color = Color.Gray)
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
            .background(Color.White)
            .then(if(isMe) Modifier.border(4.dp, Color.White, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (isMe && !userPhoto.isNullOrEmpty()) {
            AsyncImage(model = userPhoto, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
        } else {
            Text(text = if (isMe) "EU" else userName.take(1).uppercase(), color = if (isMe) Color(0xFF0A5C8A) else color, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
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