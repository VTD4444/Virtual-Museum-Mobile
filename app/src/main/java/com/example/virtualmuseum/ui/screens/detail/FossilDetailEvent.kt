package com.example.virtualmuseum.ui.screens.detail

import com.example.virtualmuseum.data.remote.dto.CommentDto

sealed class FossilDetailEvent {
    data class OnCommentChange(val text: String) : FossilDetailEvent()
    object OnSubmitComment : FossilDetailEvent()
    object OnToggleFavoriteClick : FossilDetailEvent()
    object OnDismissLoginDialog : FossilDetailEvent()
    data class OnReplyClick(val comment: CommentDto) : FossilDetailEvent()
    object OnCancelReplyClick : FossilDetailEvent()
    data class OnDeleteCommentClick(val commentId: Int) : FossilDetailEvent()
    data class OnReactionSelected(val commentId: Int, val type: String) : FossilDetailEvent()
}