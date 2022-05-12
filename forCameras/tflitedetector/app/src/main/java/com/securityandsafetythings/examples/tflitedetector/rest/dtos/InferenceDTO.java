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

package com.securityandsafetythings.examples.tflitedetector.rest.dtos;

import com.google.gson.annotations.SerializedName;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;

/**
 * A DTO that represents inference statistics.
 * inferenceTime: The time it took in milliseconds to run inference on an image.
 * framesProcessedPerSecond: The average of the number of frames being processed per second.
 * requestedFramesPerSecond: The requested number of frames per second from the VideoPipeline.
 * accelerationType: The {@link AccelerationType} that was used for the inference. For example, if the user requested
 *                   {@link AccelerationType#AUTO} and if the inference was run using {@link AccelerationType#GPU}, then
 *                   this will be formatted as "Auto (GPU)". See {@link AccelerationType#toString()}.
 */
public class InferenceDTO {
    @SerializedName("inferenceTime")
    private final long mInferenceTime;
    @SerializedName("framesProcessedPerSecond")
    private final int mFramesProcessedPerSecond;
    @SerializedName("requestedFramesPerSecond")
    private final int mRequestedFramesPerSecond;
    @SerializedName("accelerationType")
    private final String mAccelerationType;

    /**
     * Creates an {@code InferenceDTO} with results from inference.
     *
     * @param inferenceTime The time in ms the inference took to execute.
     * @param framesProcessedPerSecond The running average of the number of frames being processed per second.
     * @param requestedFramesPerSecond The requested number of frames per second from the VideoPipeline.
     * @param accelerationType The {@link AccelerationType} that was used for the inference. For example, if the user requested
     *                         {@link AccelerationType#AUTO}, and if the inference was run using {@link AccelerationType#GPU},
     *                         then this will be formatted as "Auto (GPU)". See {@link AccelerationType#toString()}.
     */
    public InferenceDTO(final long inferenceTime,
        final int framesProcessedPerSecond,
        final int requestedFramesPerSecond,
        final String accelerationType) {
        mInferenceTime = inferenceTime;
        mFramesProcessedPerSecond = framesProcessedPerSecond;
        mRequestedFramesPerSecond = requestedFramesPerSecond;
        mAccelerationType = accelerationType;
    }
}
