package com.example.virtualmuseum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.virtualmuseum.ui.screens.detail.FossilDetailScreen
import com.example.virtualmuseum.ui.screens.home.HomeScreen
import com.example.virtualmuseum.ui.screens.fossils.FossilsScreen

@Composable
fun MainNavGraph(
    innerNavController: NavHostController,
    modifier: Modifier = Modifier,
    onFossilClick: (String) -> Unit
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
                onFossilClick = onFossilClick
            )
        }
        composable(Screen.ScanQR.route) {
            // TODO: Tạo ScanQRScreen()
        }
        composable(Screen.History.route) {
            // TODO: Tạo HistoryScreen()
        }
        composable(Screen.Account.route) {
            // TODO: Tạo AccountScreen()
        }
        // Thêm màn hình chi tiết
        composable(
            route = Screen.FossilDetail.route,
            arguments = listOf(navArgument("fossilId") { type = NavType.StringType })
        ) {
            FossilDetailScreen(navController = innerNavController)
        }
    }
}