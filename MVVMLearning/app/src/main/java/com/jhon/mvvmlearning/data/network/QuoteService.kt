package com.jhon.mvvmlearning.data.network

import com.jhon.mvvmlearning.core.RetrofitHelper
import com.jhon.mvvmlearning.data.model.QuoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuoteService {
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun getQuotes(): List<QuoteModel> {
        // Ejecutamos en un hilo secundario, y asi evitamos saturar el hilo principal
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(QuoteApiClient::class.java).getAllQuotes()
            response.body() ?: emptyList()
        }
    }
}