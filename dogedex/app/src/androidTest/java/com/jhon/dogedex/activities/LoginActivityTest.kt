package com.jhon.dogedex.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.jhon.dogedex.auth.LoginActivity
import com.jhon.dogedex.di.AuthTasksModule
import com.jhon.dogedex.interfaces.AuthTasks
import com.jhon.dogedex.repositories.AuthRepositories
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import org.junit.Test
import com.jhon.dogedex.R

@HiltAndroidTest
@UninstallModules(AuthTasksModule::class)
class LoginActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get: Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class AuthTasksTestModule {

        @Binds
        abstract fun bindDogTasks(
            dogRepository: AuthRepositories.FakeAuthRepositoryToLogin
        ): AuthTasks
    }

    @Test
    fun mainActivityOpensAfterUserLogin() {
        val context = composeTestRule.activity

        composeTestRule.onNodeWithText(context.getString(R.string.login)).assertIsDisplayed()

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "email-field")
            .performTextInput("jhon@jhon.com")

        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "password-field")
            .performTextInput("test123")

        composeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()

        onView(withId(R.id.take_photo_fab)).check(matches(isDisplayed()))
    }

}