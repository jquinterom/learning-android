package com.jhon.dogedex.dogdetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.interfaces.DogTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogDetailViewModel @Inject constructor(
    private val dogRepository: DogTasks
) : ViewModel() {
    var status = mutableStateOf<ApiResponseStatus<Any>?>(null)
        private set

    fun addDogToUser(dogId: Long) {
        viewModelScope.launch {
            status.value = ApiResponseStatus.Loading()
            handleAddDogToUserResponseStatus(dogRepository.addDogToUser(dogId))
        }
    }

    private fun handleAddDogToUserResponseStatus(apiResponseStatus: ApiResponseStatus<Any>) {
        status.value = apiResponseStatus
    }

    fun resetApiResponseStatus() {
        status.value = null
    }
}