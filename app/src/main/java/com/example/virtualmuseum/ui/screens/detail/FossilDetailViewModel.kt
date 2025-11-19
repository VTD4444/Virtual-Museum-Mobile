package com.example.virtualmuseum.ui.screens.detail

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.auth.TokenManager
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.AddReactionRequest
import com.example.virtualmuseum.data.remote.dto.CommentDto
import com.example.virtualmuseum.data.remote.dto.CreateCommentRequest
import com.example.virtualmuseum.data.remote.dto.DeleteCommentRequest
import com.example.virtualmuseum.data.remote.dto.DeleteReactionRequest
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
            // --- XỬ LÝ TRẢ LỜI ---
            is FossilDetailEvent.OnReplyClick -> {
                // Khi bấm trả lời, lưu comment cha và XÓA text cũ
                _state.update { it.copy(replyingTo = event.comment, newCommentText = "") }
            }
            is FossilDetailEvent.OnCancelReplyClick -> {
                // Khi hủy, xóa comment cha và XÓA text
                _state.update { it.copy(replyingTo = null, newCommentText = "") }
            }
            // --- XỬ LÝ XÓA ---
            is FossilDetailEvent.OnDeleteCommentClick -> {
                deleteComment(event.commentId)
            }
            // --- XỬ LÝ REACTION ---
            is FossilDetailEvent.OnReactionSelected -> {
                handleReaction(event.commentId, event.type)
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
        val fossilId = fossil.fossilId

        viewModelScope.launch {
            _state.update { it.copy(isPostingComment = true) }

            val request = CreateCommentRequest(
                fossilId = fossil.fossilId, // <-- Dùng trực tiếp fossilId (String)
                content = commentText,
                parentCommentId = _state.value.replyingTo?.commentId
            )

            repository.createComment(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val createdComment = resource.data

                        // --- KIỂM TRA IS_HIDDEN ---
                        if (createdComment != null && createdComment.isHidden) {
                            // TRƯỜNG HỢP BỊ ẨN (TOXIC)
                            _state.update {
                                it.copy(
                                    isPostingComment = false,
                                    // Hiển thị thông báo lỗi nhưng KHÔNG xóa text để user biết
                                    error = "Bình luận bị ẩn do chứa nội dung không phù hợp."
                                    // Hoặc dùng string resource nếu bạn có Context:
                                    // error = context.getString(R.string.comment_toxic_warning)
                                )
                            }
                        } else {
                            // TRƯỜNG HỢP BÌNH THƯỜNG
                            _state.update { it.copy(newCommentText = "", replyingTo = null, error = null) }

                            // Tải lại danh sách bình luận
                            loadComments(fossil.fossilId, AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi")
                        }
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

    private fun deleteComment(commentId: Int) {
        val fossil = _state.value.fossil ?: return

        viewModelScope.launch {
            _state.update { it.copy(isDeletingComment = true) }

            val request = DeleteCommentRequest(commentId = commentId)

            repository.deleteComment(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Tải lại danh sách sau khi xóa
                        loadComments(fossil.fossilId, AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi")
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(error = "Không thể xóa: ${resource.message}") }
                    }
                    is Resource.Loading -> {}
                }
                _state.update { it.copy(isDeletingComment = false) }
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

    private fun handleReaction(commentId: Int, newType: String) {
        // 1. Kiểm tra đăng nhập
        if (TokenManager.getToken() == null) {
            _state.update { it.copy(showLoginDialog = true) }
            return
        }

        val currentFossilId = _state.value.fossil?.fossilId ?: return

        // Tìm comment hiện tại để xem user đã react gì chưa
        // Lưu ý: Cần tìm trong cả list comment chính và list replies (đệ quy tìm kiếm hơi phức tạp)
        // Để đơn giản, ta sẽ gọi API và sau đó reload lại toàn bộ list comment
        // Trong thực tế, nên cập nhật UI lạc quan (optimistic update) để mượt mà hơn.

        // Ở đây tôi sẽ dùng cách đơn giản: Gọi API -> Thành công -> Reload list comments

        // Tuy nhiên, để biết nên gọi "Add" hay "Delete", ta cần biết trạng thái hiện tại.
        // Ta sẽ duyệt qua list để tìm comment đó.
        val targetComment = findCommentById(_state.value.comments, commentId) ?: return
        val currentReaction = targetComment.userReaction

        viewModelScope.launch {
            if (currentReaction == newType) {
                // Nếu bấm lại icon đang chọn -> Xóa reaction
                val request = DeleteReactionRequest(commentId = commentId)
                repository.deleteReaction(request).collect {
                    if (it is Resource.Success) {
                        // Reload comments
                        loadComments(currentFossilId, AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi")
                    }
                }
            } else {
                // Nếu chưa chọn hoặc chọn icon khác -> Thêm/Sửa reaction
                val request = AddReactionRequest(commentId = commentId, type = newType)
                repository.addOrUpdateReaction(request).collect {
                    if (it is Resource.Success) {
                        // Reload comments
                        loadComments(currentFossilId, AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi")
                    }
                }
            }
        }
    }

    // Hàm tiện ích để tìm comment trong cấu trúc cây
    private fun findCommentById(comments: List<CommentDto>, id: Int): CommentDto? {
        for (comment in comments) {
            if (comment.commentId == id) return comment
            val foundInReplies = findCommentById(comment.replies, id)
            if (foundInReplies != null) return foundInReplies
        }
        return null
    }
}