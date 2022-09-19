package com.jhon.dogedex.doglist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.recyclerview.widget.GridLayoutManager
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.databinding.ActivityDogListBinding
import com.jhon.dogedex.dogdetail.DogDetailComposeActivity
import com.jhon.dogedex.dogdetail.ui.theme.DogedexTheme
import com.jhon.dogedex.model.Dog


class DogListActivity : ComponentActivity() {

    private val viewModel: DogListViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val status = viewModel.status
            DogedexTheme() {
                val dogList = viewModel.dogList
                DogListScreen(
                    onNavigationIconClick = ::onNavigationIconClick,
                    dogList = dogList.value, onDogClicked = ::openDogDetailActivity,
                    status = status.value,
                    onErrorDialogDismiss = { resetApiResponseStatus() }
                )
            }
        }
    }

    private fun openDogDetailActivity(dog: Dog) {
        val intent = Intent(this, DogDetailComposeActivity::class.java)
        intent.putExtra(DogDetailComposeActivity.DOG_KEY, dog)
        startActivity(intent)
    }

    private fun onNavigationIconClick() {
        finish()
    }


    private fun resetApiResponseStatus() {
        viewModel.resetApiResponseStatus()
    }
}