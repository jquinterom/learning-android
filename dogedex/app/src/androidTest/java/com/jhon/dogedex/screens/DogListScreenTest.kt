package com.jhon.dogedex.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jhon.dogedex.doglist.DogListScreen
import com.jhon.dogedex.doglist.DogListViewModel
import com.jhon.dogedex.repositories.DogRepositories

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

@ExperimentalMaterialApi
@RunWith(AndroidJUnit4::class)
class DogListScreenTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProgressBarShowWhenLoadingState() {
        val viewModel = DogListViewModel(
            dogRepository = DogRepositories.FakeDogRepository()
        )

        composeTestRule.setContent {
            DogListScreen(onNavigationIconClick = { /*TODO*/ }, onDogClicked = {},
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithTag(testTag = "loading-wheel")
            .assertIsDisplayed()
    }

    @Test
    fun testErrorDialogsShowsIfErrorGettingDogs() {
        val viewModel = DogListViewModel(
            dogRepository = DogRepositories.FakeDogRepositoryErrorGettingDogs()
        )

        composeTestRule.setContent {
            DogListScreen(onNavigationIconClick = { /*TODO*/ }, onDogClicked = {},
                viewModel = viewModel
            )
        }
        composeTestRule.onNodeWithTag(testTag = "error-dialog").assertIsDisplayed()
    }

    @Test
    fun testDogListShowIfSuccessGettingDogs() {
        val viewModel = DogListViewModel(
            dogRepository = DogRepositories.FakeDogRepositoryShowIfSuccessGettingDogs()
        )

        composeTestRule.setContent {
            DogListScreen(onNavigationIconClick = { /*TODO*/ }, onDogClicked = {},
                viewModel = viewModel
            )
        }

        /*
        composeTestRule.onNodeWithTag(
            useUnmergedTree = true,
            testTag = "dog-${Repositories.nameFakeDog1}"
        )
            .assertIsDisplayed()
        */

        composeTestRule.onNodeWithText(text = "2").assertIsDisplayed()
    }
}