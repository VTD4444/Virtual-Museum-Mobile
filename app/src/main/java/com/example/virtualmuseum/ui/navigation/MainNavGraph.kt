package com.example.virtualmuseum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.virtualmuseum.ui.screens.account.AccountScreen
import com.example.virtualmuseum.ui.screens.account.ChangePasswordScreen
import com.example.virtualmuseum.ui.screens.account.HistoryScreen
import com.example.virtualmuseum.ui.screens.detail.FossilDetailScreen
import com.example.virtualmuseum.ui.screens.home.HomeScreen
import com.example.virtualmuseum.ui.screens.fossils.FossilsScreen
import com.example.virtualmuseum.ui.screens.news.NewsDetailScreen
import com.example.virtualmuseum.ui.screens.scan.ScanQRScreen

@Composable
fun MainNavGraph(
    innerNavController: NavHostController,
    modifier: Modifier = Modifier,
    onFossilClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    NavHost(
        navController = innerNavController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = innerNavController)
        }
        composable(Screen.Fossils.route) {
            FossilsScreen(
                navController = innerNavController,
            )
        }
        composable(Screen.ScanQR.route) {
            ScanQRScreen(navController = innerNavController)
        }

        composable(
            route = Screen.FossilDetail.route,
            arguments = listOf(navArgument("fossilId") { type = NavType.StringType })
        ) {
            FossilDetailScreen(navController = innerNavController)
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(navController = innerNavController)
        }

        composable(Screen.History.route) {
            // Truyền AccountViewModel nếu muốn dùng chung instance,
            // hoặc để nó tự tạo mới như mặc định trong HistoryScreen
            HistoryScreen(navController = innerNavController)
        }

        composable(Screen.Account.route) {
            AccountScreen(
                navController = innerNavController,
                onLoginClick = onLoginClick,       // <-- Truyền xuống
                onRegisterClick = onRegisterClick  // <-- Truyền xuống
            )
        }

        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(androidx.navigation.navArgument("newsId") { type = androidx.navigation.NavType.IntType })
        ) {
            NewsDetailScreen(navController = innerNavController)
        }
    }
}