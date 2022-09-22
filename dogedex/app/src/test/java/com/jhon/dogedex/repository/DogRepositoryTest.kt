package com.jhon.dogedex.repository

import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.doglist.DogRepository
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class DogRepositoryTest {
    private var apiResponseStatus: ApiResponseStatus<List<Dog>>? = null
    private var apiResponseStatusToError: ApiResponseStatus<List<Dog>>? = null

    companion object {
        private var dogRepository: DogRepository? = null
        private var dogRepositoryToError: DogRepository? = null
        private var dogRepositoryToMlModelSuccess: DogRepository? = null
        private var dogRepositoryToMlModelError: DogRepository? = null
        const val nameFakeSecond = "FakeDog2"

        private var dogCollection: List<Dog>? = null

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            dogRepository = DogRepository(
                apiService = FakeServices.FakeApiService(),
                dispatcher = UnconfinedTestDispatcher()
            )

            dogRepositoryToError = DogRepository(
                apiService = FakeServices.FakeApiServiceToError(),
                dispatcher = UnconfinedTestDispatcher()
            )

            dogRepositoryToMlModelSuccess = DogRepository(
                apiService = FakeServices.FakeApiServiceToMlModelSuccess(),
                dispatcher = UnconfinedTestDispatcher()
            )

            dogRepositoryToMlModelError = DogRepository(
                apiService = FakeServices.FakeApiServiceToMlModelError(),
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

    @Test
    fun testValidMessageError() {
        assertEquals(
            R.string.unknown_host_exception_error,
            (apiResponseStatusToError as ApiResponseStatus.Error).messageId
        )
    }

    @Test
    fun testGetDogByMlSuccess(): Unit = runBlocking {
        val apiResponseStatus = dogRepositoryToMlModelSuccess?.getDogByMlId("well")
        assert(apiResponseStatus is ApiResponseStatus.Success)
    }

    @Test
    fun testGetDogByMlSuccessIdIsCorrect(): Unit = runBlocking {
        val apiResponseStatus = dogRepositoryToMlModelSuccess?.getDogByMlId("well")
        assertEquals(
            FakeServices.fakeDogUser.id,
            (apiResponseStatus as ApiResponseStatus.Success).data.id
        )
    }

    @Test
    fun testGetDogByMlIsError(): Unit = runBlocking {
        val apiResponseStatus = dogRepositoryToMlModelError?.getDogByMlId("well")
        assert(apiResponseStatus is ApiResponseStatus.Error)
    }

    @Test
    fun testGetDogByMlIsErrorMessageIdIsUnknown(): Unit = runBlocking {
        val apiResponseStatus = dogRepositoryToMlModelError?.getDogByMlId("well")
        assertEquals(
            R.string.unknown_error,
            (apiResponseStatus as ApiResponseStatus.Error).messageId
        )
    }

}