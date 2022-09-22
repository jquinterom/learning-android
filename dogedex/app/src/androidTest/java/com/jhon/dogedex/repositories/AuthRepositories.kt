package com.jhon.dogedex.repositories

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User

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
}