package com.example.virtualmuseum.ui.screens.detail

import com.example.virtualmuseum.data.remote.dto.CommentDto
import com.example.virtualmuseum.data.remote.dto.FossilDetailDto

data class FossilDetailState(
    val isLoading: Boolean = true,
    val fossil: FossilDetailDto? = null,
    val comments: List<CommentDto> = emptyList(),
    val error: String? = null,

    val newCommentText: String = "",
    val isPostingComment: Boolean = false,
    val isTogglingFavorite: Boolean = false,

    val showLoginDialog: Boolean = false
)