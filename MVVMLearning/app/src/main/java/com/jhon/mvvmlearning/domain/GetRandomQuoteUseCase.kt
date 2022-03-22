package com.jhon.mvvmlearning.domain

import com.jhon.mvvmlearning.data.model.QuoteModel
import com.jhon.mvvmlearning.data.model.QuoteProvider

class GetRandomQuoteUseCase {
    operator fun invoke(): QuoteModel? {
        val quotes = QuoteProvider.quotes
        return if (!quotes.isNullOrEmpty()) {
            val randomNumber = (quotes.indices).random()
            quotes[randomNumber]
        } else {
            null
        }
    }
}