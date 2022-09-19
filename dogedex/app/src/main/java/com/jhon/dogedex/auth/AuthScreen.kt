package com.jhon.dogedex.auth

import androidx.compose.runtime.Composable
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
    status: ApiResponseStatus<User>?,
    onErrorDialogDismiss: () -> Unit,
    onLoginButtonClick: (String, String) -> Unit,
    onSignupButtonClick: (email: String, password: String, passwordConfirmation: String) -> Unit,
) {
    val navController = rememberNavController()
    AuthNavHost(
        navController = navController,
        onLoginButtonClick = onLoginButtonClick,
        onSignupButtonClick = onSignupButtonClick,
    )

    if (status is ApiResponseStatus.Loading) {
        LoadingWheel()
    } else if (status is ApiResponseStatus.Error) {
        ErrorDialog(messageId = status.messageId, onErrorDialogDismiss = onErrorDialogDismiss)
    }
}

@Composable
private fun AuthNavHost(
    navController: NavHostController,
    onLoginButtonClick: (String, String) -> Unit,
    onSignupButtonClick: (email: String, password: String, passwordConfirmation: String) -> Unit,
) {
    NavHost(navController = navController, startDestination = LoginScreenDestination) {
        composable(route = LoginScreenDestination) {
            LoginScreen(
                onLoginButtonClick = onLoginButtonClick,
                onRegisterButtonClick = {
                    navController.navigate(route = SignUpScreenDestination)
                })
        }

        composable(route = SignUpScreenDestination) {
            SignUpScreen(
                onNavigationIconClick = { navController.navigateUp() },
                onSignupButtonClick = onSignupButtonClick
            )
        }
    }
}