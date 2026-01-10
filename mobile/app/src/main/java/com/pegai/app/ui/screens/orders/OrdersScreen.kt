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

    // --- LISTA DE ABAS COM ÍCONES ---
    val tabs = listOf(
        "Em Andamento" to Icons.Default.Sync,
        "Histórico" to Icons.Default.History
    )

    val currentBrandGradient = brandGradient()

    LaunchedEffect(authUser) {
        authUser?.let { viewModel.carregarAlugueis(it.uid) }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxWidth().background(currentBrandGradient)) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Meus Aluguéis",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp) // Curva 32dp
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = azulPrimario,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .padding(horizontal = 32.dp)
                                .background(currentBrandGradient, CircleShape)
                        )
                    },
                    divider = {}, // Sem linha divisória
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
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = azulPrimario)
            }
        } else {
            val lista = if (selectedTab == 0) uiState.activeRentals else uiState.inactiveRentals
            if (lista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum aluguel encontrado.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(lista) { rental ->
                        RentalTicketCard(
                            rental = rental,
                            currentUser = authUser,
                            onClick = { navController.navigate("chat_detail/${rental.id}") }
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

    Box(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            RentalCardBody(rental)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().height(95.dp),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize().background(currentBrandGradient).padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        Text("Dono", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TicketAvatar(isLocador, if (isLocador) currentUser?.fotoUrl else rental.locadorFoto, nomeLocadorDisplay, Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(nomeLocadorDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Icon(Icons.Default.SwapHoriz, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(38.dp).padding(horizontal = 4.dp))

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Locatário", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Text(nomeLocatarioDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                            Spacer(Modifier.width(8.dp))
                            TicketAvatar(!isLocador, if (!isLocador) currentUser?.fotoUrl else rental.locatarioFoto, nomeLocatarioDisplay, Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RentalCardBody(rental: Rental) {
    val corStatus = rental.status.color

    Column {
        Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
            AsyncImage(
                model = rental.productImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(getFieldColor())
            )

            Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 105.dp, end = 16.dp)) {
                Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shape = RoundedCornerShape(50), shadowElevation = 3.dp) {
                    StatusChip(rental.status.label, corStatus)
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(10.dp, 6.dp)) {
                Text("R$ ${String.format("%.2f", rental.productPrice)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(rental.productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = corStatus, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Status da Reserva", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(rental.status.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = corStatus)
                }
            }
        }
    }
}

@Composable
fun TicketAvatar(isMe: Boolean, userPhoto: String?, userName: String, color: Color) {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse)
    )
    Box(
        modifier = Modifier.size(44.dp).scale(if(isMe) scale else 1f).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
            .then(if(isMe) Modifier.border(2.dp, Color.White, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!userPhoto.isNullOrEmpty()) {
            AsyncImage(model = userPhoto, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
        } else {
            Text(if (isMe) "EU" else userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatusChip(statusLabel: String, color: Color) {
    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(statusLabel, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}