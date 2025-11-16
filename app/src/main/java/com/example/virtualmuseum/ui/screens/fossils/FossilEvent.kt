package com.example.virtualmuseum.ui.screens.fossils

sealed class FossilsEvent {
    data class OnSearchQueryChange(val query: String) : FossilsEvent()
    data class OnPeriodChange(val period: String) : FossilsEvent()
    data class OnOriginChange(val origin: String) : FossilsEvent()
    object OnSearchClick : FossilsEvent()
}