package com.jhon.dogedex.repositories

import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.interfaces.DogTasks
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.flow.Flow

class DogRepositories {

    companion object {
        const val nameFakeDog1 = "FakeDog1"
        const val nameFakeDog2 = "FakeDog2"
    }


    class FakeDogRepository : DogTasks {
        override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
            return ApiResponseStatus.Loading()
        }

        override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
            TODO("Not yet implemented")
        }

        override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
            TODO("Not yet implemented")
        }

        override suspend fun getProbableDogs(probableDogsIds: ArrayList<String>): Flow<ApiResponseStatus<Dog>> {
            TODO("Not yet implemented")
        }
    }

    class FakeDogRepositoryErrorGettingDogs : DogTasks {
        override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
            return ApiResponseStatus.Error(messageId = R.string.unknown_error)
        }

        override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
            TODO("Not yet implemented")
        }

        override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
            TODO("Not yet implemented")
        }

        override suspend fun getProbableDogs(probableDogsIds: ArrayList<String>): Flow<ApiResponseStatus<Dog>> {
            TODO("Not yet implemented")
        }
    }

    class FakeDogRepositoryShowIfSuccessGettingDogs : DogTasks {
        override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
            return ApiResponseStatus.Success(
                listOf(
                    Dog(
                        0, 1, nameFakeDog1, "", "", "", "",
                        "", "", "", "", inCollection = true
                    ),

                    Dog(
                        1, 2, nameFakeDog2, "", "", "", "",
                        "", "", "", "", inCollection = false
                    )
                )
            )
        }

        override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
            TODO("Not yet implemented")
        }

        override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
            TODO("Not yet implemented")
        }

        override suspend fun getProbableDogs(probableDogsIds: ArrayList<String>): Flow<ApiResponseStatus<Dog>> {
            TODO("Not yet implemented")
        }
    }
}