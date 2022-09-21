package com.jhon.dogedex.viewModel

import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.auth.AuthViewModel
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.AfterClass
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.BeforeClass

class AuthViewModelTest {

    companion object {
        var viewModel: AuthViewModel? = null
        private const val loginAccount: String = "jhon@jhon.com"
        private const val passwordAccount: String = "Password1234"
        private const val authToken: String = "123jwt"
        val fakeUser = User(1, loginAccount, authToken)
        private const val emptyString = ""

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            viewModel = AuthViewModel(authRepository = FakeAuthRepository())
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            viewModel = null
        }
    }

    class FakeAuthRepository : AuthTasks {
        override suspend fun login(
            email: String,
            password: String
        ): ApiResponseStatus<User> {
            return ApiResponseStatus.Success(fakeUser)
        }

        override suspend fun signUp(
            email: String,
            password: String,
            passwordConfirmation: String
        ): ApiResponseStatus<User> {
            return ApiResponseStatus.Success(fakeUser)
        }
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = DogedexCoroutineRule()

    @Test
    fun testLoginValidationEmailIsNotValid() {
        viewModel?.login(email = emptyString, password = passwordAccount)
        assertEquals(R.string.email_is_not_valid, viewModel?.emailError?.value)
    }

    @Test
    fun testLoginValidationPasswordIsNotValid() {
        viewModel?.login(email = loginAccount, password = emptyString)
        assertEquals(R.string.password_must_not_be_empty, viewModel?.passwordError?.value)
    }

    @Test
    fun testLoginValidationUserIsCorrect() {
        viewModel?.login(loginAccount, passwordAccount)
        assertEquals(fakeUser.email, viewModel?.user?.value?.email)
    }

    @Test
    fun testResetErrorCorrect() {
        viewModel?.resetErrors()
        assertNull(viewModel?.emailError?.value)
        assertNull(viewModel?.passwordError?.value)
        assertNull(viewModel?.passwordConfirmationError?.value)
    }

    @Test
    fun testSignUpEmailEmpty() {
        viewModel?.signUp(emptyString, passwordAccount, passwordAccount)
        assertEquals(R.string.email_is_not_valid, viewModel?.emailError?.value)
    }

    @Test
    fun testSignUpPasswordEmpty() {
        viewModel?.signUp(loginAccount, emptyString, passwordAccount)
        assertEquals(R.string.password_must_not_be_empty, viewModel?.passwordError?.value)
    }

    @Test
    fun testPasswordsDoNotMatch() {
        viewModel?.signUp(loginAccount, "badPassword", passwordAccount)
        assertEquals(R.string.passwords_do_not_match, viewModel?.passwordConfirmationError?.value)
    }
}