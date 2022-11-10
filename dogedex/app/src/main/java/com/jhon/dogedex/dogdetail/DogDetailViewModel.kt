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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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

    private var probableDogsIds = mutableStateListOf(
        savedStateHandle.get<ArrayList<String>>(DogDetailComposeActivity.MOST_PROBABLE_DOGS_IDS)
            ?: arrayListOf()
    )

    var isRecognition = mutableStateOf(
        savedStateHandle.get<Boolean>(DogDetailComposeActivity.IS_RECOGNITION_KEY) ?: false
    )
        private set

    var status = mutableStateOf<ApiResponseStatus<Any>?>(null)
        private set

    private var _probableDogList = MutableStateFlow<MutableList<Dog>>(mutableListOf())
    val probableDogList: StateFlow<MutableList<Dog>>
        get() = _probableDogList

    fun getProbableDogs() {
        _probableDogList.value.clear()
        viewModelScope.launch {
            dogRepository.getProbableDogs(probableDogsIds[0]).collect { apiResponseStatus ->
                if (apiResponseStatus is ApiResponseStatus.Success) {
                    val probableDogMutableList = _probableDogList.value.toMutableList()
                    probableDogMutableList.add(apiResponseStatus.data)
                    _probableDogList.value = probableDogMutableList
                }
            }
        }
    }

    fun updateDog(newDog: Dog){
        dog.value = newDog
    }

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