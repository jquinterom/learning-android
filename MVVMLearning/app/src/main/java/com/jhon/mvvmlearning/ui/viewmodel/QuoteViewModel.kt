package com.jhon.mvvmlearning.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhon.mvvmlearning.data.model.QuoteModel
import com.jhon.mvvmlearning.data.model.QuoteProvider
import com.jhon.mvvmlearning.domain.GetQuotesUseCase
import com.jhon.mvvmlearning.domain.GetRandomQuoteUseCase
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {
    val quoteModel = MutableLiveData<QuoteModel>()
    val isLoading = MutableLiveData<Boolean>()

    var getQuotesUseCase = GetQuotesUseCase()
    var getRandomQuotesUseCase = GetRandomQuoteUseCase()

    fun onCreate() {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getQuotesUseCase()
            if (!result.isNullOrEmpty()) {
                quoteModel.postValue(result[0])
                isLoading.postValue(false)
            }
        }
    }

    fun randomQuote() {
        isLoading.postValue(true)
        val quote = getRandomQuotesUseCase()
        if (quote != null) {
            quoteModel.postValue(quote)
        }
        isLoading.postValue(false)
    }
}