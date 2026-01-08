package com.pegai.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
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
import com.pegai.app.model.Category
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val usuarioLogado by authViewModel.usuarioLogado.collectAsState()
    val context = LocalContext.current

    val dynamicGradient = brandGradient()

    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val concedido = permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) viewModel.obterLocalizacaoAtual(context)
    }

    LaunchedEffect(Unit) {
        val temPermissao = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (temPermissao) {
            viewModel.obterLocalizacaoAtual(context)
        } else {
            launcherPermissao.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

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
                    onFavoritesClick = { }
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
                ProductCard(product = produto, onClick = { navController.navigate("product_details/${produto.pid}") })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
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

                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp).size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), shadowElevation = 2.dp) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.FavoriteBorder, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
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
                // Título e Nota
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(product.titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(String.format("%.1f", product.nota), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Linha do Dono
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
@Composable
fun HomeHeader(user: User?, localizacao: String, onLoginClick: () -> Unit, onFavoritesClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val headerButtonBg = if (isDark) Color.Black else Color.White
    val headerButtonContent = if (isDark) Color.White else Color(0xFF0E8FC6)

    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                    if (user.fotoUrl.isNotEmpty()) {
                        AsyncImage(model = user.fotoUrl, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Text(user.nome.first().toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0E8FC6))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Olá, ${user.nome.split(" ").first()} 👋", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(localizacao, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            } else {
                Column {
                    Text("Bem-vindo ao Pegaí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(localizacao, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                IconButton(onClick = onFavoritesClick, modifier = Modifier.padding(end = 8.dp).size(48.dp).background(headerButtonBg, CircleShape)) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = headerButtonContent)
                }
                IconButton(onClick = { }, modifier = Modifier.size(48.dp).background(headerButtonBg, CircleShape)) {
                    Icon(Icons.Outlined.Notifications, null, tint = headerButtonContent)
                }
            } else {
                Button(onClick = onLoginClick, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = headerButtonBg, contentColor = headerButtonContent), contentPadding = PaddingValues(horizontal = 24.dp)) {
                    Text("Entrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}