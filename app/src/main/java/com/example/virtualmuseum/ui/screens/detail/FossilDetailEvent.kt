package com.example.virtualmuseum.ui.screens.detail

sealed class FossilDetailEvent {
    data class OnCommentChange(val text: String) : FossilDetailEvent()
    object OnSubmitComment : FossilDetailEvent()
    object OnToggleFavoriteClick : FossilDetailEvent()
    object OnDismissLoginDialog : FossilDetailEvent()
}