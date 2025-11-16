package com.example.virtualmuseum.ui.screens.detail

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.auth.TokenManager
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.CommentDto
import com.example.virtualmuseum.data.remote.dto.CreateCommentRequest
import com.example.virtualmuseum.data.remote.dto.FavoriteRequest
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FossilDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: MuseumRepository = MuseumRepositoryImpl(RetrofitClient.instance)
    private val fossilIdString: String = savedStateHandle.get("fossilId")!!

    private val _state = MutableStateFlow(FossilDetailState())
    val state = _state.asStateFlow()

    init {
        loadAllData()
    }

    fun onEvent(event: FossilDetailEvent) {
        when (event) {
            is FossilDetailEvent.OnCommentChange -> {
                _state.update { it.copy(newCommentText = event.text) }
            }
            is FossilDetailEvent.OnSubmitComment -> {
                // Kiểm tra đăng nhập trước
                if (TokenManager.getToken() == null) {
                    _state.update { it.copy(showLoginDialog = true) }
                } else {
                    submitComment() // Chỉ gọi nếu đã đăng nhập
                }
            }
            is FossilDetailEvent.OnToggleFavoriteClick -> {
                // Kiểm tra đăng nhập trước
                if (TokenManager.getToken() == null) {
                    _state.update { it.copy(showLoginDialog = true) }
                } else {
                    toggleFavorite() // Chỉ gọi nếu đã đăng nhập
                }
            }
            is FossilDetailEvent.OnDismissLoginDialog -> {
                _state.update { it.copy(showLoginDialog = false) }
            }
        }
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi"

            // 1. Load fossil details using the String ID
            repository.getFossilDetail(fossilIdString, currentLanguage).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val fossil = resource.data?.fossil
                        _state.update { it.copy(fossil = fossil) }

                        // --- ADJUST COMMENT LOADING ---
                        // Check if fossil details loaded successfully
                        if (fossil != null) {
                            // Directly use the String ID for loading comments
                            loadComments(fossil.fossilId, currentLanguage) // <-- Use fossil.fossilId (String)
                        } else {
                            // Handle case where fossil detail failed but request was 'successful'
                            _state.update { it.copy(isLoading = false, error = "Failed to parse fossil details.") }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun loadComments(fossilId: String, language: String) {
        repository.getComments(fossilId, language = language).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Lọc danh sách một cách đệ quy
                    val visibleComments = resource.data
                        ?.filter { it.isHidden == false } // Lọc các bình luận cha
                        ?.map { it.copy(replies = filterHiddenReplies(it.replies)) } // Lọc các bình luận con
                        ?: emptyList()

                    _state.update { it.copy(isLoading = false, comments = visibleComments, error = null) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = resource.message) }
                }
                is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun filterHiddenReplies(comments: List<CommentDto>): List<CommentDto> {
        return comments
            .filter { it.isHidden == false } // Lọc ở cấp độ này
            .map { it.copy(replies = filterHiddenReplies(it.replies)) } // Lọc đệ quy các cấp con
    }

    private fun submitComment() {
        // ... (Keep the existing submitComment code for now, assuming POST needs Int)
        // You will need to verify if POST /comments/create-comment needs String or Int
        val fossil = _state.value.fossil ?: return
        val commentText = _state.value.newCommentText.trim()
        if (commentText.isEmpty()) return

        val request = CreateCommentRequest(
            fossilId = fossil.fossilId, // <-- Dùng trực tiếp fossilId (String)
            content = commentText,
            parentCommentId = null
        )

        viewModelScope.launch {
            _state.update { it.copy(isPostingComment = true) }

            repository.createComment(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update { it.copy(newCommentText = "", error = null) }
                        // Tải lại comment
                        loadComments(fossil.fossilId, AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi")
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(error = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
                _state.update { it.copy(isPostingComment = false) }
            }
        }
    }

    private fun toggleFavorite() {
        if (TokenManager.getToken() == null) {
            _state.update { it.copy(error = "Vui lòng đăng nhập để thực hiện") }
            return
        }

        val currentFossil = _state.value.fossil ?: return
        val isCurrentlyFavorited = currentFossil.isFavorited == true
        val request = FavoriteRequest(fossilId = currentFossil.fossilId)

        viewModelScope.launch {
            _state.update { it.copy(isTogglingFavorite = true) }

            val repositoryCall = if (isCurrentlyFavorited) {
                repository.removeFavorite(request)
            } else {
                repository.addFavorite(request)
            }

            repositoryCall.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // --- PHẦN CẬP NHẬT LOGIC ---
                        _state.update {
                            val fossil = it.fossil ?: return@update it // Lấy fossil an toàn

                            // Tính toán số lượt thích mới
                            val newLikedCount = if (isCurrentlyFavorited) {
                                (fossil.liked - 1).coerceAtLeast(0) // Trừ 1 (không bao giờ < 0)
                            } else {
                                fossil.liked + 1 // Cộng 1
                            }

                            it.copy(
                                isTogglingFavorite = false,
                                // Cập nhật cả isFavorited và liked trong state
                                fossil = fossil.copy(
                                    isFavorited = !isCurrentlyFavorited,
                                    liked = newLikedCount
                                ),
                                error = null
                            )
                        }
                        // --- HẾT PHẦN CẬP NHẬT ---
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isTogglingFavorite = false, error = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}