package com.pegai.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Orders : Screen("orders")
    object Add : Screen("add")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/navigation/AppRoutes.kt
    object Login : Screen("login")
    object Register : Screen("register")

=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/navigation/AppRoutes.kt
    object PublicProfile : Screen("public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }

    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: String) = "product_details/$productId"
    }
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/navigation/AppRoutes.kt

    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
}
=======
    object Login : Screen("login")
    object Register : Screen("register")
    object MyData : Screen("meus_dados")
object Settings : Screen("configuracoes")}

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/navigation/AppRoutes.kt
