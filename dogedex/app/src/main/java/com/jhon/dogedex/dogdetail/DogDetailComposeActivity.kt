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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogDetailComposeActivity : ComponentActivity() {
    companion object {
        const val DOG_KEY = "key"
        const val IS_RECOGNITION_KEY = "is_recognition"
        const val MOST_PROBABLE_DOGS_IDS = "most_probable_dogs_ids"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DogedexTheme {
                DogDetailScreen(
                    finishActivity = { finish() }
                )
            }
        }
    }

}
