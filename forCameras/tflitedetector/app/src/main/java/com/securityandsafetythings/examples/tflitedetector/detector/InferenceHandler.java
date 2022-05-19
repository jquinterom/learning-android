/*
 * Copyright 2019-2020 by Security and Safety Things GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.securityandsafetythings.examples.tflitedetector.detector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;

import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.detector.model.Recognition;
import com.securityandsafetythings.examples.tflitedetector.events.OnInferenceCompletedEvent;
import com.securityandsafetythings.examples.tflitedetector.events.OnInferenceCompletedEventBird;
import com.securityandsafetythings.examples.tflitedetector.utilities.EasySharedPreference;
import com.securityandsafetythings.examples.tflitedetector.utilities.Renderer;
import com.securityandsafetythings.jumpsuite.commonhelpers.BitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for handling the messages sent to the InferenceThread.
 * <p> It handles the following messages:
 * <ol>
 *   <li> {@link Message#CONFIGURE_DETECTOR} - Configures the detector and calculates the attributes of the detector. </li>
 *   <li> {@link Message#RUN_INFERENCE} - Runs inference on the image. </li>
 * </ol>
 */
public class InferenceHandler extends Handler {

    private static final String LOGTAG = InferenceHandler.class.getSimpleName();
    private final Size mCaptureSize;
    private ObjectDetector mDetector;
    private Size mDetectorInputSize;
    private Renderer mRenderer;
    private Size mCropSize;
    private int mMarginLeft;
    private int mMarginTop;
    private Matrix mScalingMatrix;
    private long mTotalInferenceTime = 0;
    private long mTotalFrames;
    private long mStartTime = 0;

    /**
     * Constructs an InferenceHandler object.
     * @param looper The {@code Looper} associated with the InferenceThread
     * @param captureSize The size of the image as requested from the VideoPipeline.
     */
    public InferenceHandler(final Looper looper, final Size captureSize) {
        super(looper);
        mCaptureSize = captureSize;
    }

    @Override
    public void handleMessage(final android.os.Message msg) {
        final Message messageType = Message.fromOrdinal(msg.what);
        switch (messageType) {
        case CONFIGURE_DETECTOR:
            handleConfigureDetector();
            break;
        case RUN_INFERENCE:
            final Bitmap imageBmp = (Bitmap)msg.obj;
            handleRunningInference(imageBmp);
            break;
        default:
            Log.e(LOGTAG, "Unknown message received on InferenceThread");
        }
    }

    /**
     * Given the width and height of a region, provides a cropped width and height that matches a target aspect ratio
     *
     * This is simply an application of the formula:
     * Ratio = width / height
     * We are given Ratio, then we hold either width or height constant and solve for the other to produce the cropped
     * value resulting in the desired ratio.
     *
     * @param width             Input region width
     * @param height            Input region height
     * @param targetAspectRatio The aspect ratio floating point representation i.e) 1:1 = 1.0, 16:9  = 1.7778, etc.
     * @return new width and height paired in a {@link Size} object that matches the target aspect ratio
     */
    private Size getCropArea(final int width, final int height, final float targetAspectRatio) {
        final Size res;
        final int targetH = Math.round(width / targetAspectRatio);
        if (targetH <= height) {
            /* either full size or height is cropped */
            res = new Size(width, targetH);
        } else {
            /* width is cropped */
            res = new Size(Math.round(height * targetAspectRatio), height);
        }
        return res;
    }

    private void prepareForInference() {
        // Crop to center region
        final float targetAspectRatio = mDetectorInputSize.getWidth() / (float)mDetectorInputSize.getHeight();
        mCropSize = getCropArea(mCaptureSize.getWidth(), mCaptureSize.getHeight(), targetAspectRatio);
        /*
         * Calculate image margins
         * ">> 1" performs a bitshift division by 2 which computes the offset to the middle of the image
         */
        mMarginLeft = (mCaptureSize.getWidth() - mCropSize.getWidth()) >> 1;
        mMarginTop = (mCaptureSize.getHeight() - mCropSize.getHeight()) >> 1;
        /*
         * Calculate scale factor
         * How big is our detectors input compared to the image preview? We'll use this to scale our input
         * appropriately
         */
        final float scaleX = mDetectorInputSize.getWidth() / (float)mCropSize.getWidth();
        final float scaleY = mDetectorInputSize.getHeight() / (float)mCropSize.getHeight();
        // Construct scaling matrix
        mScalingMatrix = new Matrix();
        mScalingMatrix.postScale(scaleX, scaleY);
    }

    @SuppressWarnings("MagicNumber")
    private void handleConfigureDetector() {
        // Stop the detector if it is already running
        if (mDetector != null) {
            mDetector.destroy();
            mDetector = null;
            mDetectorInputSize = null;
        }
        // Configure the detector with the selected acceleration type
        mDetector = new ObjectDetectorBuilder()
                // Filename of the model stored in the assets folder of the app.
                .setModelFileName("detect.tflite")
                /*
                 * Resource id of the label file that the model uses.
                 * The labels file is kept in the resources folder('/res/raw/') of the app.
                 */
                .setLabelFileResourceId(R.raw.labelmap)
                // The model input size. It is denoted by inputSize x inputSize.
                .setInputSize(300)
                /*
                 * The type of acceleration the user prefers to use
                 * for running inference on images.
                 */
                .setAccelerationType(EasySharedPreference.getInstance().getRequestedAccelerationType())
                /*
                 * Defines whether the input model is quantized (lossy compressed) or not.
                 * Set to true for quantized models, otherwise set to false.
                 */
                .setIsQuantized(true)
                /*
                 * Configures the detector to use 16 bit floating point numbers rather than 32 bit.
                 * This will optimize memory at the cost of accuracy.
                 */
                .allowFp16PrecisionForFp32().build();
        mDetectorInputSize = mDetector.getRequiredImageSize();
        /*
         * Whenever the detector is re-configured, 'mDetectorInputSize'
         * could change and hence the image manipulation parameters must be re-calculated.
         */
        prepareForInference();
        // Initializes a new Renderer with the new image manipulation parameters.
        mRenderer = new Renderer(mCaptureSize, mCropSize, new Size(mMarginLeft, mMarginTop));
        // After configuring a detector, initialize the inference statistics
        mTotalInferenceTime = 0;
        mTotalFrames = 0;
        mStartTime = SystemClock.elapsedRealtime();
    }

    /**
     * The example model operates on 300x300 pixel images. You may or may not recall that the image preview used in
     * Helloworld is full HD 1920x1080. We would not want to use just a small 300x300 crop of our preview image. Instead what
     * we can do is crop the full resolution image to the same aspect ratio as our model (1:1) and then rescale the cropped
     * image to the size our detector accepts. This way the resize operation does not warp our input, though this warping is
     * valid for some models, we won't use it here.
     */
    private void handleRunningInference(final Bitmap imageBmp) {
        // Run object detection on the frame Bitmap.
        final List<Recognition> detectionResults = detectObjectsInFrame(imageBmp);

        /*
         * Filters detection results that meet or exceed the confidence threshold set in user preferences, renders
         * bounding boxes on the frame's {@code Bitmap}, and compresses the {@code Bitmap} into bytes.
         */
        final byte[] annotatedImageBytes = getAnnotatedImageAsBytes(imageBmp, detectionResults);

        // Calculate the number of frames processed per second by the detector using different acceleration types.
        ++mTotalFrames;
        // Time taken in seconds to process the number of frames denoted by mTotalFrames
        final long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime() - mStartTime);
        int framesProcessedPerSecond = (int)mTotalFrames;
        if (timeInSeconds > 1) {
            framesProcessedPerSecond = (int)(mTotalFrames / timeInSeconds);
        }

        // Send an event to indicate that inference has been completed.
        new OnInferenceCompletedEvent(annotatedImageBytes,
               mTotalInferenceTime / mTotalFrames,
                framesProcessedPerSecond).broadcastEvent();

        new OnInferenceCompletedEventBird(annotatedImageBytes, "Jhon Test 3").broadcastEvent();
    }

    private List<Recognition> detectObjectsInFrame(final Bitmap imageBmp) {
        /*
         * Take scaled center cut of the image to run detection on
         *
         * marginLeft: defines left boundary of the crop area
         * marginTop: defines top  boundary of the crop area
         * width: how far right to read in the x axis from marginLeft start point
         * height: how far down to read in the y axis from marginTop start point
         * scalingMatrix: how to resize the image after it's been cropped. This will scale the crop to 300x300
         * boolean: whether or not to filter pixels, true provides smoothing
         */
        final Bitmap croppedBitmap = Bitmap.createBitmap(imageBmp,
            mMarginLeft,
            mMarginTop,
            mCropSize.getWidth(),
            mCropSize.getHeight(),
            mScalingMatrix,
            true);
        // Perform object detection using the detector
        final long inferenceStartTime = SystemClock.elapsedRealtime();
        final List<Recognition> detectionResults = mDetector.recognizeImage(croppedBitmap);
        final long inferenceTime = SystemClock.elapsedRealtime() - inferenceStartTime;
        // Increase the total inference time
        mTotalInferenceTime += inferenceTime;
        return detectionResults;
    }

    private byte[] getAnnotatedImageAsBytes(final Bitmap imageBmp, final List<Recognition> detectionResults) {
        // Filter detections that meet the specified minimum confidence threshold
        final List<Recognition> filteredDetections = new ArrayList<>();
        for (Recognition obj : detectionResults) {
            if (obj.getConfidence() >= EasySharedPreference.getInstance().getMinConfidenceLevel()) {
                filteredDetections.add(obj);
            }
        }
        // Render the filtered detections on the original bitmap (not the one that was cropped for running inference).
        mRenderer.render(new Canvas(imageBmp), filteredDetections);
        /*
         * Compress the annotated Bitmap before displaying it in the browser. If the Bitmap is not compressed, then the
         * browser will not be able to decipher the image and will show an error.
         */
        return BitmapUtils.compressBitmap(imageBmp);
    }

    /**
     * Enum defining the messages that the InferenceThread can process.
     */
    public enum Message {
        /**
         * Send this message to configure a detector.
         */
        CONFIGURE_DETECTOR,
        /**
         * Send this message to run inference on an image.
         */
        RUN_INFERENCE;

        private static final Message[] VALUES = values();

        /**
         * Retrieves the Message value given an ordinal value.
         *
         * @param ordinal The ordinal value representation of an {@code Message}.
         * @return An {@code Message} represented by the provided ordinal value.
         */
        private static Message fromOrdinal(final int ordinal) {
            return VALUES[ordinal];
        }
    }
}
