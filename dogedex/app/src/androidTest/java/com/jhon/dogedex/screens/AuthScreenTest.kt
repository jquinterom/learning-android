package com.jhon.dogedex.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.auth.AuthScreen
import com.jhon.dogedex.auth.AuthViewModel
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.model.User
import com.jhon.dogedex.repositories.AuthRepositories
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTappingRegisterButtonOpenSignUpScreen() {
        val viewModel = AuthViewModel(
            authRepository = AuthRepositories.FakeAuthRepository()
        )

        composeTestRule.setContent {
            AuthScreen(onUserLoggedIn = {}, authViewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "login-button")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(
            useUnmergedTree = true,
            testTag = "login-screen-register-button"
        )
            .performClick()

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "sign-up-button")
            .assertIsDisplayed()
    }

    @Test
    fun testEmailErrorShowsIfTappingLoginButtonAndNotEmail() {
        val viewModel = AuthViewModel(
            authRepository = AuthRepositories.FakeAuthRepository()
        )

        composeTestRule.setContent {
            AuthScreen(onUserLoggedIn = {}, authViewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "login-button")
            .performClick()

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "email-field-error")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "email-field")
            .performTextInput("jhon@jhon.com")

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "login-button")
            .performClick()

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "password-field-error")
            .assertIsDisplayed()
    }

}