package com.example.apiretask.data.repository

import com.example.apiretask.data.CatApiService
import com.example.apiretask.data.CatImageDto
import com.example.apiretask.domain.repository.CatRepository
import com.example.apiretask.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CatRepositoryImpl @Inject constructor(
    private val apiService: CatApiService
) : CatRepository {

    override fun getRandomCatImages(count: Int, page: Int?): Flow<Resource<List<CatImageDto>>> =
        flow {
            try {
                emit(Resource.Loading())

                val response = apiService.getRandomCats(limit = count, page = page)

                if (response.isSuccessful) {
                    val catDtos = response.body()
                    if (catDtos.isNullOrEmpty()) {
                        emit(Resource.Error("No cat images found"))
                    } else {
                        // Always append new images, do not limit
                        emit(Resource.Success(catDtos))
                    }
                } else {
                    emit(Resource.Error("Failed to fetch cat images: ${response.code()}"))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("API error: ${e.localizedMessage ?: "Unknown error"}"))
            } catch (e: IOException) {
                emit(Resource.Error("Network error. Please check your internet connection"))
            } catch (e: Exception) {
                emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }
}