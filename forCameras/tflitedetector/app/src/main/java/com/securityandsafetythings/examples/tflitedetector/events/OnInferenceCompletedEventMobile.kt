package com.securityandsafetythings.examples.tflitedetector.events

internal class OnInferenceCompletedEventMobile(imageAsBytes: ByteArray, name: String?) : BaseEvent() {
    // A byte array that represents the image annotated with bounding boxes.
    private var mImageBytes: ByteArray = imageAsBytes

    private var mName: String? = name

    /**
     * Gets a `byte[]` that represents the image annotated with bounding boxes.
     *
     * @return A `byte[]` that represents the image annotated with bounding boxes.
     */
    fun getImageAsBytes(): ByteArray {
        return mImageBytes
    }

    /**
     * Gets the inference time.
     *
     * @return The time taken for inference.
     */
    fun getName(): String? {
        return mName
    }
}