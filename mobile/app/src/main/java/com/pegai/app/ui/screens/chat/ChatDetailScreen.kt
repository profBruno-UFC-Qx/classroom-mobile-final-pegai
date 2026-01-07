package com.pegai.app.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
//import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

data class Message(
    val id: String,
    val content: String? = null,
    val imageUrl: String? = null,
    val timestamp: String,
    val isFromMe: Boolean,
    val isRead: Boolean = false
)

val sampleMessages = listOf(
    Message("1", "Oie :)\nQueria alugar seus pincéis, tem tempo agora ?", null, "11:30", false),
    Message("2", "Estou ocupado agora, a gente pode ver isso depois ? \uD83D\uDE01", null, "11:30", true), // Emoji sorriso
    Message("3", null, "url_do_macaco_aqui", "11:31", true, isRead = true)
)

@Composable
fun ChatDetailScreen(
    userId: String,
    onBackClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF0F2F5)
    val listState = rememberLazyListState()
    val userName = if (userId == "1") "teste1" else "teste2"

    Scaffold(
        containerColor = backgroundColor,
        topBar = { ChatTopBar(userName = userName, onBackClick = onBackClick) },
        bottomBar = { ChatInputBar() }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(sampleMessages) { message ->
                MessageBubble(message)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(userName: String,onBackClick: () -> Unit) {
    Surface(shadowElevation = 2.dp) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.Gray
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color(0xFF2979FF) // Azul da seta
                    )
                }
            },
            actions = {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Ligar",
                        tint = Color.Gray
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isMe = message.isFromMe

    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = bubbleShape,
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                if (message.content != null) {
                    Text(
                        text = message.content,
                        color = Color.Black,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                }

                if (message.imageUrl != null) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                         Text("Imagem", color = Color.White)
                    }
                }


                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Lido",
                            tint = if (message.isRead) Color(0xFF2979FF) else Color.Gray, // Azul se lido (simulação) ou Cinza
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar() {
    var text by remember { mutableStateOf("") }

    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Câmera",
                    tint = Color.Gray
                )
            }


            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Digite uma mensagem...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    disabledContainerColor = Color(0xFFF0F2F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = false,
                maxLines = 4
            )


            TextButton(
                onClick = { },
                enabled = text.isNotBlank() || true
            ) {
                Text(
                    text = "Enviar",
                    color = Color(0xFF2979FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatDetailPreview() {
    ChatDetailScreen(
        userId = TODO(),
        onBackClick = TODO()
    )
}