package com.pegai.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.pegai.app.data.data.utils.formatarTempo
import com.pegai.app.model.Product
import com.pegai.app.model.UserAvaliacao
import com.pegai.app.ui.viewmodel.publicprofile.PublicProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    navController: NavController,
    userId: String, // ID do usuário que queremos ver
    viewModel: PublicProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.carregarPerfil(userId)
    }

    val mainGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil Público", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8FC6))
            }
        } else if (uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Usuário não encontrado.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                // --- CABEÇALHO (Igual ao perfil privado) ---
                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                            .background(mainGradient)
                    )

                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // FOTO
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(3.dp)
                                .clip(CircleShape)
                        ) {
                            if (uiState.user!!.fotoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = uiState.user!!.fotoUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.user!!.nome.first().toString(),
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0E8FC6)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.user!!.nome,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --- REPUTAÇÃO (Separada) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-40).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusCardPublic(
                        modifier = Modifier.weight(1f),
                        label = "Como Locador",
                        nota = uiState.user!!.notaLocador,
                        total = uiState.user!!.totalAvaliacoesLocador,
                        icon = Icons.Default.Store
                    )
                    StatusCardPublic(
                        modifier = Modifier.weight(1f),
                        label = "Como Locatário",
                        nota = uiState.user!!.notaLocatario,
                        total = uiState.user!!.totalAvaliacoesLocatario,
                        icon = Icons.Default.ShoppingBag
                    )
                }

                // --- PRODUTOS ANUNCIADOS ---
                if (uiState.produtos.isNotEmpty()) {
                    Text(
                        "Anúncios Ativos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.produtos) { produto ->
                            ProductCardSmall(produto)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- ÚLTIMAS AVALIAÇÕES ---
                Text(
                    "O que dizem sobre ${uiState.user!!.nome}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (uiState.avaliacoes.isEmpty()) {
                    Text(
                        "Nenhuma avaliação ainda.",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.avaliacoes.forEach { avaliacao ->
                            ReviewCardReal(avaliacao)
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES VISUAIS ---

@Composable
fun StatusCardPublic(
    modifier: Modifier = Modifier,
    label: String,
    nota: Double,
    total: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0E8FC6), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = String.format("%.1f ★", nota), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = "$total avaliações", fontSize = 10.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun ProductCardSmall(produto: Product) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column {
            AsyncImage(
                model = produto.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(produto.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("R$ ${produto.preco}/dia", color = Color(0xFF0E8FC6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ReviewCardReal(avaliacao: UserAvaliacao) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Foto do Autor
            if (avaliacao.autorFoto.isNotEmpty()) {
                AsyncImage(
                    model = avaliacao.autorFoto,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(avaliacao.autorNome.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(avaliacao.autorNome, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (avaliacao.papel == "locador") "(Locador)" else "(Locatário)",
                        fontSize = 10.sp,
                        color = Color(0xFF0E8FC6),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Estrelas
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < avaliacao.nota) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(avaliacao.comentario, fontSize = 13.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(formatarTempo(avaliacao.data), fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}