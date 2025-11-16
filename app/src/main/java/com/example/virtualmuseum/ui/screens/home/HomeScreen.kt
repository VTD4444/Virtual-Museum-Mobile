// In file: ui/screens/home/HomeScreen.kt
package com.example.virtualmuseum.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.virtualmuseum.R
import com.example.virtualmuseum.ui.components.FeatureCard
import com.example.virtualmuseum.ui.components.InstructionDialog
import com.example.virtualmuseum.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController
) {
    // --- State để quản lý cả 3 dialog ---
    var showQrDialog by remember { mutableStateOf(false) }
    var showSocialDialog by remember { mutableStateOf(false) }
    var showExploreDialog by remember { mutableStateOf(false) }

    // Box chứa nền và nội dung
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // LỚP 1: ẢNH NỀN
        Image(
            painter = painterResource(id = R.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f),
            contentScale = ContentScale.Crop
        )

        // LỚP 2: NỘI DUNG CUỘN ĐƯỢC
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Tiêu đề
            val titleBrush = Brush.linearGradient(
                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = stringResource(id = R.string.home_title),
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = titleBrush,
                    textAlign = TextAlign.Center
                )
            )

            // Dấu gạch ngang
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(4.dp)
                    .background(titleBrush, shape = RoundedCornerShape(2.dp))
            )

            // Phụ đề
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Mũi tên xuống
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Scroll down",
                tint = MaterialTheme.colorScheme.primary
            )

            // Nút "Quét mã QR"
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showQrDialog = true }, // <-- Mở dialog QR
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(titleBrush, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.home_scan_qr),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Nút "Khám phá bộ sưu tập"
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.navigate(Screen.Fossils.route) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            ) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(id = R.string.home_explore),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            // --- Phần Tính năng nổi bật ---
            Text(
                text = stringResource(id = R.string.home_features_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.home_features_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Card 1: Quét mã QR
            FeatureCard(
                icon = Icons.Outlined.QrCodeScanner,
                title = stringResource(id = R.string.feature_qr_title),
                description = stringResource(id = R.string.feature_qr_desc),
                onClick = { showQrDialog = true } // <-- Mở dialog QR
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Tương tác xã hội
            FeatureCard(
                icon = Icons.Outlined.Forum,
                title = stringResource(id = R.string.feature_social_title),
                description = stringResource(id = R.string.feature_social_desc),
                onClick = { showSocialDialog = true } // <-- Mở dialog Tương tác
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Khám phá bộ sưu tập
            FeatureCard(
                icon = Icons.Outlined.Search,
                title = stringResource(id = R.string.feature_explore_title),
                description = stringResource(id = R.string.feature_explore_desc),
                onClick = { showExploreDialog = true } // <-- Mở dialog Khám phá
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // --- HIỂN THỊ CÁC DIALOG (ĐẶT Ở CUỐI CÙNG) ---

    // 1. Dialog Quét QR
    if (showQrDialog) {
        val steps = listOf(
            stringResource(id = R.string.qr_instruction_step1),
            stringResource(id = R.string.qr_instruction_step2),
            stringResource(id = R.string.qr_instruction_step3),
            stringResource(id = R.string.qr_instruction_step4)
        )
        InstructionDialog(
            title = stringResource(id = R.string.qr_instruction_title),
            description = stringResource(id = R.string.qr_instruction_desc),
            steps = steps,
            confirmButtonText = stringResource(id = R.string.dialog_open_scanner),
            onConfirm = {
                showQrDialog = false
                navController.navigate(Screen.ScanQR.route)
            },
            onDismiss = {
                showQrDialog = false
            }
        )
    }

    // 2. Dialog Tương tác Xã hội
    if (showSocialDialog) {
        val steps = listOf(
            stringResource(id = R.string.social_instruction_step1),
            stringResource(id = R.string.social_instruction_step2),
            stringResource(id = R.string.social_instruction_step3),
            stringResource(id = R.string.social_instruction_step4)
        )
        InstructionDialog(
            title = stringResource(id = R.string.social_instruction_title),
            description = stringResource(id = R.string.social_instruction_desc),
            steps = steps,
            confirmButtonText = stringResource(id = R.string.dialog_got_it),
            onConfirm = {
                showSocialDialog = false
                // Có thể điều hướng đến trang Khám phá để họ bắt đầu tương tác
                navController.navigate(Screen.Fossils.route)
            },
            onDismiss = {
                showSocialDialog = false
            }
        )
    }

    // 3. Dialog Khám phá Bộ sưu tập
    if (showExploreDialog) {
        val steps = listOf(
            stringResource(id = R.string.explore_instruction_step1),
            stringResource(id = R.string.explore_instruction_step2),
            stringResource(id = R.string.explore_instruction_step3),
            stringResource(id = R.string.explore_instruction_step4)
        )
        InstructionDialog(
            title = stringResource(id = R.string.explore_instruction_title),
            description = stringResource(id = R.string.explore_instruction_desc),
            steps = steps,
            confirmButtonText = stringResource(id = R.string.dialog_open_explorer),
            onConfirm = {
                showExploreDialog = false
                navController.navigate(Screen.Fossils.route)
            },
            onDismiss = {
                showExploreDialog = false
            }
        )
    }
}