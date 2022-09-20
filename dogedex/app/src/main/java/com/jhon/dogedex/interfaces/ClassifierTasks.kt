package com.jhon.dogedex.interfaces

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.jhon.dogedex.machinelearning.DogRecognition

interface ClassifierTasks {
    suspend fun recognizeImage(imageProxy: ImageProxy): DogRecognition
}