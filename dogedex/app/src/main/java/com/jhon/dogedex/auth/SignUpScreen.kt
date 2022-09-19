package com.jhon.dogedex.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jhon.dogedex.R
import com.jhon.dogedex.composables.AuthField
import com.jhon.dogedex.composables.BackNavigationIcon

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SignUpScreen(
    onNavigationIconClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(0.dp),
        topBar = { SignUpScreenToolbar(onNavigationIconClick = onNavigationIconClick) },
    ) { Content() }
}

@Composable
private fun Content() {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordConfirm by remember {
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
            value = email, onTextChanged = {
                email = it
            })

        AuthField(
            label = stringResource(id = R.string.password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = password, onTextChanged = {
                password = it
            },
            visualTransformation = PasswordVisualTransformation()
        )

        AuthField(
            label = stringResource(id = R.string.confirm_password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = passwordConfirm, onTextChanged = {
                passwordConfirm = it
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { /*TODO*/ }) {

            Text(
                text = stringResource(id = R.string.sign_up),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

    }
}

@Composable
private fun SignUpScreenToolbar(
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        backgroundColor = Color.Red,
        contentColor = Color.White,
        navigationIcon = { BackNavigationIcon { onNavigationIconClick() } },
        elevation = 4.dp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}