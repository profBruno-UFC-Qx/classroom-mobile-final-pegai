package com.pegai.app

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pegai.app.ui.components.FloatingBottomBar
import com.pegai.app.ui.screens.add.AddScreen
import com.pegai.app.ui.screens.chat.ChatScreen
import com.pegai.app.ui.screens.favorites.FavoritesScreen
import com.pegai.app.ui.screens.home.HomeScreen
import com.pegai.app.ui.screens.login.LoginScreen
import com.pegai.app.ui.screens.orders.OrdersScreen
import com.pegai.app.ui.screens.profile.ProfileScreen
import com.pegai.app.ui.theme.PegaíTheme
import com.pegai.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        var isReady = false

        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            delay(2000L)
            isReady = true
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.WHITE,
                AndroidColor.WHITE
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            PegaíTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Exibe a bottom bar apenas se a rota atual não for "login"
                val showBottomBar = currentRoute != "login"

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            FloatingBottomBar(navController = navController)
                        }
                    },
                    containerColor = Color(0xFFF5F5F5)
                ) { paddingValues ->

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
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
