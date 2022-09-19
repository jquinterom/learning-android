package com.jhon.dogedex.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.jhon.dogedex.main.MainActivity
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.databinding.ActivityLoginBinding
import com.jhon.dogedex.dogdetail.ui.theme.DogedexTheme
import com.jhon.dogedex.model.User

class LoginActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val user = viewModel.user

            val userValue = user.value
            if (userValue != null) {
                User.setLoggedInUser(this, userValue)
                startMainActivity()
            }

            val status = viewModel.status

            DogedexTheme {
                AuthScreen(
                    onErrorDialogDismiss = ::resetApiResponseStatus,
                    status = status.value,
                    onLoginButtonClick = { email, password ->
                        viewModel.login(email = email, password = password)
                    },
                    onSignupButtonClick = { email, password, passwordConfirmation ->
                        viewModel.signUp(
                            email = email,
                            password = password,
                            passwordConfirmation = passwordConfirmation
                        )
                    },
                    authViewModel = viewModel
                )
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    /*
    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment)
            .navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
    }
    */


    private fun resetApiResponseStatus() {
        viewModel.resetApiResponseStatus()
    }
}