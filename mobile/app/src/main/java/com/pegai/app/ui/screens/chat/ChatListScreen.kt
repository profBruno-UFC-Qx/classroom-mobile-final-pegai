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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmsFailed
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.components.PulsingNotificationBadge // Import the new component
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.chat.ChatListViewModel

data class ChatSummary(
    val chatId: String,
    val otherUserName: String,
    val otherUserPhoto: String = "",
    val productName: String,
    val lastMessage: String,
    val time: String,
    val isMeOwner: Boolean,
    val unreadCount: Int = 0
)

@Composable
fun ChatListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ChatListViewModel = viewModel()
) {
    val user by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(user) {
        user?.let { viewModel.carregarConversas(it.uid) }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Como Locador", "Como Locatário")
    val currentBrandGradient = brandGradient()

    if (user == null) {
        GuestPlaceholder(
            title = "Suas Conversas",
            subtitle = "Faça login para negociar aluguéis e ver seu histórico de mensagens.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        val chatsParaExibir = if (selectedTab == 0) uiState.chatsAsOwner else uiState.chatsAsRenter

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(currentBrandGradient)
        ) {
            // --- Header ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mensagens",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .background(currentBrandGradient)
                            )
                        },
                        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // --- Chat List Content ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (chatsParaExibir.isEmpty()) {
                    EmptyChatState(if (selectedTab == 0) "locador" else "locatário")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(chatsParaExibir) { chat ->
                            ConversationItem(chat, currentBrandGradient) {
                                navController.navigate("chat_detail/${chat.chatId}")
                            }
                        }
                        item { Spacer(modifier = Modifier.navigationBarsPadding()) }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    chat: ChatSummary,
    borderGradient: Brush,
    onClick: () -> Unit
) {
    // Background muda se tiver mensagem não lida
    val backgroundColor = if (chat.unreadCount > 0)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    else
        MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- AVATAR + PULSING BADGE ---
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .border(2.dp, borderGradient, CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (chat.otherUserPhoto.isNotEmpty()) {
                        AsyncImage(
                            model = chat.otherUserPhoto,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // -> PULSING DOT LOGIC HERE <-
                if (chat.unreadCount > 0) {
                    PulsingNotificationBadge(
                        modifier = Modifier.offset(x = 2.dp, y = (-2).dp),
                        size = 14.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.otherUserName,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = chat.time,
                            fontSize = 11.sp,
                            color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )

                        if (chat.unreadCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.height(20.dp).defaultMinSize(minWidth = 20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                                    Text(
                                        text = chat.unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = chat.productName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = chat.lastMessage,
                    fontSize = 13.sp,
                    color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface else Color.Gray,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun EmptyChatState(tipo: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.SmsFailed, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Nenhuma conversa como $tipo.",
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}