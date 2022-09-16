package com.jhon.dogedex.doglist

import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.DogsApi.retrofitService
import com.jhon.dogedex.api.dto.AddDogToUserDTO
import com.jhon.dogedex.api.dto.DogDTOMapper
import com.jhon.dogedex.api.makeNetworkCall
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DogRepository {

    suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
        return withContext(Dispatchers.IO) {
            val allDogsListResponseDeferred = async { downloadDogs() }
            val userDogsListResponseDeferred = async { getUserDogs() }

            val allDogsListResponse = allDogsListResponseDeferred.await()
            val userDogsListResponse = userDogsListResponseDeferred.await()

            when {
                allDogsListResponse is ApiResponseStatus.Error -> {
                    allDogsListResponse
                }
                userDogsListResponse is ApiResponseStatus.Error -> {
                    userDogsListResponse
                }
                allDogsListResponse is ApiResponseStatus.Success &&
                        userDogsListResponse is ApiResponseStatus.Success -> {
                    ApiResponseStatus.Success(
                        getCollectionList(
                            allDogsListResponse.data,
                            userDogsListResponse.data
                        )
                    )
                }
                else -> {
                    ApiResponseStatus.Error(R.string.unknown_error)
                }
            }
        }
    }

    private fun getCollectionList(allDogList: List<Dog>, userDogList: List<Dog>) =
        allDogList.map {
            if (userDogList.contains(it)) {
                it
            } else {
                Dog(
                    it.id, it.index, "", "", "", "", "",
                    "", "", "", "", inCollection = false
                )
            }
        }.sorted()

    private suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> =
        makeNetworkCall {
            val dogLisApiResponse = retrofitService.getAllDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }

    suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val defaultResponse = retrofitService.addDogToUser(addDogToUserDTO)

        if (!defaultResponse.isSuccess) {
            throw Exception(defaultResponse.message)
        }
    }

    private suspend fun getUserDogs(): ApiResponseStatus<List<Dog>> =
        makeNetworkCall {
            val dogLisApiResponse = retrofitService.getUserDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }

    suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> =
        makeNetworkCall {
            val response = retrofitService.getDogByMlId(mlDogId)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOToDogDomain(response.data.dog)
        }
}