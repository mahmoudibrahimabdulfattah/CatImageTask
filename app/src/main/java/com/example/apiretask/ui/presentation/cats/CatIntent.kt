package com.example.apiretask.ui.presentation.cats

sealed class CatIntent {
    data object LoadCatImages : CatIntent()
    data object RefreshCatImages : CatIntent()
    data object LoadMoreCatImages : CatIntent()
}