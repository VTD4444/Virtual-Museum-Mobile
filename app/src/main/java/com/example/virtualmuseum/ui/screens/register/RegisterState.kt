package com.example.virtualmuseum.ui.screens.register

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val registerError: String? = null,
    val registerSuccess: Boolean = false
)