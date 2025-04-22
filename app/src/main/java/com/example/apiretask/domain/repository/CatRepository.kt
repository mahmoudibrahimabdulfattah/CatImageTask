package com.example.apiretask.domain.repository

import com.example.apiretask.data.CatImageDto
import com.example.apiretask.util.Resource
import kotlinx.coroutines.flow.Flow

interface CatRepository {
    fun getRandomCatImages(count: Int = 10): Flow<Resource<List<CatImageDto>>>
}