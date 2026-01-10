package com.pegai.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
=======
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
=======
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pegai.app.model.Category
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.ui.navigation.Screen
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel
=======
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel



>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel = viewModel()
) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
    val uiState by viewModel.uiState.collectAsState()
    val usuarioLogado by authViewModel.usuarioLogado.collectAsState()
=======
    // [MVVM] Estado Único
    val uiState by viewModel.uiState.collectAsState()
    val usuarioLogado by authViewModel.usuarioLogado.collectAsState()

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
    val context = LocalContext.current

    val dynamicGradient = brandGradient()

    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val concedido = permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
        if (concedido) viewModel.obterLocalizacaoAtual(context)
    }

    LaunchedEffect(Unit) {
        val temPermissao = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (temPermissao) {
=======
        if (concedido) {
            viewModel.obterLocalizacaoAtual(context)
        }
    }

    LaunchedEffect(Unit) {
        val temPermissaoFina = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (temPermissaoFina) {
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
            viewModel.obterLocalizacaoAtual(context)
        } else {
            launcherPermissao.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(dynamicGradient)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    Spacer(modifier = Modifier.height(16.dp))

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                    HomeHeader(
                        user = usuarioLogado,
                        localizacao = uiState.localizacaoAtual,
                        onLoginClick = { navController.navigate(Screen.Login.route) },
                        onFavoritesClick = { navController.navigate("favorites") },
                        onMapClick = { viewModel.openMapModal() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBar(texto = uiState.textoPesquisa, onTextoChange = { viewModel.atualizarPesquisa(it) })
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 100.dp),
                modifier = Modifier.weight(1f)
            ) {
                item(span = { GridItemSpan(2) }) {
                    CategoryRow(
                        categorias = viewModel.categoriasFiltro,
                        selecionada = uiState.categoriaSelecionada,
                        onCategoriaClick = { viewModel.selecionarCategoria(it) },
                        selectedGradient = dynamicGradient
                    )
                }

                if (uiState.produtosPopulares.isNotEmpty() && uiState.textoPesquisa.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                                Text("Em Alta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.TrendingUp, null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                                items(uiState.produtosPopulares) { CompactProductCard(it) }
=======
        // HEADER
        HomeHeader(
            user = usuarioLogado,
            localizacao = uiState.localizacaoAtual,
            onLoginClick = { navController.navigate(Screen.Login.route) },
            onFavoritesClick = { navController.navigate("favorites") }

        )

        Spacer(modifier = Modifier.height(8.dp))

        // GRID PRINCIPAL
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Barra de Pesquisa
            item(span = { GridItemSpan(2) }) {
                SearchBar(
                    texto = uiState.textoPesquisa,
                    onTextoChange = { viewModel.atualizarPesquisa(it) }
                )
            }

            // Filtro de Categorias
            item(span = { GridItemSpan(2) }) {
                CategoryRow(
                    categorias = uiState.categorias,
                    selecionada = uiState.categoriaSelecionada,
                    onCategoriaClick = { viewModel.selecionarCategoria(it) }
                )
            }

            // Seção: Populares (Só aparece se tiver itens)
            if (uiState.produtosPopulares.isNotEmpty() && uiState.textoPesquisa.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Text(
                            text = "Populares",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 2.dp)
                        ) {
                            items(uiState.produtosPopulares) { produto ->
                                CompactProductCard(produto)
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                            }
                        }
                    }
                }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                item(span = { GridItemSpan(2) }) {
                    Text("Explorar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                }

                items(uiState.produtos) { produto ->
                    ProductCard(product = produto, onClick = { navController.navigate("product_details/${produto.pid}") })
                }
=======
            // Seção: Lista Geral
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Para Você",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            items(uiState.produtos) { produto ->
                ProductCard(
                    product = produto,
                    onClick = {
                         navController.navigate("product_details/${produto.pid}")
                    }
                )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
            }
        }

        if (uiState.isMapModalVisible) {
            LocationFilterModal(
                radiusKm = uiState.radiusKm,
                userLat = uiState.userLat ?: -23.55052,
                userLng = uiState.userLng ?: -46.63330,
                onRadiusChange = { viewModel.updateRadius(it) },
                onDismiss = { viewModel.closeMapModal() },
                onApply = { viewModel.closeMapModal() }
            )
        }
    }
}

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFilterModal(
    radiusKm: Float,
    userLat: Double,
    userLng: Double,
    onRadiusChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val userLocation = LatLng(userLat, userLng)
    val dynamicGradient = brandGradient()
    val primaryColor = MaterialTheme.colorScheme.primary

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 12f)
=======
// --- COMPONENTES AUXILIARES ---

@Composable
fun SearchBar(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(50),
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (texto.isEmpty()) {
                    Text("O que você procura?", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                BasicTextField(
                    value = texto,
                    onValueChange = onTextoChange,
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
// COMPONENTES AUXILIARES
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            // Imagem e Preço
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { /* TODO: Favoritar */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2F7DBF) // Roxo
                ) {
                    Text(
                        text = "R$ ${product.preco} / dia",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Textos
            Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 12.dp, bottom = 4.dp)) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Nota",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.nota.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "("+product.totalAvaliacoes.toString()+")",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dono: ${product.donoNome}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)) {
=======
        Column(modifier = Modifier.padding(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color (0xFF2F7DBF)
                ) {
                    Text(
                        text = "R$ ${product.preco}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
            Text(
                "Filtrar por Localização",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
            )

            // Mapa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                ) {
                    Marker(state = MarkerState(position = userLocation), title = "Você está aqui")
                    Circle(
                        center = userLocation,
                        radius = (radiusKm * 1000).toDouble(),
                        fillColor = primaryColor.copy(alpha = 0.15f),
                        strokeColor = primaryColor,
                        strokeWidth = 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Distância do raio", fontWeight = FontWeight.Medium)

                    Text(
                        "${radiusKm.toInt()} km",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- SLIDER COM DEGRADÊ ---
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .padding(horizontal = 2.dp)
                            .background(
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(2.dp)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (radiusKm - 1f) / 14f)
                            .height(6.dp)
                            .background(dynamicGradient, RoundedCornerShape(3.dp))
                    )

                    Slider(
                        value = radiusKm,
                        onValueChange = onRadiusChange,
                        valueRange = 1f..15f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryColor,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1 km", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("15 km", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onApply() },
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(dynamicGradient)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aplicar Filtro",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    user: User?,
    localizacao: String,
    onLoginClick: () -> Unit,
    onFavoritesClick: () -> Unit,
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
    onMapClick: () -> Unit
) {
    val headerButtonBg = MaterialTheme.colorScheme.surface
    val headerButtonContent = MaterialTheme.colorScheme.primary
=======
) {
    // Gradiente do Botão "Entrar"
    val blueGreenGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A5C8A), // Azul escuro
            Color(0xFF0E8FC6), // Azul médio
            Color(0xFF2ED1B2)  // Verde água
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 100f)
    )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                    if (user.fotoUrl.isNotEmpty()) {
                        AsyncImage(model = user.fotoUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
=======
                    if (user.fotoUrl != "") {
                        AsyncImage(
                            model = user.fotoUrl,
                            contentDescription = "Foto de Perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                    } else {
                        Text(user.nome.first().toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Olá, ${user.nome.split(" ").first()} 👋",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = localizacao,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Bem-vindo ao Pegaí",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = localizacao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(headerButtonBg, CircleShape) // Cor dinâmica do Surface
                ) {
                    Icon(Icons.Default.Map, null, tint = headerButtonContent) // Cor dinâmica Primary
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onFavoritesClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(headerButtonBg, CircleShape)
                ) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = headerButtonContent)
                }
            } else {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                Button(
                    onClick = onLoginClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text("Entrar", fontWeight = FontWeight.Bold)
=======
                // BOTÃO COM GRADIENTE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(blueGreenGradient)
                ) {
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Entrar")
                    }
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                }
            }
        }
    }
}

@Composable
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
fun ProductCard(product: Product, onClick: () -> Unit) {
    val dynamicGradient = brandGradient()
=======
fun SearchBar() {
    val textoPesquisa by remember { mutableStateOf("") }
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt

    Card(
        modifier = Modifier.fillMaxWidth().height(320.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                AsyncImage(model = product.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shadowElevation = 2.dp) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.FavoriteBorder, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                        .background(dynamicGradient)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "R$ ${product.preco} / dia", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(dynamicGradient))

            Column(modifier = Modifier.padding(14.dp).weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(product.titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(String.format("%.1f", product.nota), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(18.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(3.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${product.donoNome}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = getFieldColor(),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = product.categoria,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactProductCard(product: Product) {
    val dynamicGradient = brandGradient()

    Card(modifier = Modifier.width(160.dp).height(220.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                AsyncImage(model = product.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier.align(Alignment.BottomStart).padding(6.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, Color.White, RoundedCornerShape(6.dp)).background(dynamicGradient).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("R$ ${product.preco}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(dynamicGradient))

            Column(modifier = Modifier.padding(10.dp).weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Text(product.titulo, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${product.nota}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 1)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
=======
            Box(modifier = Modifier.weight(1f)) {
                if (textoPesquisa.isEmpty()) {
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
                    Text(
                        text = product.categoria,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.7f, fill = false),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(texto: String, onTextoChange: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (texto.isEmpty()) Text("Buscar produtos...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                BasicTextField(value = texto, onValueChange = onTextoChange, singleLine = true, textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp), cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun CategoryRow(
    categorias: List<String>,
    selecionada: String,
    onCategoriaClick: (String) -> Unit,
    selectedGradient: Brush? = null
) {
    val mainColor = Color(0xFF0E8FC6)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        items(categorias) { categoriaNome ->
            val isSelected = categoriaNome == selecionada
            val icon = if (categoriaNome == "Todos") Icons.Default.GridView else Category.fromNome(categoriaNome).icon

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
            Surface(
                modifier = Modifier
                    .height(34.dp),
                shape = RoundedCornerShape(50),
                color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.surface,
                shadowElevation = if (isSelected) 0.dp else 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            if (isSelected && selectedGradient != null) selectedGradient
                            else if (isSelected) SolidColor(MaterialTheme.colorScheme.primary)
                            else SolidColor(MaterialTheme.colorScheme.surface)
                        )
                        .clickable { onCategoriaClick(categoriaNome) }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = categoriaNome,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
=======
            FilterChip(
                selected = isSelected,
                onClick = { onCategoriaClick(categoria) },
                label = {
                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier.height(32.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = mainColor,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFFFFFFF),
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) mainColor else Color(0xFFEEEEEE),
                    borderWidth = 1.dp
                ),
                shape = RoundedCornerShape(50)
            )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/home/HomeScreen.kt
        }
    }
}

