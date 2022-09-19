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

class LoginActivity : ComponentActivity(), LoginFragment.LoginFragmentActions,
    SignUpFragment.SignUpFragmentActions {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogedexTheme {
                AuthScreen()
            }
        }
        /*
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    showErrorDialog(status.messageId)
                }
                is ApiResponseStatus.Loading -> {
                    Log.d("Loading", "Loading ...")
                    binding.loadingWheel.visibility = View.VISIBLE
                }
                is ApiResponseStatus.Success -> binding.loadingWheel.visibility = View.GONE
            }
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                User.setLoggedInUser(this, user)
                startMainActivity()
            }
        }

         */
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun showErrorDialog(messageId: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.unknown_error)
            .setMessage(messageId)
            .setPositiveButton(android.R.string.ok) { _, _ -> /** Dismiss Dialog */ }
            .create()
            .show()
    }

    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment)
            .navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
    }

    override fun onLoginFieldsValidated(email: String, password: String) {
        viewModel.login(email = email, password = password)
    }

    override fun onSignUpFieldsValidated(
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModel.signUp(
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation
        )
    }
}