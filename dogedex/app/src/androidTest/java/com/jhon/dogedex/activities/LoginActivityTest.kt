package com.jhon.dogedex.activities

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.jhon.dogedex.auth.LoginActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class LoginActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get: Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<LoginActivity>()


}