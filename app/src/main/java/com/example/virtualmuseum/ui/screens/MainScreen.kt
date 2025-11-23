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
import androidx.navigation.NavGraph.Companion.findStartDestination

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

    // Hàm saveMapToDownloads
    fun saveMapToDownloads() {
        try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.map)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "museum_map_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val uri = context.contentResolver.insert(imageCollection, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(it, contentValues, null, null)
                }
                Toast.makeText(context, "Đã lưu bản đồ!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lưu bản đồ thất bại!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Hàm tiện ích để điều hướng trong BottomBar và Drawer
    fun navigateToTab(route: String) {
        innerNavController.navigate(route) {
            // 1. Xóa sạch backstack cho đến điểm bắt đầu (Home)
            // Để tránh chồng chất các màn hình khi chuyển tab liên tục
            popUpTo(innerNavController.graph.findStartDestination().id) {
                saveState = true
            }

            // 2. Tránh mở trùng lặp cùng một màn hình
            launchSingleTop = true

            // 3. QUAN TRỌNG: Đặt restoreState = false
            // Điều này buộc tab phải tải lại từ đầu (về trang gốc của tab),
            // thay vì khôi phục lại trạng thái cũ (ví dụ: trang chi tiết).
            restoreState = false
        }
    }

    // --- Cấu hình cho Bottom Bar ---
    val bottomNavItems = listOf(
        BottomNavItem(stringResource(id = R.string.bottom_nav_home), Icons.Default.Home, Screen.Home.route),
        BottomNavItem(stringResource(id = R.string.bottom_nav_explore), Icons.Default.Explore, Screen.Fossils.route),
        BottomNavItem(stringResource(id = R.string.home_scan_qr), Icons.Default.QrCodeScanner, Screen.ScanQR.route),
        BottomNavItem(stringResource(id = R.string.menu_account), Icons.Default.AccountCircle, Screen.Account.route)
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
                        // 2. CẤU HÌNH NAVIGATION CHO DRAWER (MENU)
                        onExploreClick = {
                            scope.launch { drawerState.close() }
                            navigateToTab(Screen.Fossils.route) // Điều hướng đến trang Fossils
                        },
                        onMapClick = {
                            scope.launch { drawerState.close() }
                            showMapDialog = true
                        },
                        onHistoryClick = {
                            scope.launch { drawerState.close() }
                            navigateToTab(Screen.History.route)
                        },
                        onAccountClick = {
                            scope.launch { drawerState.close() }
                            navigateToTab(Screen.Account.route)
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
                                navigateToTab(item.route)
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
                            innerNavController.navigate(Screen.FossilDetail.createRoute(fossilId))
                        },
                        // --- THÊM 2 DÒNG NÀY ---
                        onLoginClick = {
                            rootNavController.navigate(Screen.Login.route)
                        },
                        onRegisterClick = {
                            rootNavController.navigate(Screen.Register.route)
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