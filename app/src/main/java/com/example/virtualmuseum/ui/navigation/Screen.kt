package com.example.virtualmuseum.ui.navigation

// Sử dụng sealed class để giới hạn các màn hình có thể có trong ứng dụng
sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Fossils : Screen("fossils_screen")
    object ScanQR : Screen("scan_qr_screen")
    object Map : Screen("map_screen")
    object Account : Screen("account_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object FossilDetail : Screen("fossil_detail_screen/{fossilId}") {
        fun createRoute(fossilId: String) = "fossil_detail_screen/$fossilId"
    }
    object ChangePassword : Screen("change_password_screen")
    object History : Screen("history_screen")
    object NewsDetail : Screen("news_detail_screen/{newsId}") {
        fun createRoute(newsId: Int) = "news_detail_screen/$newsId"
    }
}