package com.example.virtualmuseum.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.virtualmuseum.R
import com.example.virtualmuseum.data.remote.dto.NewsDto

@Composable
fun NewsItemCard(
    news: NewsDto,
    onClick: () -> Unit
) {
    // Cấu hình Base URL cho ảnh (thay IP máy tính của bạn vào đây)
    val baseUrl = "http://192.168.1.12:5000"
    val fullImageUrl = if (news.imageUrl.startsWith("http")) news.imageUrl else "$baseUrl${news.imageUrl}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
    ) {
        Column {
            // Ảnh bìa
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fullImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.placeholder_image),
                error = androidx.compose.ui.res.painterResource(id = R.drawable.error_image)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Ngày tháng
                Text(
                    text = news.publishDate.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Tiêu đề
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Mô tả ngắn
                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}