package com.example.virtualmuseum.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme

/**
 * Hiển thị một dialog thông báo lỗi.
 *
 * @param title Tiêu đề của dialog (ví dụ: "Lỗi").
 * @param message Nội dung thông báo lỗi.
 * @param onDismiss Hàm được gọi khi người dùng nhấn nút "Đã hiểu" hoặc bấm ra ngoài.
 */
@Composable
fun ErrorPopup(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Đã hiểu",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary // Màu vàng
                )
            }
        },
        shape = RoundedCornerShape(16.dp), // Bo tròn các góc
        containerColor = MaterialTheme.colorScheme.surface, // Màu nền của card
        titleContentColor = MaterialTheme.colorScheme.onSurface, // Màu chữ tiêu đề
        textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // Màu chữ nội dung
    )
}

@Preview
@Composable
fun ErrorPopupPreview() {
    VirtualMuseumTheme {
        ErrorPopup(
            title = "Lỗi kết nối",
            message = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại đường truyền mạng của bạn.",
            onDismiss = {}
        )
    }
}