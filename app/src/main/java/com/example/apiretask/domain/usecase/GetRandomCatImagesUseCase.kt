package com.example.apiretask.domain.usecase

import com.example.apiretask.data.CatImageDto
import com.example.apiretask.domain.repository.CatRepository
import com.example.apiretask.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomCatImagesUseCase @Inject constructor(
    private val repository: CatRepository
) {
    operator fun invoke(count: Int = 10): Flow<Resource<List<CatImageDto>>> {
        return repository.getRandomCatImages(count)
    }
}