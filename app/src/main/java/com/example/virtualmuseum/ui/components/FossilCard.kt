// In file: ui/components/FossilCard.kt
package com.example.virtualmuseum.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // <-- Add import
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // <-- Add import
import androidx.compose.ui.text.font.FontWeight // <-- Add import
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.virtualmuseum.R // <-- Add import
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme

/**
 * Hiển thị một thẻ thông tin cho một hóa thạch.
 *
 * @param fossilName Tên của hóa thạch.
 * @param fossilOrigin Thông tin xuất xứ/mô tả ngắn. <-- NEW PARAMETER
 * @param fossilImageUrl URL của ảnh hóa thạch.
 * @param onClick Hàm được gọi khi người dùng nhấn vào thẻ.
 * @param modifier Modifier để tùy chỉnh.
 */
@Composable
fun FossilCard(
    modifier: Modifier = Modifier,
    fossilName: String,
    fossilOrigin: String, // <-- ADDED fossilOrigin
    fossilImageUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Phần hình ảnh (Keep existing AsyncImage code)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fossilImageUrl)
                    .crossfade(true)
                    .listener(onError = { request, result ->
                        Log.e("FossilCardCoil", "Error loading ${request.data}: ${result.throwable}")
                    })
                    .build(),
                placeholder = painterResource(id = R.drawable.placeholder_image), // Keep placeholder
                error = painterResource(id = R.drawable.error_image),       // Keep error
                contentDescription = "Ảnh của $fossilName",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // Adjust aspect ratio if needed, e.g., 4f / 3f
                contentScale = ContentScale.Crop
            )

            // --- Updated Text Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Add padding around text content
            ) {
                // Fossil Name
                Text(
                    text = fossilName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold, // Make name bold
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Fossil Origin/Short Description
                Text(
                    text = fossilOrigin,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), // Slightly dimmer color
                    maxLines = 3, // Limit lines for origin/description
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))

                // "See details" Text (aligned to the right)
                Text(
                    text = stringResource(id = R.string.feature_see_details), // Use string resource
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.End) // Align to the end (right)
                )
            }
        }
    }
}


@Preview
@Composable
fun FossilCardPreview() {
    VirtualMuseumTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FossilCard(
                fossilName = "Khủng Long Bạo Chúa",
                fossilOrigin = "Được tìm thấy ở Surrey, Anh (tầng đá sét Weald); cũng được phát hiện ở Tây Ban Nha; hóa thạch đầu ti",
                fossilImageUrl = "https://your-image-url.com/placeholder.jpg",
                onClick = {}
            )
        }
    }
}