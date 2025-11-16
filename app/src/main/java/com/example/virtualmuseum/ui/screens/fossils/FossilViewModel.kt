package com.example.virtualmuseum.ui.screens.fossils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.SearchRequest
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FossilsViewModel : ViewModel() {

    // Khởi tạo thủ công
    private val repository: MuseumRepository = MuseumRepositoryImpl(RetrofitClient.instance)

    private val _state = MutableStateFlow(FossilsState())
    val state = _state.asStateFlow()

    init {
        // Tải danh sách mặc định khi màn hình vừa mở
        searchFossils()
    }

    fun onEvent(event: FossilsEvent) {
        when (event) {
            is FossilsEvent.OnSearchQueryChange -> _state.update { it.copy(searchQuery = event.query) }
            is FossilsEvent.OnPeriodChange -> _state.update { it.copy(period = event.period) }
            is FossilsEvent.OnOriginChange -> _state.update { it.copy(origin = event.origin) }
            is FossilsEvent.OnSearchClick -> searchFossils()
        }
    }

    private fun searchFossils() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val request = SearchRequest(
                q = _state.value.searchQuery.trim().ifEmpty { null },
                period = _state.value.period.trim().ifEmpty { null },
                origin = _state.value.origin.trim().ifEmpty { null }
            )

            repository.searchFossils(request).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                fossils = resource.data?.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: "Lỗi không xác định"
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}