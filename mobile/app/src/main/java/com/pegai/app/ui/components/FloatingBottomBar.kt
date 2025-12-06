package com.pegai.app.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Modelo de dados para os itens da barra de navegação.
 */
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

/**
 * Uma barra de navegação inferior flutuante com estilo "cápsula".
 */
@Composable
fun FloatingBottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("Início", "home", Icons.Default.Home),
        BottomNavItem("Pedidos", "orders", Icons.Default.ShoppingCart),
        BottomNavItem("Favoritos", "favorites", Icons.Default.Favorite),
        BottomNavItem("Perfil", "profile", Icons.Default.Person),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
            .height(70.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(50)),
        shape = RoundedCornerShape(50),
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            // Remove o padding padrão do sistema para centralizar os ícones verticalmente na pílula
            windowInsets = WindowInsets(0.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Evita empilhar várias cópias da mesma tela
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF3D5AFE),
                        selectedTextColor = Color(0xFF3D5AFE),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFFF0F0F0) // Cinza bem claro para o destaque
                    )
                )
            }
        }
    }
}