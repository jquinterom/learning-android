package com.jhon.dogedex.main

import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.interfaces.ClassifierTasks
import com.jhon.dogedex.interfaces.DogTasks
import com.jhon.dogedex.machinelearning.Classifier
import com.jhon.dogedex.machinelearning.ClassifierRepository
import com.jhon.dogedex.machinelearning.DogRecognition
import com.jhon.dogedex.model.Dog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.nio.MappedByteBuffer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dogRepository: DogTasks,
    private val classifierRepository: ClassifierTasks
) : ViewModel() {

    private val _dog = MutableLiveData<Dog?>()
    val dog: LiveData<Dog?>
        get() = _dog

    private val _status = MutableLiveData<ApiResponseStatus<Dog>>()
    val status: LiveData<ApiResponseStatus<Dog>>
        get() = _status

    private val _dogRecognition = MutableLiveData<DogRecognition>()
    val dogRecognition: LiveData<DogRecognition>
        get() = _dogRecognition

    val probableDogIds = mutableListOf<String>()

    fun recognizeImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            val dogRecognitionList = classifierRepository.recognizeImage(imageProxy)
            updateDogRecognition(dogRecognitionList)
            updateProbableDogIds(dogRecognitionList)
            imageProxy.close()
        }
    }

    private fun updateProbableDogIds(dogRecognitionList: List<DogRecognition>) {
        probableDogIds.clear()
        if(dogRecognitionList.size >= 5) {
            val recognitionListId = dogRecognitionList.subList(1, 4).map {
                it.id
            }
            probableDogIds.addAll(recognitionListId)
        }
    }

    private fun updateDogRecognition(dogRecognitionList: List<DogRecognition>) {
        _dogRecognition.value = dogRecognitionList.first()
    }

    fun getDogByMlId(mlDogId: String) {
        viewModelScope.launch {
            handleResponseStatus(dogRepository.getDogByMlId(mlDogId))
        }
    }

    private fun handleResponseStatus(apiResponseStatus: ApiResponseStatus<Dog>) {
        if (apiResponseStatus is ApiResponseStatus.Success) {
            _dog.value = apiResponseStatus.data
        }

        _status.value = apiResponseStatus
    }
}