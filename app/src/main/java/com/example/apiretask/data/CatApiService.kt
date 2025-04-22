package com.example.apiretask.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {
    @GET("images/search")
    suspend fun getRandomCats(@Query("limit") limit: Int = 10): Response<List<CatImageDto>>
}