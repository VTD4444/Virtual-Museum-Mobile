package com.example.virtualmuseum.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

// Đây là nơi duy nhất lưu trữ trạng thái đăng nhập
object TokenManager {

    private val _token = MutableStateFlow<String?>(null) // null = chưa đăng nhập

    // UI có thể "lắng nghe" luồng này để biết khi nào trạng thái đăng nhập thay đổi
    val isLoggedInFlow = _token.map { it != null }

    fun saveToken(token: String) {
        _token.value = token
    }

    fun clearToken() {
        _token.value = null
    }

    fun getToken(): String? {
        return _token.value
    }
}