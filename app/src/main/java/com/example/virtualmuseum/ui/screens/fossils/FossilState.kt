package com.example.virtualmuseum.ui.screens.fossils

import com.example.virtualmuseum.data.remote.dto.FossilSearchResultDto

data class FossilsState(
    val isLoading: Boolean = false,
    val fossils: List<FossilSearchResultDto> = emptyList(),
    val error: String? = null,

    // Trạng thái cho các ô filter
    val searchQuery: String = "",
    val period: String = "",
    val origin: String = ""
)