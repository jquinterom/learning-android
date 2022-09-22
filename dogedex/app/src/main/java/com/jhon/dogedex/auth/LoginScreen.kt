package com.jhon.dogedex.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.databinding.adapters.TextViewBindingAdapter
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.composables.AuthField

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onLoginButtonClick: (String, String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    authViewModel: AuthViewModel,
) {
    Scaffold(
        modifier = Modifier.padding(0.dp),
        topBar = { LoginScreenToolbar() },
    ) {
        Content(
            onLoginButtonClick = onLoginButtonClick,
            onRegisterButtonClick = onRegisterButtonClick,
            authViewModel = authViewModel,
            resetFieldErrors = { authViewModel.resetErrors() }
        )
    }
}

@Composable
private fun Content(
    onLoginButtonClick: (String, String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    authViewModel: AuthViewModel,
    resetFieldErrors: () -> Unit,
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthField(
            label = stringResource(id = R.string.email),
            modifier = Modifier
                .fillMaxWidth(),
            value = email,
            onTextChanged = {
                email = it
                resetFieldErrors()
            },
            errorMessageId = authViewModel.emailError.value,
            errorSemantic = "email-field-error",
            fieldSemantic = "email-field",
        )

        AuthField(
            label = stringResource(id = R.string.password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = password,
            onTextChanged = {
                password = it
                resetFieldErrors()
            },
            visualTransformation = PasswordVisualTransformation(),
            errorMessageId = authViewModel.passwordError.value,
            errorSemantic = "password-field-error",
            fieldSemantic = "password-field",
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics {
                    testTag = "login-button"
                },
            onClick = {
                onLoginButtonClick(email, password)
            }) {

            Text(
                text = stringResource(id = R.string.login),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(id = R.string.do_not_have_an_account),
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier
                .clickable(enabled = true, onClick = {
                    onRegisterButtonClick()
                })
                .fillMaxWidth()
                .padding(16.dp)
                .semantics { testTag = "login-screen-register-button" },
            text = stringResource(id = R.string.register),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}


@Composable
fun LoginScreenToolbar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        backgroundColor = colorResource(id = R.color.colorPrimaryDark),
        contentColor = Color.White,
        elevation = 4.dp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}