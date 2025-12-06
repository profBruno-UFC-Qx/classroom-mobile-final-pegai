package com.pegai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // Importante para o paddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // O componente que faltava
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pegai.app.ui.components.FloatingBottomBar
import com.pegai.app.ui.screens.favorites.FavoritesScreen
import com.pegai.app.ui.screens.home.HomeScreen
import com.pegai.app.ui.screens.orders.OrdersScreen
import com.pegai.app.ui.screens.profile.ProfileScreen
import com.pegai.app.ui.theme.PegaíTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch {
            delay(2000L)
            isReady = true
        }

        super.onCreate(savedInstanceState)

        setContent {
            PegaíTheme {
                // 1. Criar o controlador de navegação (Faltava isso no seu print)
                val navController = rememberNavController()

                // 2. O Scaffold é quem segura a barra lá embaixo (Faltava essa palavra)
                Scaffold(
                    bottomBar = {
                        FloatingBottomBar(navController = navController)
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->

                    // 3. O NavHost fica dentro do Scaffold
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        // O paddingValues garante que o conteúdo não fique escondido atrás da barra
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") { HomeScreen(navController = navController) }
                        composable("orders") { OrdersScreen() }
                        composable("favorites") { FavoritesScreen() }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}