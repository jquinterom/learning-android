package com.jhon.dogedex.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.model.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var user = mutableStateOf<User?>(null)
        private set

    var status = mutableStateOf<ApiResponseStatus<User>?>(null)
        private set

    private val authRepository = AuthRepository()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            status.value = ApiResponseStatus.Loading()

            handleResponseStatus(
                authRepository.login(
                    email = email,
                    password = password,
                )
            )
        }
    }

    fun signUp(email: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            status.value = ApiResponseStatus.Loading()
            handleResponseStatus(
                authRepository.signUp(
                    email = email,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
        }
    }

    private fun handleResponseStatus(apiResponseStatus: ApiResponseStatus<User>) {
        if (apiResponseStatus is ApiResponseStatus.Success) {
            user.value = apiResponseStatus.data
        }

        status.value = apiResponseStatus
    }

    fun resetApiResponseStatus() {
        status.value = null
    }


}