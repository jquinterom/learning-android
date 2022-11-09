package com.jhon.dogedex.interfaces

import androidx.camera.core.ImageProxy
import com.jhon.dogedex.machinelearning.DogRecognition

interface ClassifierTasks {
    suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition>
}