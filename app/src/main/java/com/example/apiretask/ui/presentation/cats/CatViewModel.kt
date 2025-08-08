package com.example.apiretask.ui.presentation.cats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apiretask.domain.usecase.GetRandomCatImagesUseCase
import com.example.apiretask.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class CatViewModel @Inject constructor(
    private val getRandomCatImagesUseCase: GetRandomCatImagesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CatUiState())
    val state: StateFlow<CatUiState> = _state.asStateFlow()

    init {
        processIntent(CatIntent.LoadCatImages)
    }

    fun processIntent(intent: CatIntent) {
        Log.d("CatViewModel", "Process intent: ${intent.javaClass.simpleName}")
        when (intent) {
            is CatIntent.LoadCatImages, is CatIntent.RefreshCatImages -> {
                loadInitialCatImages()
            }
            is CatIntent.LoadMoreCatImages -> {
                loadMoreCatImages()
            }
        }
    }

    private fun loadInitialCatImages() {
        _state.value =
            _state.value.copy(currentPage = 0, catImages = emptyList(), canLoadMore = true)
        getCatImages(page = 0, append = false)
    }

    private fun loadMoreCatImages() {
        if (_state.value.isLoading || _state.value.isLoadingMore || !_state.value.canLoadMore) return
        val nextPage = _state.value.currentPage + 1
        getCatImages(page = nextPage, append = true)
    }

    private fun getCatImages(page: Int = 0, append: Boolean = false) {
        val pageSize = 10
        Log.d("CatViewModel", "getCatImages page=$page append=$append")
        getRandomCatImagesUseCase(count = pageSize, page = page)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("CatViewModel", "Loading state; append: $append")
                        _state.value = _state.value.copy(
                            isLoading = !append,
                            isLoadingMore = append,
                            error = null
                        )
                    }

                    is Resource.Success -> {
                        Log.i(
                            "CatViewModel",
                            "Loaded ${result.data?.size ?: 0} cat images. Page: $page Append: $append"
                        )
                        val newImages = result.data ?: emptyList()
                        val allImages =
                            if (append) _state.value.catImages + newImages else newImages
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            catImages = allImages,
                            currentPage = page,
                            canLoadMore = true,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        Log.e("CatViewModel", "Error loading images: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}