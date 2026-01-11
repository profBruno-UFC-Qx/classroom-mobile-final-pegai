package com.pegai.app.ui.screens.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.repository.ChatRepository
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.details.ProductDetailsViewModel
import com.pegai.app.ui.viewmodel.details.ReviewUI
import com.pegai.app.ui.viewmodel.favorites.FavoritesViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder


@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productId: String?,
    authViewModel: AuthViewModel,
    viewModel: ProductDetailsViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel
) {
    val user by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val chatRepository = remember { ChatRepository() }

    LaunchedEffect(productId) {
        viewModel.carregarDetalhes(productId)
    }

    var currentImageIndex by remember { mutableIntStateOf(0) }
    var showRentalConfirmationDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val mainColor = MaterialTheme.colorScheme.primary
    val currentBrandGradient = brandGradient()
    val bottomBarGradient = Brush.horizontalGradient(listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2)))

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mainColor)
        }
        return
    }

    val product = uiState.produto ?: return
    val imagens = uiState.imagensCarrossel
    val reviewsList = uiState.reviews
    val u by authViewModel.usuarioLogado.collectAsState()
    val fav by favoritesViewModel.uiState.collectAsState()
    val p = uiState.produto!!               // assume que aqui já carregou
    val pid = p.pid.ifBlank { productId.orEmpty() }
    val isFav = pid in fav.favoriteIds


    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            BottomRentBar(
                price = "R$ ${String.format("%.2f", product.preco)} / dia",
                onRentClick = {
                    if (user != null) {
                        if (user?.uid != product.donoId) {
                            showRentalConfirmationDialog = true
                        }
                    } else {
                        showLoginRequiredDialog = true
                    }
                },
                gradient = bottomBarGradient
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(currentBrandGradient)
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

                // --- Media Carousel ---
                Box(modifier = Modifier.fillMaxWidth().height(480.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(getFieldColor())) {
                            if (imagens.isNotEmpty()) {
                                AsyncImage(
                                    model = imagens[currentImageIndex],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(6.dp).background(currentBrandGradient))
                    }

                    if (imagens.size > 1) {
                        if (currentImageIndex > 0) {
                            Surface(
                                onClick = { currentImageIndex-- },
                                modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp).size(40.dp),
                                shape = CircleShape, color = Color.White, shadowElevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color(0xFF0E8FC6))
                                }
                            }
                        }

                        if (currentImageIndex < imagens.size - 1) {
                            Surface(
                                onClick = { currentImageIndex++ },
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(40.dp),
                                shape = CircleShape, color = Color.White, shadowElevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFF0E8FC6))
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 20.dp),
                            shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 8.dp
                        ) {
                            Text(
                                text = "${currentImageIndex + 1}/${imagens.size}",
                                color = Color(0xFF0E8FC6), fontWeight = FontWeight.Bold, fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 16.dp).size(48.dp),
                        shape = CircleShape, color = Color.White, shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF0E8FC6))
                        }
                    }

                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 16.dp)) {
                        Surface(onClick = { }, modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.White, shadowElevation = 8.dp) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Share, null, tint = Color(0xFF0E8FC6)) }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(onClick = { }, modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.White, shadowElevation = 8.dp) {
                            Box(contentAlignment = Alignment.Center) { IconButton(onClick = { if (u == null) navController.navigate("login") else favoritesViewModel.toggleFavorite(p.copy(pid = pid)) }) { Icon(imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null, tint = Color(0xFF0E8FC6)) } }
                        }
                    }
                }

                // --- Product Info ---
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(24.dp)) {
                    Surface(color = mainColor.copy(alpha = 0.1f), shape = RoundedCornerShape(50)) {
                        Text(
                            text = product.categoria,
                            modifier = Modifier.padding(12.dp, 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = mainColor, fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(product.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                        // Mostra a nota agregada e o total de reviews
                        Text(" ${String.format("%.1f", product.nota)} ", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        Text("• ${product.totalAvaliacoes} Avaliações", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Anunciado por", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth().clickable {
                            navController.navigate(Screen.PublicProfile.createRoute(product.donoId))
                        }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

                            // --- LÓGICA DA FOTO DO DONO ---
                            if (uiState.fotoDono.isNotEmpty()) {
                                AsyncImage(
                                    model = uiState.fotoDono,
                                    contentDescription = "Foto do dono",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.nomeDono.firstOrNull()?.toString() ?: "U",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(uiState.nomeDono, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Ver perfil", fontSize = 12.sp, color = mainColor)
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Sobre o produto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.descricao, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), lineHeight = 24.sp)

                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Avaliações do Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (reviewsList.isEmpty()) {
                        Text("Nenhuma avaliação ainda.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        reviewsList.forEach { review ->
                            ReviewProdutoItem(review)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // --- Dialogs ---
        if (showRentalConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showRentalConfirmationDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(text = "Solicitar Aluguel?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                text = { Text("Você deseja enviar uma solicitação de aluguel para ${uiState.nomeDono}? O chat será aberto para negociação.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val chatId = chatRepository.iniciarNegociacao(
                                        renterId = user!!.uid,
                                        ownerId = product.donoId,
                                        product = product
                                    )
                                    showRentalConfirmationDialog = false
                                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Box(modifier = Modifier.background(bottomBarGradient).padding(horizontal = 20.dp, vertical = 10.dp)) {
                            Text("Sim, solicitar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRentalConfirmationDialog = false }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            )
        }

        if (showLoginRequiredDialog) {
            AlertDialog(
                onDismissRequest = { showLoginRequiredDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Login Necessário", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                text = { Text("Para alugar este produto, você precisa entrar na sua conta.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showLoginRequiredDialog = false
                            navController.navigate(Screen.Login.route)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Box(modifier = Modifier.background(bottomBarGradient).padding(horizontal = 20.dp, vertical = 10.dp)) {
                            Text("Fazer Login", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginRequiredDialog = false }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            )
        }
    }
}

@Composable
fun ReviewProdutoItem(review: ReviewUI) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(review.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        repeat(5) { index ->
                            Icon(Icons.Default.Star, null, tint = if (index < review.rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(review.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun BottomRentBar(price: String, onRentClick: () -> Unit, gradient: Brush) {
    Surface(shadowElevation = 20.dp, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Box(modifier = Modifier.fillMaxWidth().background(gradient).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Valor: ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(price, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                }
                Button(
                    onClick = onRentClick, colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp), modifier = Modifier.height(40.dp).width(140.dp)
                ) {
                    Text("Alugar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0E8FC6))
                }
            }
        }
    }
}