package com.example.virtualmuseum.ui.screens.home

import androidx.appcompat.app.AppCompatDelegate
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

data class HomeState(
    val isLoading: Boolean = false,
    val newsList: List<NewsDto> = emptyList(),
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val repository: MuseumRepository = MuseumRepositoryImpl(RetrofitClient.instance)
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            val currentLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag() ?: "vi"

            repository.getNews(currentLanguage).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, newsList = resource.data ?: emptyList()) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                }
            }
        }
    }
}