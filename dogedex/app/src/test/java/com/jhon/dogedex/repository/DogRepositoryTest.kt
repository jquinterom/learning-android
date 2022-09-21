package com.jhon.dogedex.repository

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.ApiService
import com.jhon.dogedex.api.dto.AddDogToUserDTO
import com.jhon.dogedex.api.dto.DogDTO
import com.jhon.dogedex.api.dto.LoginDTO
import com.jhon.dogedex.api.dto.SignUpDTO
import com.jhon.dogedex.api.responses.*
import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.*
import org.junit.Assert.*
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class DogRepositoryTest {
    private var apiResponseStatus: ApiResponseStatus<List<Dog>>? = null
    private var apiResponseStatusToError: ApiResponseStatus<List<Dog>>? = null

    companion object {
        private var dogRepository: DogRepository? = null
        private var dogRepositoryToError: DogRepository? = null
        private const val nameFakeSecond = "FakeDog2"
        private val fakeDogUser = DogDTO(
            1, 2, nameFakeSecond, "", "", "", "",
            "", "", "", ""
        )
        private var dogCollection: List<Dog>? = null

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            dogRepository = DogRepository(
                apiService = FakeApiService(),
                dispatcher = UnconfinedTestDispatcher()
            )

            dogRepositoryToError = DogRepository(
                apiService = FakeApiService2(),
                dispatcher = UnconfinedTestDispatcher()
            )
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            dogRepository = null
            dogRepositoryToError = null
        }
    }

    @Before
    fun setup(): Unit = runBlocking {
        apiResponseStatus = dogRepository?.getDogCollection()
        dogCollection = (apiResponseStatus as ApiResponseStatus.Success).data

        apiResponseStatusToError = dogRepositoryToError?.getDogCollection()
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

    class FakeApiService2 : ApiService {
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

    @Test
    fun testDownloadDogListStatusesCorrect() {
        assert(apiResponseStatus is ApiResponseStatus.Success)
    }

    @Test
    fun testSizeOfCollectionIsCorrect() {
        assertEquals(2, dogCollection?.size)
    }

    @Test
    fun testValidDogFakeForUserIsSecond() {
        assertEquals(nameFakeSecond, dogCollection?.get(1)?.name)
    }

    @Test
    fun testValidaNameDogIsEmpty() {
        assertEquals("", dogCollection?.get(0)?.name)
    }

    @Test
    fun testGetAllDogError() {
        assert(apiResponseStatusToError is ApiResponseStatus.Error)
    }
}