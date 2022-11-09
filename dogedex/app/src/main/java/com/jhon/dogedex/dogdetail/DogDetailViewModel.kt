package com.jhon.dogedex.dogdetail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.interfaces.DogTasks
import com.jhon.dogedex.model.Dog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogDetailViewModel @Inject constructor(
    private val dogRepository: DogTasks,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var dog: MutableState<Dog?> = mutableStateOf(
        savedStateHandle[DogDetailComposeActivity.DOG_KEY]
    )
        private set

    var probableDogsIds = mutableStateListOf(
        savedStateHandle.get<ArrayList<String>>(DogDetailComposeActivity.MOST_PROBABLE_DOGS_IDS)
    )
        private set

    var isRecognition = mutableStateOf(
        savedStateHandle.get<Boolean>(DogDetailComposeActivity.MOST_PROBABLE_DOGS_IDS) ?: false
    )
        private set

    var status = mutableStateOf<ApiResponseStatus<Any>?>(null)
        private set

    fun addDogToUser() {
        viewModelScope.launch {
            status.value = ApiResponseStatus.Loading()
            handleAddDogToUserResponseStatus(dogRepository.addDogToUser(dog.value!!.id))
        }
    }

    private fun handleAddDogToUserResponseStatus(apiResponseStatus: ApiResponseStatus<Any>) {
        status.value = apiResponseStatus
    }

    fun resetApiResponseStatus() {
        status.value = null
    }
}