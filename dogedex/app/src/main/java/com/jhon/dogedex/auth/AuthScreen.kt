package com.jhon.dogedex.auth

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.auth.AuthDestinations.LoginScreenDestination
import com.jhon.dogedex.auth.AuthDestinations.SignUpScreenDestination
import com.jhon.dogedex.composables.ErrorDialog
import com.jhon.dogedex.composables.LoadingWheel
import com.jhon.dogedex.model.User

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onUserLoggedIn: (User) -> Unit,
) {

    val user = authViewModel.user

    val userValue = user.value
    if (userValue != null) {
        onUserLoggedIn(userValue)
    }

    val navController = rememberNavController()
    val status = authViewModel.status.value

    AuthNavHost(
        navController = navController,
        onLoginButtonClick = { email, password ->
            authViewModel.login(
                email = email,
                password = password
            )
        },
        onSignupButtonClick = { email, password, passwordConfirmation ->
            authViewModel.signUp(
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
        },
        authViewModel = authViewModel,
    )

    if (status is ApiResponseStatus.Loading) {
        LoadingWheel()
    } else if (status is ApiResponseStatus.Error) {
        ErrorDialog(messageId = status.messageId, onErrorDialogDismiss = {
            authViewModel.resetApiResponseStatus()
        })
    }
}

@Composable
private fun AuthNavHost(
    navController: NavHostController,
    onLoginButtonClick: (String, String) -> Unit,
    onSignupButtonClick: (email: String, password: String, passwordConfirmation: String) -> Unit,
    authViewModel: AuthViewModel,
) {
    NavHost(navController = navController, startDestination = LoginScreenDestination) {
        composable(route = LoginScreenDestination) {
            LoginScreen(
                onLoginButtonClick = onLoginButtonClick,
                onRegisterButtonClick = {
                    navController.navigate(route = SignUpScreenDestination)
                },
                authViewModel = authViewModel,
            )
        }

        composable(route = SignUpScreenDestination) {
            SignUpScreen(
                onNavigationIconClick = { navController.navigateUp() },
                onSignupButtonClick = onSignupButtonClick,
                authViewModel = authViewModel,
            )
        }
    }
}