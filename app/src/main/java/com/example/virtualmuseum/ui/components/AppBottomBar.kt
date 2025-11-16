package com.example.virtualmuseum.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home // <-- THÊM IMPORT NÀY
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource // <-- THÊM IMPORT NÀY
import androidx.compose.ui.tooling.preview.Preview
import com.example.virtualmuseum.R // <-- THÊM IMPORT NÀY (sẽ dùng ở bước 3)
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme

// Lớp đại diện cho một mục trên bottom bar
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface, // Màu nền của card
    ) {
        items.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary, // Màu vàng khi được chọn
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.background // Màu nền của indicator
                )
            )
        }
    }
}

@Preview
@Composable
fun AppBottomBarPreview() {
    // Cập nhật preview theo yêu cầu mới
    val items = listOf(
        BottomNavItem("Trang chủ", Icons.Default.Home, "route1"),
        BottomNavItem("Quét QR", Icons.Default.QrCodeScanner, "route2"),
        BottomNavItem("Khám phá", Icons.Default.Explore, "route3"),
        BottomNavItem("Tài khoản", Icons.Default.AccountCircle, "route4")
    )
    VirtualMuseumTheme {
        // Đặt currentRoute = "route1" để highlight mục "Trang chủ"
        AppBottomBar(items = items, currentRoute = "route1", onItemClick = {})
    }
}