package com.jhon.dogedex.interfaces

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.model.User

interface AuthTasks {
    suspend fun login(
        email: String,
        password: String,
    ): ApiResponseStatus<User>

    suspend fun signUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ): ApiResponseStatus<User>
}