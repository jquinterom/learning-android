package com.jhon.dogedex.dogdetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.dogdetail.ui.theme.DogedexTheme
import com.jhon.dogedex.machinelearning.DogRecognition
import com.jhon.dogedex.model.Dog

class DogDetailComposeActivity : ComponentActivity() {
    companion object {
        const val DOG_KEY = "key"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        if (dog == null) {
            Toast.makeText(
                this,
                resources.getString(R.string.error_showing_dog_not_found),
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContent {
            val status = viewModel.status
            if (status.value is ApiResponseStatus.Success) {
                finish()
            } else {
                DogedexTheme {
                    DogDetailScreen(
                        dog = dog,
                        status = status.value,
                        onButtonClicked = {
                            onButtonCLicked(
                                isRecognition = isRecognition,
                                dog.id
                            )
                        },
                    )
                }
            }
        }
    }

    private fun onButtonCLicked(isRecognition: Boolean, dogId: Long) {
        if (isRecognition) {
            viewModel.addDogToUser(dogId = dogId)
        } else {
            finish()
        }
    }
}
