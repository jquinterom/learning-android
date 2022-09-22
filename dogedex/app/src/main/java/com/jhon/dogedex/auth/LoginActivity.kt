package com.jhon.dogedex.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.jhon.dogedex.dogdetail.ui.theme.DogedexTheme
import com.jhon.dogedex.main.MainActivity
import com.jhon.dogedex.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogedexTheme {
                AuthScreen(
                    onUserLoggedIn = ::startMainActivity
                )
            }
        }
    }

    private fun startMainActivity(userValue: User) {
        User.setLoggedInUser(this, userValue)

        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }
}