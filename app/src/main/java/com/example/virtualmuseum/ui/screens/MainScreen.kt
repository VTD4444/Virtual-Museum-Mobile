package com.example.virtualmuseum.ui.screens

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.virtualmuseum.R
import com.example.virtualmuseum.ui.components.*
import com.example.virtualmuseum.ui.navigation.MainNavGraph
import com.example.virtualmuseum.ui.navigation.Screen
import kotlinx.coroutines.launch
import com.example.virtualmuseum.data.auth.TokenManager
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.res.stringResource

@Composable
fun MainScreen(
    rootNavController: NavController // NavController gốc từ MainActivity
) {
    // NavController nội bộ cho 5 tab (Home, Scan, History, Account)
    val innerNavController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMapDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoggedIn by TokenManager.isLoggedInFlow.collectAsState(initial = false)

    // Hàm saveMapToDownloads (giữ nguyên)
    fun saveMapToDownloads() { /* ... */ }

    // --- Cấu hình cho Bottom Bar ---
    val bottomNavItems = listOf(
        BottomNavItem(stringResource(id = R.string.bottom_nav_home), Icons.Default.Home, Screen.Home.route),
        BottomNavItem(stringResource(id = R.string.bottom_nav_explore), Icons.Default.Explore, Screen.History.route),
        BottomNavItem("Quét QR", Icons.Default.QrCodeScanner, Screen.ScanQR.route),
        BottomNavItem("Tài khoản", Icons.Default.AccountCircle, Screen.Account.route)
    )
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    DrawerContent(
                        isLoggedIn = isLoggedIn, // <-- Truyền trạng thái
                        navController = rootNavController, // <-- Truyền NavController gốc
                        onMapClick = {
                            scope.launch { drawerState.close() }
                            showMapDialog = true
                        },
                        onCloseDrawer = {
                            scope.launch { drawerState.close() }
                        },
                        onLogout = {
                            // Xử lý đăng xuất
                            TokenManager.clearToken()
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        AppHeader(
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    },
                    // --- ĐÂY LÀ FOOTER CỦA BẠN ---
                    bottomBar = {
                        AppBottomBar(
                            items = bottomNavItems,
                            currentRoute = currentRoute,
                            onItemClick = { item ->
                                if (currentRoute != item.route) {
                                    innerNavController.navigate(item.route) {
                                        popUpTo(innerNavController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background // Màu nền đen chính
                ) { innerPadding ->
                    // Nội dung chính là một NavHost cho các màn hình con
                    MainNavGraph(
                        innerNavController = innerNavController,
                        modifier = Modifier.padding(innerPadding),
                        onFossilClick = { fossilId ->
                            rootNavController.navigate(Screen.FossilDetail.createRoute(fossilId))
                        }
                    )
                }
            }
        }
    }

    if (showMapDialog) {
        MuseumMapDialog(
            onDismissRequest = { showMapDialog = false },
            onDownloadClick = {
                saveMapToDownloads()
                showMapDialog = false
            }
        )
    }
}