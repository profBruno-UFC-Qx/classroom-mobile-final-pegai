package com.pegai.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.Product
import com.pegai.app.model.Review
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.publicprofile.PublicProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PublicProfileScreen(
    navController: NavController,
    userId: String,
    viewModel: PublicProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedReviewTab by remember { mutableIntStateOf(0) }
    val reviewTabs = listOf("Como Locador", "Como Locatário")

    LaunchedEffect(userId) {
        viewModel.carregarPerfil(userId)
    }

    val currentBrandGradient = brandGradient()
    val mainColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = mainColor)
            }
        } else if (uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Usuário não encontrado.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // --- CABEÇALHO ---
                Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                            .background(currentBrandGradient)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

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
                                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.user!!.nome.take(1).uppercase(),
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = mainColor
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

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp, top = 16.dp)
                            .statusBarsPadding()
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF0E8FC6)
                        )
                    }
                }

                // --- REPUTAÇÃO ---
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
                        icon = Icons.Default.Store,
                        mainColor = mainColor
                    )
                    StatusCardPublic(
                        modifier = Modifier.weight(1f),
                        label = "Como Locatário",
                        nota = uiState.user!!.notaLocatario,
                        total = uiState.user!!.totalAvaliacoesLocatario,
                        icon = Icons.Default.ShoppingBag,
                        mainColor = mainColor
                    )
                }

                Spacer(modifier = Modifier.height((-20).dp))

                // --- ANÚNCIOS ---
                if (uiState.produtos.isNotEmpty()) {
                    Text(
                        text = "Anúncios Ativos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.produtos) { produto ->
                            ProductCardSmall(produto, mainColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- AVALIAÇÕES ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Avaliações",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    TabRow(
                        selectedTabIndex = selectedReviewTab,
                        containerColor = Color.Transparent,
                        contentColor = mainColor,
                        indicator = { tabPositions ->
                            if (selectedReviewTab < tabPositions.size) {
                                Box(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[selectedReviewTab])
                                        .height(3.dp)
                                        .padding(horizontal = 20.dp)
                                        .background(mainColor, RoundedCornerShape(3.dp))
                                )
                            }
                        },
                        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
                    ) {
                        reviewTabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedReviewTab == index,
                                onClick = { selectedReviewTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedReviewTab == index) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (selectedReviewTab == index) mainColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val papelFiltro = if (selectedReviewTab == 0) "LOCADOR" else "LOCATARIO"

                    val avaliacoesFiltradas = uiState.avaliacoes.filter {
                        it.papel.equals(papelFiltro, ignoreCase = true)
                    }

                    if (avaliacoesFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma avaliação como ${if (selectedReviewTab == 0) "locador" else "locatário"} ainda.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            avaliacoesFiltradas.forEach { avaliacao ->
                                ReviewCardReal(avaliacao)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StatusCardPublic(
    modifier: Modifier = Modifier,
    label: String,
    nota: Double,
    total: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    mainColor: Color
) {
    Card(
        modifier = modifier.height(135.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = mainColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.1f ★", nota),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "($total avaliações)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ProductCardSmall(produto: Product, mainColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(150.dp)
    ) {
        Column {
            AsyncImage(
                model = produto.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(110.dp).background(getFieldColor())
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(produto.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text("R$ ${produto.preco}/dia", color = mainColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ReviewCardReal(avaliacao: Review) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (avaliacao.autorFoto.isNotEmpty()) {
                AsyncImage(
                    model = avaliacao.autorFoto,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if(avaliacao.autorNome.isNotEmpty()) avaliacao.autorNome.take(1).uppercase() else "?",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(avaliacao.autorNome, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(formatarDataLong(avaliacao.data), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }

                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < avaliacao.nota) Color(0xFFFFC107) else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = avaliacao.comentario,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun formatarDataLong(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}