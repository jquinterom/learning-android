package com.jhon.dogedex.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository : AuthTasks
) : ViewModel() {

    var user = mutableStateOf<User?>(null)
        private set

    var status = mutableStateOf<ApiResponseStatus<User>?>(null)
        private set

    var emailError = mutableStateOf<Int?>(null)
        private set

    var passwordError = mutableStateOf<Int?>(null)
        private set

    var passwordConfirmationError = mutableStateOf<Int?>(null)
        private set



    fun login(email: String, password: String) {
        when {
            email.isEmpty() -> {
                emailError.value = R.string.email_is_not_valid
            }
            password.isEmpty() -> {
                passwordError.value = R.string.password_must_not_be_empty
            }
            else -> {
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
        }
    }

    fun signUp(email: String, password: String, passwordConfirmation: String) {
        when {
            email.isEmpty() -> {
                emailError.value = R.string.email_is_not_valid
            }
            password.isEmpty() -> {
                passwordError.value = R.string.password_must_not_be_empty
            }
            passwordConfirmation.isEmpty() -> {
                passwordConfirmationError.value = R.string.password_must_not_be_empty
            }
            password != passwordConfirmation -> {
                passwordError.value = R.string.passwords_do_not_match
                passwordConfirmationError.value = R.string.passwords_do_not_match
            }
            else -> {
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

    fun resetErrors() {
        emailError.value = null
        passwordError.value = null
        passwordConfirmationError.value = null
    }


}