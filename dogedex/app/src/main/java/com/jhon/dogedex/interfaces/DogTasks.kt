package com.jhon.dogedex.interfaces

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.flow.Flow

interface DogTasks {
    suspend fun getDogCollection(): ApiResponseStatus<List<Dog>>
    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any>
    suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog>
    suspend fun getProbableDogs(probableDogsIds: ArrayList<String>): Flow<ApiResponseStatus<Dog>>
}