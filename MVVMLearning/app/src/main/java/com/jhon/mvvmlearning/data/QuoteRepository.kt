package com.jhon.mvvmlearning.data

import com.jhon.mvvmlearning.data.model.QuoteModel
import com.jhon.mvvmlearning.data.model.QuoteProvider
import com.jhon.mvvmlearning.data.network.QuoteService

class QuoteRepository {
    private val api = QuoteService()

    suspend fun getAllQuotes() : List<QuoteModel> {
        val response = api.getQuotes()
        QuoteProvider.quotes = response // Pequeña base de datos, guardamos como cache
        return response
    }
}