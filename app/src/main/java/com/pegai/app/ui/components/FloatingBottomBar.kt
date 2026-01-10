package com.pegai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
=======
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pegai.app.ui.navigation.Screen
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.chat.ChatListViewModel
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun FloatingBottomBar(
    navController: NavController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatListViewModel = viewModel()
) {
    // --- LÓGICA DE NOTIFICAÇÃO ---
    val currentUser by authViewModel.usuarioLogado.collectAsState()
    val chatState by chatViewModel.uiState.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { chatViewModel.carregarConversas(it.uid) }
    }

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

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
    val activeColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val dynamicGradient = brandGradient()
=======
    // Cor para os ícones selecionados
    val mainColor = Color(0xFF0E8FC6)

    // Definição do Degradê
    val buttonGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A5C8A), // Azul escuro
            Color(0xFF0E8FC6), // Azul médio
            Color(0xFF2ED1B2)  // Verde água
        )
    )
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight),
        contentAlignment = Alignment.TopCenter
    ) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
=======
        // Background Surface
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                                activeColor = activeColor,
                                unselectedColor = unselectedColor
=======
                                activeColor = mainColor
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(60.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rightItems.forEach { item ->
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                            val showBadge = item.route == Screen.Chat.route && chatState.hasUnreadMessages

=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                            CustomNavItem(
                                item = item,
                                currentRoute = currentRoute,
                                navController = navController,
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                                activeColor = activeColor,
                                unselectedColor = unselectedColor,
                                showBadge = showBadge
=======
                                activeColor = mainColor
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(navBarHeight))
            }
        }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
        Surface(
            modifier = Modifier
                .size(64.dp)
                .offset(y = (-20).dp),
            shadowElevation = 8.dp,
=======
        // Floating Action Button com Degradê
        Surface(
            modifier = Modifier
                .size(64.dp)
                .offset(y = 5.dp),
            shadowElevation = 2.dp,
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
                    .background(dynamicGradient),
=======
                    .background(buttonGradient),
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt
    activeColor: Color,
    unselectedColor: Color,
    showBadge: Boolean = false
) {
    val isSelected = currentRoute == item.route
=======
    activeColor: Color
) {
    val isSelected = currentRoute == item.route
    val selectedColor = activeColor
    val unselectedColor = Color.LightGray
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/components/FloatingBottomBar.kt

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
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) activeColor else unselectedColor,
                modifier = Modifier.size(24.dp)
            )

            // --- BOLINHA PULSANTE ---
            if (showBadge) {
                PulsingNotificationBadge(
                    modifier = Modifier.offset(x = 6.dp, y = (-4).dp),
                    size = 10.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else unselectedColor
        )
    }
}