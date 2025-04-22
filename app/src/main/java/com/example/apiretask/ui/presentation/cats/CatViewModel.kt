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
        when (intent) {
            is CatIntent.LoadCatImages, is CatIntent.RefreshCatImages -> {
                getCatImages()
            }
        }
    }

    private fun getCatImages() {
        getRandomCatImagesUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        catImages = result.data ?: emptyList(),
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        // الاحتفاظ بالصور السابقة إذا كانت متوفرة في حالة الخطأ
                        catImages = result.data ?: _state.value.catImages,
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}