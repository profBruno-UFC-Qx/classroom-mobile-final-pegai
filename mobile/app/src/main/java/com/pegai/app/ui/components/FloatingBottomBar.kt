package com.pegai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.theme.brandGradient // Importado do seu tema

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun FloatingBottomBar(navController: NavController) {

    val leftItems = listOf(
        BottomNavItem("Início", Screen.Home.route, Icons.Default.Home),
        BottomNavItem("Aluguéis", Screen.Orders.route, Icons.Default.DateRange),
    )

    val rightItems = listOf(
        BottomNavItem("Chat", Screen.Chat.route, Icons.Default.Email),
        BottomNavItem("Perfil", Screen.Profile.route, Icons.Default.Person),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalHeight = 80.dp + navBarHeight

    // Cores dinâmicas do Tema
    val activeColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val dynamicGradient = brandGradient()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background Surface Dinâmica
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface, // Muda para cinza escuro no Dark Mode
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 16.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        leftItems.forEach { item ->
                            CustomNavItem(
                                item = item,
                                currentRoute = currentRoute,
                                navController = navController,
                                activeColor = activeColor,
                                unselectedColor = unselectedColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(60.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rightItems.forEach { item ->
                            CustomNavItem(
                                item = item,
                                currentRoute = currentRoute,
                                navController = navController,
                                activeColor = activeColor,
                                unselectedColor = unselectedColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(navBarHeight))
            }
        }

        // Floating Action Button com Gradiente Dinâmico
        Surface(
            modifier = Modifier
                .size(64.dp)
                .offset(y = (-20).dp),
            shadowElevation = 8.dp,
            shape = CircleShape,
            color = Color.Transparent,
            onClick = {
                navController.navigate(Screen.Add.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dynamicGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Anunciar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun CustomNavItem(
    item: BottomNavItem,
    currentRoute: String?,
    navController: NavController,
    activeColor: Color,
    unselectedColor: Color
) {
    val isSelected = currentRoute == item.route

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) activeColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else unselectedColor
        )
    }
}