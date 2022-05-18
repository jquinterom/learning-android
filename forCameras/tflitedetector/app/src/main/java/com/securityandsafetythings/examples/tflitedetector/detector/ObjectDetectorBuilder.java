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

import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;

/**
 * Configures and builds an {@link ObjectDetector}.
 */
public class ObjectDetectorBuilder {
    //private String mModelFileName = "detect.tflite";
    private String mModelFileName = "bird.tflite";
    private int mLabelFileResId = R.raw.labelmap;
    @SuppressWarnings("MagicNumber")
    private int mMaxDetectionsPerImage = 10;
    @SuppressWarnings("MagicNumber")
    private int mInputSize = 300;
    @SuppressWarnings("MagicNumber")
    private int mNumThreads = 4;
    private boolean mAllowFp16PrecisionForFp32 = false;
    private boolean mIsQuantized = false;
    private AccelerationType mAccelerationType = AccelerationType.AUTO;

    /**
     * Builds an {@code ObjectDetector} with the specified configuration.
     *
     * @return A ready-to-use {@code ObjectDetector}.
     */
    ObjectDetector build() {
        return new ObjectDetector(mModelFileName,
            mLabelFileResId,
            mMaxDetectionsPerImage,
            mInputSize,
            mNumThreads,
            mAllowFp16PrecisionForFp32,
            mIsQuantized,
            mAccelerationType);
    }

    /**
     * Sets the name of the file that represents the model.
     * Default value: "detect.tflite"
     *
     * @param name The name of the file that represents the model.
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder setModelFileName(final String name) {
        mModelFileName = name;
        return this;
    }

    /**
     * Sets the {@code AccelerationType} to use.
     * Default value: {@link AccelerationType#AUTO}
     *
     * @param accelerationType The {@code AccelerationType} that the ObjectDetector must be configured with.
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder setAccelerationType(final AccelerationType accelerationType) {
        mAccelerationType = accelerationType;
        return this;
    }

    /**
     * Sets the label file to use with the model.
     * Default value: R.raw.labelmap
     *
     * @param resId Resource id of the label file.
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder setLabelFileResourceId(final int resId) {
        mLabelFileResId = resId;
        return this;
    }

    /**
     * Sets the maximum number of detections for a single image as defined in the model.
     * Default value: 10
     *
     * @param maxDetectionsPerImage The maximum number of object detections per image as defined in the model.
     * @return This builder as a convenience for call chaining.
     */
    public ObjectDetectorBuilder setMaxDetectionsPerImage(final int maxDetectionsPerImage) {
        mMaxDetectionsPerImage = maxDetectionsPerImage;
        return this;
    }

    /**
     * Sets input size the detector expects.
     * Default value: 300
     *
     * @param size The model's input size n, in nxn.
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder setInputSize(final int size) {
        mInputSize = size;
        return this;
    }

    /**
     * Sets the number of threads to use.
     * Default value: 4
     *
     * @param n Number of threads.
     * @return This builder as a convenience for call chaining.
     */
    public ObjectDetectorBuilder setNumThreads(final int n) {
        mNumThreads = n;
        return this;
    }

    /**
     * Configures the detector to use 16 bit precision for 32 bit values to save on space.
     * Default value: false
     *
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder allowFp16PrecisionForFp32() {
        mAllowFp16PrecisionForFp32 = true;
        return this;
    }

    /**
     * Sets whether the model in question is quantized (lossy compressed) or not.
     * Default value: false
     *
     * @param isQuantized A boolean specifying whether the model is quantized.
     * @return This builder as a convenience for call chaining.
     */
    ObjectDetectorBuilder setIsQuantized(final boolean isQuantized) {
        mIsQuantized = isQuantized;
        return this;
    }
}
