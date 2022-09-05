package com.jhon.dogedex.doglist

import com.jhon.dogedex.Dog
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.DogsApi.retrofitService
import com.jhon.dogedex.api.dto.DogDTOMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class DogRepository {
    suspend fun downloadDogs(): ApiResponseStatus {
        return withContext(Dispatchers.IO) {
            try {
                val dogLisApiResponse = retrofitService.getAllDogs()
                val dogDTOList = dogLisApiResponse.data.dogs
                val dogDTOMapper = DogDTOMapper()
                ApiResponseStatus.Success(dogDTOMapper.fromDogDTOListToDogDomainList(dogDTOList))
            } catch (e: UnknownHostException) {
                ApiResponseStatus.Error(R.string.unknown_host_exception_error)
            } catch (e: Exception) {
                ApiResponseStatus.Error(R.string.unknown_error)
            }
        }
    }
}