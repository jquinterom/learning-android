package com.jhon.dogedex.doglist

import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.ApiService
import com.jhon.dogedex.api.dto.AddDogToUserDTO
import com.jhon.dogedex.api.dto.DogDTOMapper
import com.jhon.dogedex.api.makeNetworkCall
import com.jhon.dogedex.interfaces.DogTasks
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DogRepository @Inject constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) : DogTasks {

    override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
        return withContext(dispatcher) {
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
            val dogLisApiResponse = apiService.getAllDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }

    override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val defaultResponse = apiService.addDogToUser(addDogToUserDTO)

        if (!defaultResponse.isSuccess) {
            throw Exception(defaultResponse.message)
        }
    }

    private suspend fun getUserDogs(): ApiResponseStatus<List<Dog>> =
        makeNetworkCall {
            val dogLisApiResponse = apiService.getUserDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }

    override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> =
        makeNetworkCall {
            val response = apiService.getDogByMlId(mlDogId)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOToDogDomain(response.data.dog)
        }

    override suspend fun getProbableDogs(probableDogsIds: ArrayList<String>): Flow<ApiResponseStatus<Dog>> =
        flow {
            for (mlDogId in probableDogsIds) {
                val dog = getDogByMlId(mlDogId)
                emit(dog)
            }
        }.flowOn(dispatcher)
}