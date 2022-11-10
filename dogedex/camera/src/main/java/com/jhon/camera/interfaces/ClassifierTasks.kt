package com.jhon.camera.interfaces

import androidx.camera.core.ImageProxy
import com.jhon.camera.machinelearning.DogRecognition

interface ClassifierTasks {
    suspend fun recognizeImage(imageProxy: ImageProxy): List<DogRecognition>
}