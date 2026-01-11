package com.pegai.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Orders : Screen("orders")
    object Add : Screen("add")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")


    object Favorites : Screen("favorites")
    object MeusDados : Screen("meus_dados")
    object Settings : Screen("settings")
    object Support : Screen("support")

    object PublicProfile : Screen("public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }

    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: String) = "product_details/$productId"
    }

    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
}