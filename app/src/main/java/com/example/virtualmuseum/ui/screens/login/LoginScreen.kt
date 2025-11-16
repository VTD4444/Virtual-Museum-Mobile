package com.example.virtualmuseum.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.virtualmuseum.ui.components.Spinner
import com.example.virtualmuseum.ui.navigation.Screen
import androidx.compose.ui.res.stringResource
import com.example.virtualmuseum.R

@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel() // Tự động lấy 1 instance của LoginViewModel
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    // Xử lý khi đăng nhập thành công hoặc thất bại (Side Effect)
    LaunchedEffect(key1 = state.loginError, key2 = state.loginSuccess) {
        if (state.loginError != null) {
            Toast.makeText(context, state.loginError, Toast.LENGTH_LONG).show()
        }
        if (state.loginSuccess) {
            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Home.route) {
                // Xóa mọi thứ khỏi backstack, để user không thể "back" về trang login
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Box này chỉ dùng để chứa Spinner
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.displayLarge
                )
                // ... phần còn lại của Column (TextFields, Buttons) ...
                Spacer(modifier = Modifier.height(32.dp))

                // Username
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { vm.onEvent(LoginEvent.OnUsernameChange(it)) },
                    label = { Text(stringResource(id = R.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { vm.onEvent(LoginEvent.OnPasswordChange(it)) },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Nút Login
                Button(
                    onClick = { vm.onEvent(LoginEvent.OnLoginClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading // Vô hiệu hóa nút khi đang tải
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text(stringResource(id = R.string.no_account))
                }
            }

            // Hiển thị vòng xoay loading
            if (state.isLoading) {
                Spinner(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}