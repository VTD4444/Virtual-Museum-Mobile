package com.example.virtualmuseum.ui.screens.news

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualmuseum.data.remote.RetrofitClient
import com.example.virtualmuseum.data.remote.dto.NewsDto
import com.example.virtualmuseum.data.repository.MuseumRepositoryImpl
import com.example.virtualmuseum.domain.repository.MuseumRepository
import com.example.virtualmuseum.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsDetailState(
    val isLoading: Boolean = true,
    val news: NewsDto? = null,
    val error: String? = null
)

class NewsDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: MuseumRepository = MuseumRepositoryImpl(RetrofitClient.instance)
    private val newsId: Int = savedStateHandle.get<Int>("newsId") ?: -1

    private val _state = MutableStateFlow(NewsDetailState())
    val state = _state.asStateFlow()

    init {
        loadNewsDetail()
    }

    private fun loadNewsDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi"

            // Vì chưa có API detail riêng, ta lấy danh sách và lọc
            repository.getNews(currentLanguage).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val foundNews = resource.data?.find { it.id == newsId }
                        if (foundNews != null) {
                            _state.update { it.copy(isLoading = false, news = foundNews) }
                        } else {
                            _state.update { it.copy(isLoading = false, error = "Không tìm thấy bài viết") }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}