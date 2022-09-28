package com.jhon.dogedex.repositories

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User
import javax.inject.Inject

class AuthRepositories {

    class FakeAuthRepository : AuthTasks {
        override suspend fun login(email: String, password: String): ApiResponseStatus<User> {
            TODO("Not yet implemented")
        }

        override suspend fun signUp(
            email: String,
            password: String,
            passwordConfirmation: String
        ): ApiResponseStatus<User> {
            TODO("Not yet implemented")
        }
    }


    class FakeAuthRepositoryToLogin @Inject constructor() : AuthTasks {
        override suspend fun login(email: String, password: String): ApiResponseStatus<User> {
            return ApiResponseStatus.Success(
                User(1L, "jhon@jhon.com", "")
            )
        }

        override suspend fun signUp(
            email: String,
            password: String,
            passwordConfirmation: String
        ): ApiResponseStatus<User> {
            TODO("Not yet implemented")
        }
    }
}