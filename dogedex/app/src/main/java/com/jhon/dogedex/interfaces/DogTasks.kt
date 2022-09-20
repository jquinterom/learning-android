package com.jhon.dogedex.interfaces

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.model.Dog

interface DogTasks {
    suspend fun getDogCollection(): ApiResponseStatus<List<Dog>>
    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any>
    suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog>
}