package com.example.virtualmuseum.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.virtualmuseum.R
import com.example.virtualmuseum.data.remote.dto.CommentHistoryDto
import com.example.virtualmuseum.data.remote.dto.FavoriteFossilDto
import com.example.virtualmuseum.ui.components.Spinner
import com.example.virtualmuseum.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    vm: AccountViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Yêu thích", "Bình luận")

    // Tải dữ liệu khi màn hình mở ra
    LaunchedEffect(Unit) {
        vm.loadHistoryData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử tương tác") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // --- TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Favorite else Icons.Default.Comment,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // --- CONTENT ---
            if (state.isHistoryLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Spinner()
                }
            } else {
                when (selectedTabIndex) {
                    0 -> FavoritesList(
                        favorites = state.favorites,
                        onItemClick = { fossilId ->
                            navController.navigate(Screen.FossilDetail.createRoute(fossilId))
                        }
                    )
                    1 -> CommentsList(
                        comments = state.commentHistory,
                        onItemClick = { fossilId ->
                            navController.navigate(Screen.FossilDetail.createRoute(fossilId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesList(
    favorites: List<FavoriteFossilDto>,
    onItemClick: (String) -> Unit
) {
    if (favorites.isEmpty()) {
        EmptyState("Chưa có mẫu vật yêu thích nào")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { item ->
                FavoriteItemCard(item, onItemClick)
            }
        }
    }
}

@Composable
fun CommentsList(
    comments: List<CommentHistoryDto>,
    onItemClick: (String) -> Unit
) {
    if (comments.isEmpty()) {
        EmptyState("Bạn chưa viết bình luận nào")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comments) { item ->
                CommentHistoryItemCard(item, onItemClick)
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: FavoriteFossilDto,
    onClick: (String) -> Unit
) {
    Card(
        onClick = { onClick(item.fossilId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Ảnh nhỏ bên trái
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_image),
                error = painterResource(id = R.drawable.error_image)
            )

            // Thông tin bên phải
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.origin,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CommentHistoryItemCard(
    item: CommentHistoryDto,
    onClick: (String) -> Unit
) {
    Card(
        onClick = { onClick(item.fossilId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đã bình luận tại: ${item.fossilId}", // API không trả về tên, dùng ID tạm
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatDate(item.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.HistoryEdu, // Hoặc icon khác
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, color = Color.Gray)
    }
}

// Hàm format ngày tháng đơn giản
fun formatDate(dateString: String): String {
    return try {
        // Giả sử format server là ISO 8601: "2025-09-06T03:00:35.115Z"
        // Cần điều chỉnh tùy theo format thực tế nếu cần
        dateString.take(10) + " " + dateString.substring(11, 16)
    } catch (e: Exception) {
        dateString
    }
}