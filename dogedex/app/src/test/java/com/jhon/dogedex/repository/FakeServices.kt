package com.jhon.dogedex.repository

import com.jhon.dogedex.api.ApiService
import com.jhon.dogedex.api.dto.AddDogToUserDTO
import com.jhon.dogedex.api.dto.DogDTO
import com.jhon.dogedex.api.dto.LoginDTO
import com.jhon.dogedex.api.dto.SignUpDTO
import com.jhon.dogedex.api.responses.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class FakeServices {

    companion object {
        val fakeDogUser = DogDTO(
            1, 2, DogRepositoryTest.nameFakeSecond, "", "", "", "",
            "", "", "", ""
        )
    }

    class FakeApiService : ApiService {
        override suspend fun getAllDogs(): DogListApiResponse {
            return DogListApiResponse(
                message = "success",
                isSuccess = true,
                data = DogListResponse(
                    listOf(
                        DogDTO(
                            0, 1, "FakeDog1", "", "", "", "",
                            "", "", "", ""
                        ),
                        fakeDogUser
                    )
                ),
            )

        }

        override suspend fun login(loginDTO: LoginDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun signUp(signUpDTO: SignUpDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun addDogToUser(addDogToUserDTO: AddDogToUserDTO): DefaultResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getUserDogs(): DogListApiResponse {
            return DogListApiResponse(
                message = "success",
                isSuccess = true,
                data = DogListResponse(
                    listOf(fakeDogUser)
                ),
            )
        }

        override suspend fun getDogByMlId(mlId: String): DogApiResponse {
            TODO("Not yet implemented")
        }
    }

    class FakeApiServiceToError : ApiService {
        override suspend fun getAllDogs(): DogListApiResponse {
            throw UnknownHostException()
        }

        override suspend fun login(loginDTO: LoginDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun signUp(signUpDTO: SignUpDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun addDogToUser(addDogToUserDTO: AddDogToUserDTO): DefaultResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getUserDogs(): DogListApiResponse {
            return DogListApiResponse(
                message = "success",
                isSuccess = true,
                data = DogListResponse(
                    listOf(fakeDogUser)
                ),
            )
        }

        override suspend fun getDogByMlId(mlId: String): DogApiResponse {
            TODO("Not yet implemented")
        }
    }

    class FakeApiServiceToMlModelSuccess : ApiService {
        override suspend fun getAllDogs(): DogListApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun login(loginDTO: LoginDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun signUp(signUpDTO: SignUpDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun addDogToUser(addDogToUserDTO: AddDogToUserDTO): DefaultResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getUserDogs(): DogListApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getDogByMlId(mlId: String): DogApiResponse {
            return DogApiResponse(
                message = "",
                isSuccess = true,
                data = DogResponse(
                    dog = fakeDogUser
                )
            )
        }
    }


    class FakeApiServiceToMlModelError : ApiService {
        override suspend fun getAllDogs(): DogListApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun login(loginDTO: LoginDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun signUp(signUpDTO: SignUpDTO): AuthApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun addDogToUser(addDogToUserDTO: AddDogToUserDTO): DefaultResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getUserDogs(): DogListApiResponse {
            TODO("Not yet implemented")
        }

        override suspend fun getDogByMlId(mlId: String): DogApiResponse {
            return DogApiResponse(
                message = "error_getting_dog_by_ml_id",
                isSuccess = false,
                data = DogResponse(
                    dog = fakeDogUser
                )
            )
        }
    }
}