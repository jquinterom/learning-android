package com.jhon.dogedex.auth

import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.api.DogsApi
import com.jhon.dogedex.api.dto.LoginDTO
import com.jhon.dogedex.api.dto.SignUpDTO
import com.jhon.dogedex.api.dto.UserDTOMapper
import com.jhon.dogedex.api.makeNetworkCall
import com.jhon.dogedex.model.User

class AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): ApiResponseStatus<User> =
        makeNetworkCall {
            val loginDTO = LoginDTO(
                email = email,
                password = password,
            )
            val loginResponse = DogsApi.retrofitService.login(loginDTO = loginDTO)

            if (!loginResponse.isSuccess) {
                throw Exception(loginResponse.message)
            }

            val userDTO = loginResponse.data.user
            val userDTOMapper = UserDTOMapper()
            userDTOMapper.fromUserDTOToUserDomain(userDTO)
        }


    suspend fun signUp(
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
            val signUpResponse = DogsApi.retrofitService.signUp(signUpDTO = signUpDTO)

            if (!signUpResponse.isSuccess) {
                throw Exception(signUpResponse.message)
            }

            val userDTO = signUpResponse.data.user
            val userDTOMapper = UserDTOMapper()
            userDTOMapper.fromUserDTOToUserDomain(userDTO)
        }
}