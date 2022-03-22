package com.jhon.mvvmlearning.domain

import com.jhon.mvvmlearning.data.QuoteRepository
import com.jhon.mvvmlearning.data.model.QuoteModel

class GetQuotesUseCase {
    private val repository = QuoteRepository()

    suspend operator fun invoke(): List<QuoteModel>? = repository.getAllQuotes()

}