package com.pegai.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.model.Product
import com.pegai.app.model.User

/**
 * Tela principal do Pegaí.
 * Exibe o cabeçalho do usuário, busca, filtros por categoria e a grade de produtos.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val produtos by viewModel.produtos.collectAsState()
    val usuarioLogado by viewModel.usuarioLogado.collectAsState()
    val categoriaSelecionada by viewModel.categoriaSelecionada.collectAsState()
    val categorias = viewModel.categorias

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        HomeHeader(
            user = usuarioLogado,
            onLoginClick = { viewModel.simularLogin() }
        )

        SearchBar()

        CategoryRow(
            categorias = categorias,
            selecionada = categoriaSelecionada,
            onCategoriaClick = { novaCategoria ->
                viewModel.selecionarCategoria(novaCategoria)
            }
        )

        // Grade de produtos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // Padding bottom extra para garantir que o último item não fique escondido atrás da BottomBar
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(produtos) { produto ->
                ProductCard(produto)
            }
        }
    }
}

/**
 * Card individual que exibe a foto (placeholder), preço, título e dono do produto.
 */
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Área da Imagem e Preço
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFF0F0F0))
            ) {
                IconButton(
                    onClick = { /* TODO: Implementar favoritar */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Color.Gray
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "R$ ${product.preco} / dia",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Área de Detalhes
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Dono: ${product.dono}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null, // Decorativo
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.nota.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Cabeçalho dinâmico. Mostra saudação se logado, ou botão de entrar se visitante.
 */
@Composable
fun HomeHeader(
    user: User?,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Saudação
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.nome.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Olá, ${user.nome.split(" ").first()} \uD83D\uDC4B",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "O que vamos alugar hoje?",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            } else {
                Column {
                    Text(
                        text = "Bem-vindo ao Pegaí",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Economize com materiais usados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // Ações (Notificação ou Botão Entrar)
        if (user != null) {
            IconButton(
                onClick = { /* TODO: Tela de Notificações */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = Color.Black
                )
            }
        } else {
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Entrar")
            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(bottom = 8.dp),
        placeholder = {
            Text(
                text = "O que você procura?",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = { /* TODO: Abrir Filtros */ }) {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Filtros",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}

@Composable
fun CategoryRow(
    categorias: List<String>,
    selecionada: String,
    onCategoriaClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        items(categorias) { categoria ->
            val isSelected = categoria == selecionada

            FilterChip(
                selected = isSelected,
                onClick = { onCategoriaClick(categoria) },
                label = { Text(categoria) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5),
                    labelColor = Color.Black
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
                shape = RoundedCornerShape(50)
            )
        }
    }
}