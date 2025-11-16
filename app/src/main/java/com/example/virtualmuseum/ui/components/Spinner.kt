package com.example.virtualmuseum.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme

/**
 * Một Composable hiển thị một vòng xoay tải dữ liệu (loading indicator) ở giữa.
 * @param modifier Modifier để tùy chỉnh bố cục.
 */
@Composable
fun Spinner(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.primary, // Sử dụng màu vàng chính của theme
            strokeWidth = 4.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpinnerPreview() {
    VirtualMuseumTheme {
        // Để xem trước trên nền tối giống ứng dụng thật
        // Surface(color = MaterialTheme.colorScheme.background) {
        //    Spinner()
        // }

        // Hoặc xem trên nền sáng mặc định
        Spinner()
    }
}