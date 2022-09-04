package com.jhon.dogedex.doglist

import com.jhon.dogedex.Dog
import com.jhon.dogedex.api.DogsApi.retrofitService
import com.jhon.dogedex.api.dto.DogDTOMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DogRepository {
    suspend fun downloadDogs(): List<Dog> {
        return withContext(Dispatchers.IO) {
            val dogLisApiResponse = retrofitService.getAllDogs()
            val dogDTOList = dogLisApiResponse.data.dogs
            val dogDTOMapper = DogDTOMapper()
            dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList)
        }
    }
}