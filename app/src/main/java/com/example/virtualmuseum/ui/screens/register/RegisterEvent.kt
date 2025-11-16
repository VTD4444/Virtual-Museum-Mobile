package com.example.virtualmuseum.ui.screens.register

sealed class RegisterEvent {
    data class OnUsernameChange(val value: String) : RegisterEvent()
    data class OnEmailChange(val value: String) : RegisterEvent()
    data class OnPasswordChange(val value: String) : RegisterEvent()
    data class OnConfirmPasswordChange(val value: String) : RegisterEvent()
    object OnRegisterClick : RegisterEvent()
}