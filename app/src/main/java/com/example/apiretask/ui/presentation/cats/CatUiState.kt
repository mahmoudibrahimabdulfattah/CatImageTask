package com.example.apiretask.ui.presentation.cats

import com.example.apiretask.data.CatImageDto

data class CatUiState(
    val isLoading: Boolean = false,
    val catImages: List<CatImageDto> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true
)