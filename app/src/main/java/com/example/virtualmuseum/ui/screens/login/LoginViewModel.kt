package com.example.virtualmuseum.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.auth.TokenManager
import com.example.virtualmuseum.data.remote.RetrofitClient // <-- Import object cũ
import com.example.virtualmuseum.data.remote.dto.LoginRequest
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    // Khởi tạo thủ công Repository
    private val apiService = RetrofitClient.instance
    private val repository: MuseumRepository = MuseumRepositoryImpl(apiService)

    // Trạng thái (State)
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow() // UI sẽ theo dõi state này

    // Hàm này được gọi khi người dùng thay đổi text field
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChange -> {
                _state.update { it.copy(username = event.value) }
            }
            is LoginEvent.OnPasswordChange -> {
                _state.update { it.copy(password = event.value) }
            }
            is LoginEvent.OnLoginClick -> {
                login()
            }
        }
    }

    private fun login() {
        val currentState = _state.value
        val request = LoginRequest(
            username = currentState.username.trim(),
            password = currentState.password.trim()
        )

        // Gọi Repository và xử lý kết quả bằng Flow
        repository.login(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, loginError = null) }
                }
                is Resource.Success -> {
                    // <-- LƯU TOKEN KHI ĐĂNG NHẬP THÀNH CÔNG
                    resource.data?.token?.let {
                        TokenManager.saveToken(it)
                    }

                    resource.data?.let { data ->
                        TokenManager.saveToken(data.token)
                        TokenManager.saveUserId(data.user.userId) // <-- LƯU USER ID
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            loginError = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginError = resource.message ?: "Lỗi không xác định",
                            loginSuccess = false
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}

// Lớp niêm phong (sealed class) định nghĩa các hành động người dùng
sealed class LoginEvent {
    data class OnUsernameChange(val value: String) : LoginEvent()
    data class OnPasswordChange(val value: String) : LoginEvent()
    object OnLoginClick : LoginEvent()
}