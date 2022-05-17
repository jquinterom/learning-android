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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.Size;

import com.securityandsafetythings.examples.tflitedetector.BuildConfig;
import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.TfLiteDetectorApplication;
import com.securityandsafetythings.examples.tflitedetector.detector.model.Recognition;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;
import com.securityandsafetythings.examples.tflitedetector.events.OnObjectDetectorInitializationFailedEvent;
import com.securityandsafetythings.examples.tflitedetector.events.OnObjectDetectorInitializedEvent;
import com.securityandsafetythings.examples.tflitedetector.utilities.ResourceHelper;
import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.HexagonDelegate;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ObjectDetector is the class responsible for preparing and accessing our model. It is based on the example provided by
 * the TensorFlow team
 */
class ObjectDetector {
    private static final String LOGTAG = ObjectDetector.class.getSimpleName();
    // Gets byte associated with red channel
    @SuppressWarnings("MagicNumber")
    private static final int SHIFT_RED = 16;
    // Gets byte associated with green channel
    @SuppressWarnings("MagicNumber")
    private static final int SHIFT_GREEN = 8;
    // Median value of 0 - 255 used to normalize inputs for non quantized models
    @SuppressWarnings("MagicNumber")
    private static final float IMAGE_MED = 128.0f;
    // Used to select individual bytes from RGB channels of the image
    @SuppressWarnings("MagicNumber")
    private static final int BYTE_MASK = 0xff;
    // For Auto mode, use this pre-defined list of AccelerationTypes to check the optimal AccelerationType in-order.
    private static final List<AccelerationType> AUTO_ACCELERATION_TYPES;
    // nxn size of the image the model expects as input
    private final int mInputSize;
    // Defines the maximum number of objects detected per image
    private final int mMaxDetectionsPerImage;
    // Ordered list mapping from model output to string label
    private List<String> mLabels;
    // TensorFlow lite api
    private Interpreter mModel;
    // Whether the model is quantized or not. This affects how input images are processed
    private final boolean mIsQuantized;
    // Holds the int pixel values for each image
    private final int[] mPixelValues;

    /**
     * outputLocations: array of shape [Batchsize, mMaxDetectionsPerImage, 4]
     * contains the location of detected boxes in [top, left, bottom, right] format per detection
     */
    private float[][][] mOutputLocations;
    /**
     * outputClasses: array of shape [Batchsize, mMaxDetectionsPerImage]
     * contains the classes of detected boxes
     */
    private float[][] mOutputClasses;
    /**
     * outputScores: array of shape [Batchsize, mMaxDetectionsPerImage]
     * contains the scores of detected boxes
     */
    private float[][] mOutputScores;
    /**
     * numDetections: array of shape [Batchsize]
     * contains the number of detected boxes
     */
    private float[] mDetectionCount;
    // Buffer for the image data that will contain bytes in RGB ordering
    private final ByteBuffer mImgData;

    private AutoCloseable mCloseable;
    private final Context mContext;
    private final boolean mIsAuto;

    static {
        AUTO_ACCELERATION_TYPES = new ArrayList<>();
        AUTO_ACCELERATION_TYPES.add(AccelerationType.HEXAGON_DSP);
        AUTO_ACCELERATION_TYPES.add(AccelerationType.GPU);
        AUTO_ACCELERATION_TYPES.add(AccelerationType.NONE);
    }

    /**
     * Constructs an {@code ObjectDetector}.
     *
     * Determines buffer sizes and loads the specified model and labels.
     *
     * @param modelFileName The name of the model file. This file will be loaded from the app's assets directory.
     * @param labelFileResId The resource id of the label file stored in '/res/raw/'.
     * @param maxDetectionsPerImage The maximum number of detections per image as indicated by the model.
     * @param inputSize The size of the input the model expects, denoted by inputSize x inputSize.
     * @param numThreads The number of threads that TensorFlow should be instructed to use.
     * @param allowFp16PrecisionForFp32 When set, optimizes memory at the cost of accuracy by using 16 bit floating
     *                                  point numbers rather than 32 bit.
     * @param isQuantized Defines whether the input model is quantized (lossy compressed) or not.
     *                    This is a property of the model and must be set accordingly.
     * @param accelerationType The {@code AccelerationType} that will be used to run inference on images.
     */
    @SuppressWarnings("MagicNumber")
    ObjectDetector(final String modelFileName,
        final @RawRes int labelFileResId,
        final int maxDetectionsPerImage,
        final int inputSize,
        final int numThreads,
        final boolean allowFp16PrecisionForFp32,
        final boolean isQuantized,
        final AccelerationType accelerationType) {
        mContext = TfLiteDetectorApplication.getAppContext();
        mInputSize = inputSize;
        mMaxDetectionsPerImage = maxDetectionsPerImage;
        mIsQuantized = isQuantized;
        final int numBytesPerChannel = mIsQuantized ? 1 : 4;
        // Allocate image buffer using height x width x 3 (from RGB channels) x <size of data>
        mImgData = ByteBuffer.allocateDirect(mInputSize * mInputSize * 3 * numBytesPerChannel);
        // Use endianness of the hardware for the buffer
        mImgData.order(ByteOrder.nativeOrder());
        // Allocate array for image pixel values
        mPixelValues = new int[mInputSize * mInputSize];
        // Check if we are using Auto mode.
        mIsAuto = accelerationType == AccelerationType.AUTO;
        // Initializes the Interpreter as per requested by the user. If Auto mode is used, an optimal AccelerationType is used.
        initializeInterpreter(accelerationType, modelFileName, labelFileResId, numThreads, allowFp16PrecisionForFp32);
    }

    private boolean initializeInterpreter(final AccelerationType accelerationType,
        final String modelFileName,
        final @RawRes int labelFileResId,
        final int numThreads,
        final boolean allowFp16PrecisionForFp32) {
        if (accelerationType == AccelerationType.AUTO) {
            // If using Auto mode, find the optimal AccelerationType and initialize the Interpreter with it.
            initializeOptimalInterpreter(modelFileName, labelFileResId, numThreads, allowFp16PrecisionForFp32);
            return true;
        }
        // Configure TensorFlow interpreter options from parameters
        final Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(numThreads);

        if (accelerationType != AccelerationType.NONE) {
            // Creates a Delegate based on the acceleration type
            final Delegate delegate = createDelegate(accelerationType, allowFp16PrecisionForFp32);
            if (delegate == null) {
                // If the delegate was not instantiated successfully, return false and exit early.
                return false;
            }
            mCloseable = (AutoCloseable)delegate;
            options.addDelegate(delegate);
        }
        try {
            // Load the model
            mModel = new Interpreter(ResourceHelper.loadModelFile(
                    mContext.getAssets(), modelFileName), options);
            // Prepare the labels
            mLabels = ResourceHelper.loadLabels(mContext, labelFileResId);
            new OnObjectDetectorInitializedEvent(accelerationType).broadcastEvent();
            // Successfully initialized the interpreter.
            Log.i(LOGTAG, "ObjectDetector configured with acceleration mode " + accelerationType);
            return true;
        } catch (Exception e) {
            Log.e(LOGTAG, "ObjectDetector failed to initialize with acceleration mode " + accelerationType, e);
            /*
             * Bubble up the error from the Interpreter only if either of the following is true:
             * 1. User did not request {@link AccelerationType#AUTO} mode. (OR)
             * 2. User requested {@link AccelerationType#AUTO} mode and there are no more {@code AccelerationType}s to try.
             */
            if (!mIsAuto || accelerationType == AccelerationType.NONE) {
                new OnObjectDetectorInitializationFailedEvent(accelerationType, e.getMessage()).broadcastEvent();
            }
        }
        // Failed to initialize the interpreter.
        return false;
    }

    private void initializeOptimalInterpreter(final String modelFileName,
        final @RawRes int labelFileResId,
        final int numThreads,
        final boolean allowFp16PrecisionForFp32) {
        // Try each AccelerationType in the list until a working one is found, or an exception is thrown.
        for (AccelerationType accelerationTypeToTry : AUTO_ACCELERATION_TYPES) {
            // Initializes the Interpreter based on the acceleration type
            if (initializeInterpreter(accelerationTypeToTry, modelFileName, labelFileResId, numThreads, allowFp16PrecisionForFp32)) {
                // When the Interpreter is initialized successfully, break out of the loop.
                break;
            }
        }
    }

    private Delegate createDelegate(final AccelerationType accelerationType, final boolean allowFp16PrecisionForFp32) {
        Delegate delegate = null;
        switch (accelerationType) {
        case NNAPI:
            // Add the NNAPI Delegate
            final NnApiDelegate.Options nnApiDelegateOptions = new NnApiDelegate.Options();
            nnApiDelegateOptions.setAllowFp16(allowFp16PrecisionForFp32);
            delegate = new NnApiDelegate(nnApiDelegateOptions);
            break;
        case GPU:
            // If the device has a supported GPU, add the GPU delegate.
            final GpuDelegate.Options delegateOptions = new CompatibilityList().getBestOptionsForThisDevice();
            /*
             * To enable GPU support for quantized models, set 'setQuantizedModelsAllowed()' to true.
             * While the TensorFlow documentation states that this is set to true by default, our empirical
             * testing has shown that it is not.
             */
            delegateOptions.setQuantizedModelsAllowed(mIsQuantized);
            /*
             * For the GpuDelegate to work correctly, it is important to create the GpuDelegate with GpuDelegate.Options.
             * If the options are omitted, inference will not be executed on the GPU.
             */
            delegate = new GpuDelegate(delegateOptions);
            break;
        case HEXAGON_DSP:
            delegate = createHexagonDelegate();
            break;
        default:
        }
        return delegate;
    }

    private Delegate createHexagonDelegate() {
        Delegate delegate = null;
        // Check if the libraries required for Hexagon Delegate exist
        if (!BuildConfig.DO_HEXAGON_DELEGATE_FILES_EXIST) {
            if (!mIsAuto) {
                new OnObjectDetectorInitializationFailedEvent(
                    AccelerationType.HEXAGON_DSP, mContext.getString(R.string.hexagon_dsp_files_missing_error)).broadcastEvent();
            }
            return delegate;
        }
        // Check if the model is quantized
        if (!mIsQuantized) {
            if (!mIsAuto) {
                new OnObjectDetectorInitializationFailedEvent(
                    AccelerationType.HEXAGON_DSP, mContext.getString(R.string.hexagon_dsp_model_not_quantized_error)).broadcastEvent();
            }
            return delegate;
        }
        try {
            delegate = new HexagonDelegate(mContext);
        } catch (UnsupportedOperationException e) {
            // If Hexagon delegate is not supported on this device, send an event to bubble this up to users.
            Log.e(LOGTAG, "Hexagon Delegate is not supported on this device.", e);
            if (!mIsAuto) {
                new OnObjectDetectorInitializationFailedEvent(
                    AccelerationType.HEXAGON_DSP, mContext.getString(R.string.hexagon_dsp_build_error)).broadcastEvent();
            }
        } catch (UnsatisfiedLinkError e) {
            // Libraries required for Hexagon delegate are missing from the app.
            Log.e(LOGTAG, "Libraries required for Hexagon Delegate are missing. "
                + "See https://www.tensorflow.org/lite/performance/hexagon_delegate for more information.", e);
            if (!mIsAuto) {
                new OnObjectDetectorInitializationFailedEvent(
                    AccelerationType.HEXAGON_DSP, mContext.getString(R.string.hexagon_dsp_files_missing_error)).broadcastEvent();
            }
        }
        return delegate;
    }

    /**
     * Destroys the {@code ObjectDetector}, releasing all the internally allocated resources.
     */
    void destroy() {
        // Close the model.
        if (mModel != null) {
            mModel.close();
        }
        // Clear the labels.
        if (mLabels != null) {
            mLabels.clear();
        }
        // Close the Delegate.
        if (mCloseable != null) {
            try {
                mCloseable.close();
            } catch (Exception e) {
                Log.e(LOGTAG, "Could not close delegate", e);
            }
        }
    }

    /**
     * Runs inference on a bitmap
     * 1. Performs some data preprocessing
     * Populates the `imgData` input array with bytes from the bitmap in RGB order
     * Normalizes data if necessary
     *
     * 2. Inference
     * Sets up inputs and outputs for the TensorFlow lite api `runForMultipleInputsOutputs`
     * https://www.tensorflow.org/lite/guide/inference
     *
     * Inputs
     * Object[1]: our imgData array is the sole element
     *
     * Outputs (in index order)
     * Location array: format is [top, left, bottom, right]
     * Object class id: mapped to human readable label using the label map
     * Confidence: float in range from 0 to 1
     * Detection Count: how many objects were detected in the frame
     *
     * 3. Maps outputs to
     * {@link Recognition} objects
     * for easier use.
     *
     * @param bitmap The image {@code Bitmap} to run inference on.
     * @return A {@code List<Recognition>} containing all the recognized objects.
     */
    @SuppressWarnings("MagicNumber")
    List<Recognition> recognizeImage(final Bitmap bitmap) {
        // Return the recognitions in an accessible format
        final List<Recognition> recognitions = new ArrayList<>(mMaxDetectionsPerImage);
        if (mModel == null) {
            return recognitions;
        }
        // Preprocess the image data from 0-255 int to normalized value based on the provided parameters.
        bitmap.getPixels(mPixelValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        mImgData.rewind();
        for (int i = 0; i < mInputSize; ++i) {
            for (int j = 0; j < mInputSize; ++j) {
                // Get the data for the jth pixel in the ith row of the image
                final int pixelValue = mPixelValues[i * mInputSize + j];
                if (mIsQuantized) {
                    // Quantized model
                    mImgData.put((byte)((pixelValue >> SHIFT_RED) & BYTE_MASK));
                    mImgData.put((byte)((pixelValue >> SHIFT_GREEN) & BYTE_MASK));
                    mImgData.put((byte)(pixelValue & BYTE_MASK));
                } else {
                    // Float model
                    mImgData.putFloat((((pixelValue >> SHIFT_RED) & BYTE_MASK) - IMAGE_MED) / IMAGE_MED);
                    mImgData.putFloat((((pixelValue >> SHIFT_GREEN) & BYTE_MASK) - IMAGE_MED) / IMAGE_MED);
                    mImgData.putFloat(((pixelValue & BYTE_MASK) - IMAGE_MED) / IMAGE_MED);
                }
            }
        }

        final Object[] inputArray = {mImgData};

        // Allocate buffers
        mOutputLocations = new float[inputArray.length][mMaxDetectionsPerImage][4];
        mOutputClasses = new float[inputArray.length][mMaxDetectionsPerImage];
        mOutputScores = new float[inputArray.length][mMaxDetectionsPerImage];
        mDetectionCount = new float[inputArray.length];
        final Map<Integer, Object> outputMap = new HashMap<>();
        /*
         * Build output map to reflect the tensors trained in the model. This model has the order locations, classes,
         * scores, and count.
         */
        outputMap.put(0, mOutputLocations);
        outputMap.put(1, mOutputClasses);
        outputMap.put(2, mOutputScores);
        outputMap.put(3, mDetectionCount);

        /*
         * Accepts the formatted ByteBuffer as an inputArray, and runs an inference to populate detection data in the
         * outputMap
         */
        mModel.runForMultipleInputsOutputs(inputArray, outputMap);

        for (int i = 0; i < mMaxDetectionsPerImage; ++i) {
            // Return coordinates relative to the detection area
            final RectF detection =
                new RectF(
                mOutputLocations[0][i][1],
                mOutputLocations[0][i][0],
                mOutputLocations[0][i][3],
                mOutputLocations[0][i][2]);
            /*
             * SSD Mobilenet V1 Model assumes class 0 is background class
             * in label file and class labels start from 1 to number_of_classes+1,
             * while outputClasses correspond to class index from 0 to number_of_classes
             */
            recognitions.add(
                new Recognition(
                String.valueOf(i),
                mLabels.get((int)mOutputClasses[0][i]),
                mOutputScores[0][i],
                detection));
        }
        return recognitions;
    }

    /**
     * Gets the size of image as required by the detector.
     *
     * @return A {@code Size} that defines the expected height and width of the input image.
     */
    Size getRequiredImageSize() {
        return new Size(mInputSize, mInputSize);
    }
}
