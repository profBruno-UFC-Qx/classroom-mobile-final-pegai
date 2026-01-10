package com.pegai.app.ui.screens.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
import androidx.compose.foundation.border
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
import com.pegai.app.repository.ChatRepository
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.details.ProductDetailsViewModel
import com.pegai.app.ui.viewmodel.details.ReviewUI
import kotlinx.coroutines.launch
=======
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.details.ProductDetailsViewModel
import com.pegai.app.ui.viewmodel.details.ReviewUI
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productId: String?,
    authViewModel: AuthViewModel,
    viewModel: ProductDetailsViewModel = viewModel()
) {
    val user by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
    val scope = rememberCoroutineScope()
    val chatRepository = remember { ChatRepository() }
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt

    LaunchedEffect(productId) {
        viewModel.carregarDetalhes(productId)
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
    var currentImageIndex by remember { mutableIntStateOf(0) }
    var showRentalConfirmationDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val mainColor = MaterialTheme.colorScheme.primary
    val currentBrandGradient = brandGradient()
    val bottomBarGradient = Brush.horizontalGradient(listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2)))
=======
    var currentImageIndex by remember { mutableStateOf(0) }
    var showRentalConfirmationDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val mainColor = Color(0xFF0E8FC6)
    val bottomBarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2))
    )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mainColor)
        }
        return
    }

    val product = uiState.produto ?: return
    val imagens = uiState.imagensCarrossel
    val reviewsList = uiState.reviews

    Scaffold(
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
        contentWindowInsets = WindowInsets(0.dp),
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
        bottomBar = {
            BottomRentBar(
                price = "R$ ${String.format("%.2f", product.preco)} / dia",
                onRentClick = {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                    if (user != null) {
                        if (user?.uid != product.donoId) {
                            showRentalConfirmationDialog = true
                        }
                    } else {
                        showLoginRequiredDialog = true
                    }
=======
                    if (user != null) showRentalConfirmationDialog = true
                    else showLoginRequiredDialog = true
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                },
                gradient = bottomBarGradient
            )
        }
    ) { paddingValues ->
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
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
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.FavoriteBorder, null, tint = Color(0xFF0E8FC6)) }
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
=======
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // === CARROSSEL ===
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).background(Color(0xFFF0F0F0))) {
                    if (imagens.isNotEmpty()) {
                        AsyncImage(
                            model = imagens[currentImageIndex],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (imagens.size > 1) {
                    if (currentImageIndex > 0) {
                        Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
                            IconButton(onClick = { currentImageIndex-- }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(40.dp)) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White)
                            }
                        }
                    }
                    if (currentImageIndex < imagens.size - 1) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)) {
                            IconButton(onClick = { currentImageIndex++ }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(40.dp)) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
                            }
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)).padding(8.dp, 4.dp)) {
                        Text("${currentImageIndex + 1}/${imagens.size}", color = Color.White, fontSize = 12.sp)
                    }
                }

                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start = 12.dp).offset(y = (-25).dp).background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
                }

                Row(modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(end = 12.dp).offset(y = (-25).dp)) {
                    IconButton(onClick = { /* Share */ }, modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                        Icon(Icons.Default.Share, null, tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { /* Favorite */ }, modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = Color.Black)
                    }
                }
            }

            // === CONTEÚDO ===
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(color = mainColor.copy(alpha = 0.1f), shape = RoundedCornerShape(50)) {
                    Text(product.categoria, modifier = Modifier.padding(12.dp, 6.dp), style = MaterialTheme.typography.labelMedium, color = mainColor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(product.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Text(" ${product.nota} ", fontWeight = FontWeight.SemiBold)
                    Text("• ${uiState.avaliacoesCount} Avaliações", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(24.dp))

                // CARD DONO
                Text("Anunciado por", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.PublicProfile.createRoute(product.donoId))
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text(uiState.nomeDono.first().toString(), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(uiState.nomeDono, fontWeight = FontWeight.Bold)
                            Text("Ver perfil", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Sobre o produto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.descricao, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5A5A5A), lineHeight = 24.sp)

                Spacer(modifier = Modifier.height(32.dp))
                Text("Avaliações do Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                reviewsList.forEach { review ->
                    ReviewProdutoItem(review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showRentalConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showRentalConfirmationDialog = false },
                title = { Text(text = "Solicitar Aluguel?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Você deseja enviar uma solicitação de aluguel para ${uiState.nomeDono}? O chat será aberto para negociação.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRentalConfirmationDialog = false
                            // TODO: Navegar para o chat no futuro
                            // navController.navigate("chat_detail/novo")
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(25.dp)
                    ) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                        Box(modifier = Modifier.background(bottomBarGradient).padding(horizontal = 20.dp, vertical = 10.dp)) {
=======
                        Box(
                            modifier = Modifier
                                .background(bottomBarGradient)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                            Text("Sim, solicitar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRentalConfirmationDialog = false }) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
=======
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
            )
        }

        if (showLoginRequiredDialog) {
            AlertDialog(
                onDismissRequest = { showLoginRequiredDialog = false },
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Login Necessário", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                text = { Text("Para alugar este produto, você precisa entrar na sua conta.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
=======
                title = { Text("Login Necessário", fontWeight = FontWeight.Bold) },
                text = { Text("Para alugar este produto, você precisa entrar na sua conta.") },
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                        Box(modifier = Modifier.background(bottomBarGradient).padding(horizontal = 20.dp, vertical = 10.dp)) {
=======
                        Box(
                            modifier = Modifier
                                .background(bottomBarGradient)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                            Text("Fazer Login", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginRequiredDialog = false }) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
=======
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
            )
        }
    }
}

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
=======
// COMPONENTES AUXILIARES
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
@Composable
fun ReviewProdutoItem(review: ReviewUI) {
    Card(
        shape = RoundedCornerShape(12.dp),
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
=======
        colors = CardDefaults.cardColors(containerColor = Color.White),
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
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
=======
                Text(review.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        repeat(5) { index ->
                            Icon(Icons.Default.Star, null, tint = if (index < review.rating) Color(0xFFFFB300) else Color(0xFFE0E0E0), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(review.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5A5A5A))
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
        }
    }
}

@Composable
fun BottomRentBar(price: String, onRentClick: () -> Unit, gradient: Brush) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
    Surface(shadowElevation = 20.dp, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
=======
    Surface(shadowElevation = 20.dp, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/details/ProductDetailsScreen.kt
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