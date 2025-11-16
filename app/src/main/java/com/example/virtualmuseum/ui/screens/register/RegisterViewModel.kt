package com.example.virtualmuseum.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.RegisterRequest
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    // Khởi tạo thủ công Repository
    private val apiService = RetrofitClient.instance
    private val repository: MuseumRepository = MuseumRepositoryImpl(apiService)

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnUsernameChange -> _state.update { it.copy(username = event.value) }
            is RegisterEvent.OnEmailChange -> _state.update { it.copy(email = event.value) }
            is RegisterEvent.OnPasswordChange -> _state.update { it.copy(password = event.value) }
            is RegisterEvent.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = event.value) }
            is RegisterEvent.OnRegisterClick -> register()
        }
    }

    private fun register() {
        val currentState = _state.value

        // Kiểm tra mật khẩu khớp
        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(registerError = "Mật khẩu không khớp") }
            return
        }

        val request = RegisterRequest(
            username = currentState.username.trim(),
            email = currentState.email.trim(),
            password = currentState.password
        )

        repository.register(request).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true, registerError = null) }
                }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            registerSuccess = true,
                            registerError = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            registerError = resource.message ?: "Đăng ký thất bại",
                            registerSuccess = false
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // Hàm để reset lỗi sau khi Toast đã hiển thị
    fun clearError() {
        _state.update { it.copy(registerError = null) }
    }
}