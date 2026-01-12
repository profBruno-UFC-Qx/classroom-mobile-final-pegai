package com.pegai.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pegai.app.model.Category
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel
import com.pegai.app.ui.viewmodel.favorites.FavoritesViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val usuarioLogado by authViewModel.usuarioLogado.collectAsState()
    val context = LocalContext.current
    val favState by favoritesViewModel.uiState.collectAsState()

    val dynamicGradient = brandGradient()

    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val concedido = permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) viewModel.obterLocalizacaoAtual(context)
    }

    LaunchedEffect(Unit) {
        val temPermissao =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (temPermissao) {
            viewModel.obterLocalizacaoAtual(context)
        } else {
            launcherPermissao.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(usuarioLogado?.uid) {
        usuarioLogado?.uid?.let { uid ->
            viewModel.definirUsuarioLogado(uid)
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
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Text("Explorar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                }

                items(uiState.produtos) { produto ->
                    val isFav = favState.favoriteIds.contains(produto.pid)

                    ProductCard(
                        product = produto,
                        isFavorite = isFav,
                        onClick = { navController.navigate("product_details/${produto.pid}") },
                        onToggleFavorite = {
                            if (usuarioLogado == null) {
                                navController.navigate("login")
                            } else {
                                favoritesViewModel.toggleFavorite(produto)
                            }
                        }
                    )
                }
            }
        }

        if (uiState.isMapModalVisible) {
            LocationFilterModal(
                radiusKm = uiState.radiusKm,
                userLat = uiState.userLat ?: -23.55052,
                userLng = uiState.userLng ?: -46.63330,
                onRadiusChange = { viewModel.updateRadius(it) },
                onDismiss = {
                    viewModel.limparFiltroLocalizacao()
                    viewModel.closeMapModal()
                },
                onApply = {
                    viewModel.filtroPorLocalizacao()
                    viewModel.closeMapModal()
                }
            )
        }
    }
}

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
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)) {
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
    onMapClick: () -> Unit
) {
    val headerButtonBg = MaterialTheme.colorScheme.surface
    val headerButtonContent = MaterialTheme.colorScheme.primary

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
                    if (user.fotoUrl.isNotEmpty()) {
                        AsyncImage(model = user.fotoUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
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
                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier
                        .size(45.dp)
                        .background(headerButtonBg, CircleShape) // Cor dinâmica do Surface
                ) {
                    Icon(Icons.Default.Map, null, tint = headerButtonContent) // Cor dinâmica Primary
                }
                Spacer(modifier = Modifier.width(8.dp))
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
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val dynamicGradient = brandGradient()

    Card(
        modifier = Modifier.fillMaxWidth().height(320.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                AsyncImage(model = product.imageUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                Surface(onClick = onToggleFavorite, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shadowElevation = 2.dp) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )                    }
                }

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
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        items(categorias) { categoriaNome ->
            val isSelected = categoriaNome == selecionada
            val icon = if (categoriaNome == "Todos") Icons.Default.GridView else Category.fromNome(categoriaNome).icon

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
        }
    }
}