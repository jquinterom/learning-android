package com.jhon.dogedex.activities

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.jhon.dogedex.auth.AuthRepository
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

}