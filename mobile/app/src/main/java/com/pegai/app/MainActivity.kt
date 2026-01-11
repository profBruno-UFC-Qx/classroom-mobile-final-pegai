package com.pegai.app

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pegai.app.data.utils.ThemeViewModel
import com.pegai.app.data.utils.ThemeViewModelFactory
import com.pegai.app.ui.components.FloatingBottomBar
import com.pegai.app.ui.screens.add.AddScreen
import com.pegai.app.ui.screens.chat.ChatListScreen
import com.pegai.app.ui.screens.favorites.FavoritesScreen
import com.pegai.app.ui.screens.home.HomeScreen
import com.pegai.app.ui.screens.login.LoginScreen
import com.pegai.app.ui.screens.orders.OrdersScreen
import com.pegai.app.ui.screens.profile.ProfileScreen
import com.pegai.app.ui.screens.register.RegisterScreen
import com.pegai.app.ui.theme.PegaiTheme
import com.pegai.app.ui.screens.details.ProductDetailsScreen
import com.pegai.app.ui.screens.chat.ChatDetailScreen
import com.pegai.app.ui.screens.profile.PublicProfileScreen
import com.pegai.app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory


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
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.WHITE, AndroidColor.WHITE)
        )

        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(applicationContext)
            )
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            PegaiTheme(darkTheme = isDarkTheme) {

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute != "login" &&
                        currentRoute != "register" &&
                        currentRoute?.startsWith("chat_detail") != true &&
                        currentRoute?.startsWith("public_profile") != true &&
                        currentRoute?.startsWith("product_details") != true

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            FloatingBottomBar(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                    ) {
                        composable("home") {
                            HomeScreen(navController = navController, authViewModel = authViewModel)
                        }

                        composable("login") {
                            LoginScreen(navController = navController, authViewModel = authViewModel)
                        }

                        composable("register") {
                            RegisterScreen(navController = navController)
                        }

                        composable("orders") {
                            OrdersScreen(navController = navController, authViewModel = authViewModel)
                        }

                        composable("add") {
                            AddScreen(navController = navController, authViewModel = authViewModel)
                        }

                        composable("favorites") { FavoritesScreen() }

                        composable("profile") {
                            ProfileScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                themeViewModel = themeViewModel
                            )
                        }

                        composable("chat") {
                            ChatListScreen(navController = navController, authViewModel = authViewModel)
                        }

                        composable(
                            route = "product_details/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId")
                            ProductDetailsScreen(navController = navController, productId = productId, authViewModel = authViewModel)
                        }

                        composable(
                            route = "public_profile/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            PublicProfileScreen(navController = navController, userId = userId)
                        }

                        composable(
                            route = "chat_detail/{chatId}",
                            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId")
                            ChatDetailScreen(navController = navController, chatId = chatId, authViewModel = authViewModel)
                        }
                    }
                }
            }
        }
    }
}