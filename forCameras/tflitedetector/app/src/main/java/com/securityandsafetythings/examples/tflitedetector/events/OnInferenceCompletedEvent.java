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

package com.securityandsafetythings.examples.tflitedetector.events;

/**
 * An event indicating that the inference has been completed.
 */
public class OnInferenceCompletedEvent extends BaseEvent {
    // A byte array that represents the image annotated with bounding boxes.
    private final byte[] mImageBytes;
    // The time taken by the detector to run the inference.
    private final long mInferenceTime;
    // The average of the number of frames being processed per second.
    private final int mFramesProcessedPerSecond;

    /**
     * Constructs an {@code OnInferenceCompletedEvent}.
     *
     * @param imageAsBytes A byte array that represents the image annotated with bounding boxes.
     * @param inferenceTime The time taken by the detector to run the inference.
     * @param framesProcessedPerSecond The average of the number of frames being processed per second.
     */
    public OnInferenceCompletedEvent(final byte[] imageAsBytes, final long inferenceTime, final int framesProcessedPerSecond) {
        mImageBytes = imageAsBytes;
        mInferenceTime = inferenceTime;
        mFramesProcessedPerSecond = framesProcessedPerSecond;
    }

    /**
     * Gets a {@code byte[]} that represents the image annotated with bounding boxes.
     *
     * @return A {@code byte[]} that represents the image annotated with bounding boxes.
     */
    public byte[] getImageAsBytes() {
        return mImageBytes;
    }

    /**
     * Gets the inference time.
     *
     * @return The time taken for inference.
     */
    public long getInferenceTime() {
        return mInferenceTime;
    }

    /**
     * Gets the average of the number of frames being processed per second.
     *
     * @return The average of the number of frames being processed per second.
     */
    public int getFramesProcessedPerSecond() {
        return mFramesProcessedPerSecond;
    }
}
