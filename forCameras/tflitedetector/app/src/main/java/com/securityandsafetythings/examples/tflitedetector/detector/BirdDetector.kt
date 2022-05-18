package com.securityandsafetythings.examples.tflitedetector.detector

import android.content.Context
import android.graphics.Bitmap
import com.securityandsafetythings.examples.tflitedetector.utilities.ResourceHelper
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage

class BirdDetector {
    private val modelFileName = "bird.tflite"

    private fun outputGenerator(bitmap: Bitmap, context: Context){
        //val birdsModel = BirdsModel.newInstance(this)
        val mOutputLocations: Array<Array<FloatArray>>

        // Load the model
        val mModel = Interpreter(ResourceHelper.loadModelFile(
                context.assets, modelFileName), null)

        val tfImage = TensorImage.fromBitmap(bitmap)
    }
}