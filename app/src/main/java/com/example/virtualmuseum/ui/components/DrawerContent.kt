package com.example.virtualmuseum.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.virtualmuseum.R
import com.example.virtualmuseum.ui.navigation.Screen

@Composable
fun DrawerContent(
    isLoggedIn: Boolean,
    navController: NavController,
    onExploreClick: () -> Unit,
    onMapClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    val currentLocaleTag = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi"

    // Hàm để thay đổi ngôn ngữ
    fun setLocale(lang: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
        // Dòng trên sẽ tự động lưu lựa chọn và khởi động lại Activity
        // để áp dụng ngôn ngữ mới.
    }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Nút đóng Menu
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Menu", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                IconButton(onClick = onCloseDrawer) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng Menu", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // --- CÁC MỤC MENU (HIỂN THỊ CÓ ĐIỀU KIỆN) ---
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.menu_explore)) },
                selected = false,
                onClick = onExploreClick,
                icon = { Icon(Icons.Default.Explore, null) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.menu_map)) },
                selected = false,
                onClick = onMapClick,
                icon = { Icon(Icons.Default.Map, null) }
            )

            // Chỉ hiển thị khi đã đăng nhập
            if (isLoggedIn) {
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_account)) },
                    selected = false,
                    onClick = onAccountClick, // <-- GỌI HÀM NÀY
                    icon = { Icon(Icons.Default.AccountCircle, null) }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_history)) },
                    selected = false,
                    onClick = onHistoryClick, // <-- GỌI HÀM NÀY
                    icon = { Icon(Icons.Default.History, null) }
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Đẩy các mục dưới cùng xuống

            // --- NÚT ĐĂNG NHẬP / ĐĂNG XUẤT (HIỂN THỊ CÓ ĐIỀU KIỆN) ---
            if (isLoggedIn) {
                // ĐÃ ĐĂNG NHẬP: Hiển thị nút Đăng xuất
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(id = R.string.menu_logout), color = MaterialTheme.colorScheme.onPrimary)
                }
            } else {
                // CHƯA ĐĂNG NHẬP: Hiển thị nút Đăng nhập & Đăng ký
                Button(
                    onClick = {
                        onCloseDrawer()
                        navController.navigate(Screen.Login.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(id = R.string.login), color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        onCloseDrawer()
                        navController.navigate(Screen.Register.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(id = R.string.register))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.surface)

            // Chọn ngôn ngữ
            Text(
                stringResource(id = R.string.menu_language),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = { setLocale("vi") },
                    colors = ButtonDefaults.buttonColors(
                        // Highlight nút nếu là ngôn ngữ đang chọn
                        containerColor = if (currentLocaleTag == "vi") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("VI", color = if (currentLocaleTag == "vi") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { setLocale("en") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentLocaleTag == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("EN", color = if (currentLocaleTag == "en") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}