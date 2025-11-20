package com.example.virtualmuseum.ui.screens.account

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.auth.TokenManager
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.ChangePasswordRequest
import com.example.virtualmuseum.data.remote.dto.CommentHistoryDto
import com.example.virtualmuseum.data.remote.dto.FavoriteFossilDto
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,

    // Đổi mật khẩu
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val changePasswordSuccess: Boolean = false,

    val favorites: List<FavoriteFossilDto> = emptyList(),
    val commentHistory: List<CommentHistoryDto> = emptyList(),
    val isHistoryLoading: Boolean = false
)

class AccountViewModel : ViewModel() {

    private val repository: MuseumRepository = MuseumRepositoryImpl(RetrofitClient.instance)

    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    fun onOldPasswordChange(text: String) {
        _state.update { it.copy(oldPassword = text) }
    }

    fun onNewPasswordChange(text: String) {
        _state.update { it.copy(newPassword = text) }
    }

    fun onConfirmNewPasswordChange(text: String) {
        _state.update { it.copy(confirmNewPassword = text) }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null, changePasswordSuccess = false) }
    }

    fun logout() {
        TokenManager.clearToken()
    }

    fun changePassword() {
        val s = _state.value
        if (s.newPassword != s.confirmNewPassword) {
            _state.update { it.copy(error = "Mật khẩu xác nhận không khớp") }
            return
        }
        if (s.newPassword.length < 8) {
            _state.update { it.copy(error = "Mật khẩu mới phải có ít nhất 8 ký tự") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = ChangePasswordRequest(
                oldPassword = s.oldPassword,
                newPassword = s.newPassword
            )

            repository.changePassword(request).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Đổi mật khẩu thành công",
                                changePasswordSuccess = true,
                                oldPassword = "", newPassword = "", confirmNewPassword = ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(this)
        }
    }

    fun loadHistoryData() {
        viewModelScope.launch {
            _state.update { it.copy(isHistoryLoading = true, error = null) }

            val currentLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi"

            // 1. Tải danh sách yêu thích
            repository.getFavorites(language = currentLanguage).collect { resource ->
                if (resource is Resource.Success) {
                    _state.update { it.copy(favorites = resource.data ?: emptyList()) }
                }
                // Không block lỗi ở đây để tiếp tục tải comment
            }

            // 2. Tải lịch sử bình luận
            repository.getCommentHistory().collect { resource ->
                if (resource is Resource.Success) {
                    // --- LỌC BÌNH LUẬN BỊ ẨN ---
                    val allComments = resource.data ?: emptyList()
                    val visibleComments = allComments.filter { it.isHidden == false }

                    _state.update { it.copy(commentHistory = visibleComments) }
                }
            }

            _state.update { it.copy(isHistoryLoading = false) }
        }
    }
}