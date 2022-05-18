package com.securityandsafetythings.examples.tflitedetector.detector.model

import android.graphics.RectF
import java.util.*

class Bird(id: String?, label: String?, confidence: Float?, location: RectF?) {

    // A unique identifier for what has been recognized. Specific to the image
    private var mId: String? = id

    // Display name for the recognition.
    private var mLabel: String? = label

    // A sortable score for how good the recognition is relative to others. Higher should be better.
    private var mConfidence: Float? = confidence

    // Optional location within the source image for the location of the recognized object.
    private var mLocation: RectF? = location

    /*
    /**
     * A single recognized object
     * @param id Identifier for the object
     * @param label The name of the object recognized
     * @param confidence Value from 0-1 how strong the confidence is for the detection
     * @param location Bounding box for the object
     */
    fun RecognitionJohn(
            id: String?, label: String?, confidence: Float?, location: RectF?) {
        mId = id
        mLabel = label
        mConfidence = confidence
        mLocation = location
    }
    */


    /**
     * Gets recognition id
     * @return String id of the object
     */
    fun getId(): String? {
        return mId
    }

    /**
     * Gets the label for the object
     * @return Object class
     */
    fun getLabel(): String? {
        return mLabel
    }

    /**
     * Gets object confidence score
     * @return 0-1 value indicating confidence
     */
    fun getConfidence(): Float? {
        return mConfidence
    }

    /**
     * Gets a bounding box specifying the detection location
     * @return Bounding box
     */
    fun getLocation(): RectF {
        return RectF(mLocation)
    }

    /**
     * Sets the location for a detection
     * @param location Rectangle specifying the location
     */
    fun setLocation(location: RectF?) {
        mLocation = location
    }

    override fun toString(): String {
        val space = ' '
        val resultString = StringBuilder()
        if (mId != null) {
            resultString.append("[").append(mId).append("] ")
        }
        if (mLabel != null) {
            resultString.append(mLabel).append(space)
        }
        if (mConfidence != null) {
            resultString.append(String.format(Locale.US, "(%.1f%%) ", mConfidence!! * 100.0f))
        }
        if (mLocation != null) {
            resultString.append(mLocation).append(space)
        }
        return resultString.toString().trim { it <= ' ' }
    }
}