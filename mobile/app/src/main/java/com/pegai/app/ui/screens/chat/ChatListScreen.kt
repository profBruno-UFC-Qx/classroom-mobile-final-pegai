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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.viewmodel.AuthViewModel

data class ChatSummary(
    val chatId: String,
    val otherUserName: String,
    val otherUserPhoto: String = "",
    val productName: String,
    val lastMessage: String,
    val time: String,
    val isMeOwner: Boolean
)

@Composable
fun ChatListScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    // Controle das Abas (0 = Locador, 1 = Locatário)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Como Locador", "Como Locatário")

    val brandGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0A5C8A), Color(0xFF2ED1B2))
    )

    if (user == null) {
        GuestPlaceholder(
            title = "Suas Conversas",
            subtitle = "Faça login para negociar aluguéis e ver seu histórico de mensagens.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        // MOCK DE DADOS (Simulando o que virá do Firebase)
        val chats = listOf(
            ChatSummary("1", "Maria Oliveira", "", "Calculadora HP 12c", "Tudo bem, pode pegar amanhã.", "10:30", false),
            ChatSummary("2", "João Silva", "", "Livro Cálculo I", "Ainda está disponível?", "Ontem", true),
            ChatSummary("3", "Ana Costa", "", "Jaleco G", "Aceito sua oferta.", "Segunda", true),
            ChatSummary("4", "Pedro Rocha", "", "Furadeira Bosch", "Qual horário fica bom pra você?", "08:15", false),
            ChatSummary("5", "Lucas Lima", "", "Câmera Canon", "Vou querer alugar por 3 dias.", "Sexta", true)
        )

        // Filtra as listas para cada aba
        val chatsFiltrados = if (selectedTab == 0) {
            chats.filter { it.isMeOwner }
        } else {
            chats.filter { !it.isMeOwner }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brandGradient)
        ) {
            // === HEADER UNIFICADO ===
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

                // ABAS ESTILO ADICIONAR PRODUTO
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Color(0xFF0E8FC6),
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .background(brandGradient)
                            )
                        },
                        divider = { HorizontalDivider(color = Color(0xFFEEEEEE)) },
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
                                        color = if (selectedTab == index) Color(0xFF0E8FC6) else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // === CONTEÚDO DA LISTA  ===
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                if (chatsFiltrados.isEmpty()) {
                    EmptyChatState(if (selectedTab == 0) "locador" else "locatário")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(chatsFiltrados) { chat ->
                            ConversationItem(chat, brandGradient) {
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com Borda em Degradê solicitada
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, borderGradient, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE)),
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.otherUserName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = chat.time,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Nome do Produto em Azul para destaque sutil
                Text(
                    text = chat.productName,
                    fontSize = 12.sp,
                    color = Color(0xFF0E8FC6),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Última mensagem
                Text(
                    text = chat.lastMessage,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
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