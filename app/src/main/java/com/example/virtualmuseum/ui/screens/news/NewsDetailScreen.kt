package com.example.virtualmuseum.ui.screens.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.virtualmuseum.R
import com.example.virtualmuseum.ui.components.Spinner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    navController: NavController,
    vm: NewsDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.news_detail_title)) },
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
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading) {
                Spinner(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else if (state.news != null) {
                val news = state.news!!

                // URL ảnh (dùng logic tương tự NewsItemCard)
                val baseUrl = "http://10.0.2.2:5000" // Hoặc IP máy thật của bạn
                val fullImageUrl = if (news.imageUrl.startsWith("http")) news.imageUrl else "$baseUrl${news.imageUrl}"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Ảnh bìa lớn
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fullImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Tiêu đề
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Ngày đăng
                        Text(
                            text = stringResource(id = R.string.news_published_on) + formatDate(news.publishDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Nội dung chi tiết
                        Text(
                            text = news.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                        )
                    }
                }
            } else {
                Text(
                    text = state.error ?: "Lỗi không xác định",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Hàm format ngày (tái sử dụng hoặc move vào Utils)
fun formatDate(dateString: String): String {
    return try {
        dateString.take(10)
    } catch (e: Exception) {
        dateString
    }
}