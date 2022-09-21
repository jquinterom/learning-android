package com.jhon.dogedex

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.doglist.DogListViewModel
import com.jhon.dogedex.interfaces.DogTasks
import com.jhon.dogedex.model.Dog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DogListViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = DogedexCoroutineRule()

    @Test
    fun downloadDogListStatusesCorrect() {
        class FakeDogRepository : DogTasks {
            override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
                return ApiResponseStatus.Success(
                    listOf(
                        Dog(
                            0, 1, "", "", "", "", "",
                            "", "", "", "", inCollection = false
                        ),

                        Dog(
                            1, 2, "", "", "", "", "",
                            "", "", "", "", inCollection = false
                        )
                    )
                )

            }

            override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
                return ApiResponseStatus.Success(Unit)
            }

            override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
                return ApiResponseStatus.Success(
                    Dog(
                        1, 2, "", "", "", "", "",
                        "", "", "", "", inCollection = false
                    )
                )
            }
        }

        val viewModel = DogListViewModel(
            dogRepository = FakeDogRepository()
        )

        //assertEquals(2, viewModel.dogList.value.size)
        //assertEquals(1, viewModel.dogList.value[1].id)
        assert(viewModel.status.value is ApiResponseStatus.Success)
    }

    @Test
    fun downloadDogListErrorStatusesCorrect() {
        class FakeDogRepository : DogTasks {
            override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
                return ApiResponseStatus.Error(R.string.unknown_error)

            }

            override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
                return ApiResponseStatus.Success(Unit)
            }

            override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
                return ApiResponseStatus.Success(
                    Dog(
                        1, 2, "", "", "", "", "",
                        "", "", "", "", inCollection = false
                    )
                )
            }
        }

        val viewModel = DogListViewModel(
            dogRepository = FakeDogRepository()
        )

        assert(viewModel.status.value is ApiResponseStatus.Error)
    }

    @Test
    fun resetStatusCorrect() {
        class FakeDogRepository : DogTasks {
            override suspend fun getDogCollection(): ApiResponseStatus<List<Dog>> {
                return ApiResponseStatus.Error(R.string.unknown_error)

            }

            override suspend fun addDogToUser(dogId: Long): ApiResponseStatus<Any> {
                return ApiResponseStatus.Success(Unit)
            }

            override suspend fun getDogByMlId(mlDogId: String): ApiResponseStatus<Dog> {
                return ApiResponseStatus.Success(
                    Dog(
                        1, 2, "", "", "", "", "",
                        "", "", "", "", inCollection = false
                    )
                )
            }
        }

        val viewModel = DogListViewModel(
            dogRepository = FakeDogRepository()
        )

        //assert(viewModel.status.value is ApiResponseStatus.Error)
        viewModel.resetApiResponseStatus()
        assertEquals(null, viewModel.status.value)
    }
}