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
import com.jhon.dogedex.viewModel.DogedexCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class DogRepositoryTest {
    private var apiResponseStatus: ApiResponseStatus<List<Dog>>? = null

    companion object {
        private var dogRepository: DogRepository? = null
        private const val nameFakeDog2 = "FakeDog2"
        private val fakeDogUser = DogDTO(
            1, 2, nameFakeDog2, "", "", "", "",
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
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            dogRepository = null
        }
    }

    @Before
    fun setup(): Unit = runBlocking {
        apiResponseStatus = dogRepository?.getDogCollection()
        dogCollection = (apiResponseStatus as ApiResponseStatus.Success).data
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

    @Test
    fun downloadDogListStatusesCorrect() {
        assert(apiResponseStatus is ApiResponseStatus.Success)
    }

    @Test
    fun sizeOfCollectionIsCorrect() {
        assertEquals(2, dogCollection?.size)
    }

    @Test
    fun validDogFakeForUserIsSecond() {
        assertEquals(nameFakeDog2, dogCollection?.get(1)?.name)
    }

    @Test
    fun validaNameDogIsEmpty(){
        assertEquals("", dogCollection?.get(0)?.name)
    }
}