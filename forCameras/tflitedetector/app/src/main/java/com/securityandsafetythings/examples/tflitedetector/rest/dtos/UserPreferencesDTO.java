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
 * A DTO that represents the user preferences for this app.
 */
public class UserPreferencesDTO {
    @SerializedName("confidence")
    private final float mConfidence;
    @SerializedName("accelerationType")
    private final String mAccelerationType;

    /**
     * Constructs a {@code UserPreferencesDTO}.
     *
     * @param confidence The minimum confidence level that detections must meet so as to be rendered.
     * @param accelerationType The {@link AccelerationType} to be used for inference. See {@link AccelerationType#toString()}.
     */
    public UserPreferencesDTO(final float confidence, final String accelerationType) {
        mConfidence = confidence;
        mAccelerationType = accelerationType;
    }

    /**
     * Gets the minimum confidence level that detections must meet so as to be rendered.
     *
     * @return A float representing the confidence threshold.
     */
    public float getConfidence() {
        return mConfidence;
    }

    /**
     * Gets the {@link AccelerationType} to be used for inference. See {@link AccelerationType#toString()}.
     *
     * @return The {@code AccelerationType} to be used for inference.
     */
    public String getAccelerationType() {
        return mAccelerationType;
    }
}