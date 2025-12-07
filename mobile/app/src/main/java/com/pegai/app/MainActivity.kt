package com.pegai.app

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pegai.app.ui.components.FloatingBottomBar
import com.pegai.app.ui.screens.add.AddScreen
import com.pegai.app.ui.screens.chat.ChatScreen
import com.pegai.app.ui.screens.favorites.FavoritesScreen
import com.pegai.app.ui.screens.home.HomeScreen
import com.pegai.app.ui.screens.orders.OrdersScreen
import com.pegai.app.ui.screens.profile.ProfileScreen
import com.pegai.app.ui.theme.PegaíTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ponto de entrada da aplicação.
 * Configura o tema, a navegação principal e a barra de status/navegação.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isReady = false

        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            delay(2000L)
            isReady = true
        }

        // Configuração para ocupar a tela inteira (atrás das barras de sistema)
        enableEdgeToEdge(
            // Barra de Status (Topo): Transparente para o conteúdo fluir
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),

            // Barra de Navegação (Baixo): Forçamos branco para fundir com o fundo da tela,
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.WHITE,
                AndroidColor.WHITE
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            PegaíTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        FloatingBottomBar(navController = navController)
                    },
                    // Cor de fundo cinza claro (Light Gray) para toda a aplicação.
                    containerColor = Color(0xFFF5F5F5)
                ) { paddingValues ->

                    // Configuração das Rotas de Navegação
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") { HomeScreen(navController = navController) }

                        // Telas Secundárias
                        composable("orders") { OrdersScreen() }
                        composable("add") { AddScreen() }
                        composable("favorites") { FavoritesScreen() }
                        composable("profile") { ProfileScreen() }
                        composable("chat") { ChatScreen() }
                    }
                }
            }
        }
    }
}