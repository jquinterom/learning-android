package com.jhon.mvvmlearning.data.network

import com.jhon.mvvmlearning.data.model.QuoteModel
import retrofit2.Response
import retrofit2.http.GET

interface QuoteApiClient {
    @GET("/.json")
    suspend fun getAllQuotes(): Response<List<QuoteModel>>
}