package com.example.virtualmuseum.ui.screens.detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.virtualmuseum.R
import com.example.virtualmuseum.data.remote.dto.CommentDto
import com.example.virtualmuseum.data.remote.dto.FossilDetailDto
import com.example.virtualmuseum.ui.components.Spinner
import com.example.virtualmuseum.ui.components.WebViewComponent
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import com.example.virtualmuseum.ui.navigation.Screen

@Composable
fun FossilDetailScreen(
    navController: NavController,
    vm: FossilDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isLoading && state.fossil == null) {
            Spinner()
        } else if (state.fossil != null) {
            val currentFossil = state.fossil!!

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // SECTION 1: 3D MODEL + OVERLAY
                item(
                    // Provide a unique & stable key for this specific fossil
                    key = currentFossil.fossilId
                ) {
                    ModelSection(fossil = currentFossil)
                }

                // SECTION 2: GENERAL INFO
                item {
                    GeneralInfoSection(
                        fossil = currentFossil,
                        isTogglingFavorite = state.isTogglingFavorite, // <-- Truyền trạng thái
                        onEvent = vm::onEvent // <-- Truyền sự kiện
                    )
                }

                // SECTION 3: COMMENTS
                item {
                    CommentInputSection(state = state, onEvent = vm::onEvent)
                }

                // SECTION 4: COMMENT LIST
                items(
                    items = state.comments,
                    // Use comment_id as the key for each comment item
                    key = { comment -> comment.commentId }
                ) { comment ->
                    CommentItem(comment = comment, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không thể tải thông tin hóa thạch.")
            }
        }

        if (state.showLoginDialog) {
            LoginRequiredDialog(
                onDismiss = { vm.onEvent(FossilDetailEvent.OnDismissLoginDialog) },
                onLoginClick = {
                    vm.onEvent(FossilDetailEvent.OnDismissLoginDialog)
                    // Điều hướng đến trang Đăng nhập
                    navController.navigate(Screen.Login.route)
                }
            )
        }
    }
}

// --- SECTION 1: 3D MODEL COMPOSABLE ---
@Composable
private fun ModelSection(fossil: FossilDetailDto) {
    var showInfoOverlay by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Square aspect ratio
            .background(Color.DarkGray)
    ) {
        WebViewComponent(
            modifier = Modifier.fillMaxSize(),
            // Truyền trực tiếp đường dẫn model từ API
            modelPath = fossil.model3dUrl
        )

        // --- UI ELEMENTS OVER THE WEBVIEW ---

        // Overlay thông tin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            AnimatedVisibility(
                visible = showInfoOverlay,
                modifier = Modifier.padding(end = 8.dp), // Thêm padding
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                InfoOverlayContentTopRight(fossil)
            }

            // Nút bật/tắt
            IconButton(
                onClick = { showInfoOverlay = !showInfoOverlay },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(id = R.string.toggle_info),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Content of the Info Overlay (Top Right version)
@Composable
private fun InfoOverlayContentTopRight(fossil: FossilDetailDto) {
    Column(
        modifier = Modifier
            .background(
                color = Color.Black.copy(alpha = 0.6f), // Semi-transparent black
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.Start // Align text to the start within the column
    ) {
        InfoOverlayItem(label = stringResource(id = R.string.info_size), value = fossil.size)
        Spacer(modifier = Modifier.height(4.dp))
        InfoOverlayItem(label = stringResource(id = R.string.info_weight), value = fossil.weight)
        Spacer(modifier = Modifier.height(4.dp))
        InfoOverlayItem(label = stringResource(id = R.string.info_ability), value = fossil.specialAbility)
    }
}

// Helper for displaying info items in the overlay (no change)
@Composable
private fun InfoOverlayItem(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row {
        Text(
            text = "$label: ",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3 // Limit lines for overlay
        )
    }
}

// --- SECTION 2: GENERAL INFO COMPOSABLE ---
@Composable
private fun GeneralInfoSection(
    fossil: FossilDetailDto,
    isTogglingFavorite: Boolean,
    onEvent: (FossilDetailEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {

        // HÀNG 1: CHỨA TIÊU ĐỀ, LƯỢT THÍCH, VÀ NÚT YÊU THÍCH
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Căn giữa các item
        ) {
            // Tiêu đề (chiếm phần lớn không gian)
            Text(
                text = fossil.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f) // Cho phép text xuống dòng
            )

            // --- CỤM YÊU THÍCH (Ở BÊN PHẢI) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Hiển thị số lượt thích
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = stringResource(id = R.string.info_likes),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = fossil.liked.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp)) // Khoảng cách lớn hơn
                IconButton(
                    onClick = { onEvent(FossilDetailEvent.OnToggleFavoriteClick) },
                    enabled = !isTogglingFavorite
                ) {
                    if (isTogglingFavorite) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        val isFavorited = fossil.isFavorited == true
                        Icon(
                            imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(id = if (isFavorited) R.string.remove_from_favorites else R.string.add_to_favorites),
                            tint = if (isFavorited) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        // --- HẾT HÀNG 1 ---

        Spacer(modifier = Modifier.height(16.dp))

        // Ảnh (Full Width)
        AsyncImage(
            model = fossil.imageUrl,
            contentDescription = fossil.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Period và Origin
        InfoText(label = stringResource(id = R.string.period_placeholder), value = fossil.period)
        Spacer(modifier = Modifier.height(4.dp))
        InfoText(label = stringResource(id = R.string.origin_placeholder), value = fossil.origin)
        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = stringResource(id = R.string.description),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fossil.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

// Helper for displaying Period/Origin (no change)
@Composable
private fun InfoText(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// --- SECTION 3: COMMENT INPUT COMPOSABLE ---
@Composable
private fun CommentInputSection(
    state: FossilDetailState,
    onEvent: (FossilDetailEvent) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(
            text = stringResource(id = R.string.comments),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.newCommentText,
            onValueChange = { onEvent(FossilDetailEvent.OnCommentChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.comment_placeholder)) },
            trailingIcon = {
                if (state.isPostingComment) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    IconButton(
                        onClick = { onEvent(FossilDetailEvent.OnSubmitComment) },
                        enabled = state.newCommentText.isNotBlank() // Only enable if text exists
                    ) {
                        Icon(Icons.Default.Send, contentDescription = stringResource(id = R.string.post_comment))
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- SECTION 4: COMMENT ITEM COMPOSABLE (No change needed) ---
@Composable
private fun CommentItem(
    comment: CommentDto,
    modifier: Modifier = Modifier
) {
    // ... (Keep the existing CommentItem code)
    Column(modifier = modifier) {
        // Thông tin người đăng
        Text(
            text = comment.user.username,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Nội dung bình luận
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // TODO: Thêm nút Reactions và Reply (khi user yêu cầu)

        // Hiển thị các Replies (đệ quy)
        if (comment.replies.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp) // Thụt lề cho replies
            ) {
                comment.replies.forEach { reply ->
                    Spacer(modifier = Modifier.height(8.dp))
                    CommentItem(comment = reply) // Tái sử dụng Composable
                }
            }
        }
    }
}

@Composable
private fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yêu cầu đăng nhập") },
        text = { Text("Bạn cần đăng nhập để sử dụng tính năng này.") },
        confirmButton = {
            Button(onClick = onLoginClick) {
                Text("Đăng nhập")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}