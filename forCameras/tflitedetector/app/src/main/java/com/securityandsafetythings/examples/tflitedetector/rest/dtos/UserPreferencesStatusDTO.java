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

/**
 * A DTO that represents the result of updating user preferences.
 */
public class UserPreferencesStatusDTO {
    @SerializedName("message")
    private final String mMessage;
    @SerializedName("isSuccessful")
    private final boolean mIsSuccessful;

    /**
     * Constructs a {@code UserPreferencesStatusDTO}.
     *
     * @param message String which describes the status.
     * @param isSuccessful Boolean which indicates whether the status is successful or not.
     */
    public UserPreferencesStatusDTO(final String message, final boolean isSuccessful) {
        mMessage = message;
        mIsSuccessful = isSuccessful;
    }

    /**
     * Gets the message.
     *
     * @return String which describes the status.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Gets whether the status is successful or not.
     *
     * @return True if everything built successfully, otherwise false.
     */
    public boolean isSuccessful() {
        return mIsSuccessful;
    }
}
