package com.jhon.dogedex.doglist

import com.jhon.dogedex.model.Dog
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.DogsApi.retrofitService
import com.jhon.dogedex.api.dto.DogDTOMapper
import com.jhon.dogedex.api.makeNetworkCall

class DogRepository {
    suspend fun downloadDogs(): ApiResponseStatus<List<Dog>> =
        makeNetworkCall {
            val dogLisApiResponse = retrofitService.getAllDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }
}