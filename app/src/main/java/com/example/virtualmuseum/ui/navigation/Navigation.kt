package com.example.virtualmuseum.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.example.virtualmuseum.ui.screens.MainScreen
import com.example.virtualmuseum.ui.screens.detail.FossilDetailScreen
import com.example.virtualmuseum.ui.screens.login.LoginScreen
import com.example.virtualmuseum.ui.screens.register.RegisterScreen
import com.example.virtualmuseum.ui.screens.fossils.FossilsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation() {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // MÀN HÌNH CHÍNH (chứa Scaffold, BottomBar, và NavGraph con)
        composable(route = Screen.Home.route) {
            MainScreen(rootNavController = navController)
        }
    }
}