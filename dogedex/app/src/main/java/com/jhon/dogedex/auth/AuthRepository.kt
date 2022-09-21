package com.jhon.dogedex.auth

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.ApiService
import com.jhon.dogedex.api.dto.LoginDTO
import com.jhon.dogedex.api.dto.SignUpDTO
import com.jhon.dogedex.api.dto.UserDTOMapper
import com.jhon.dogedex.api.makeNetworkCall
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
): AuthTasks {
    override suspend fun login(
        email: String,
        password: String,
    ): ApiResponseStatus<User> =
        makeNetworkCall {
            val loginDTO = LoginDTO(
                email = email,
                password = password,
            )
            val loginResponse = apiService.login(loginDTO = loginDTO)

            if (!loginResponse.isSuccess) {
                throw Exception(loginResponse.message)
            }

            val userDTO = loginResponse.data.user
            val userDTOMapper = UserDTOMapper()
            userDTOMapper.fromUserDTOToUserDomain(userDTO)
        }


    override suspend fun signUp(
        email: String,
        password: String,
        passwordConfirmation: String
    ): ApiResponseStatus<User> =
        makeNetworkCall {
            val signUpDTO = SignUpDTO(
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
            val signUpResponse = apiService.signUp(signUpDTO = signUpDTO)

            if (!signUpResponse.isSuccess) {
                throw Exception(signUpResponse.message)
            }

            val userDTO = signUpResponse.data.user
            val userDTOMapper = UserDTOMapper()
            userDTOMapper.fromUserDTOToUserDomain(userDTO)
        }
}