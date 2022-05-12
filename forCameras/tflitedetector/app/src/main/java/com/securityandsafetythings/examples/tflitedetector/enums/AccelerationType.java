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

package com.securityandsafetythings.examples.tflitedetector.enums;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.TfLiteDetectorApplication;

/**
 * Enum defining the Acceleration types available
 */
public enum AccelerationType {
    /**
     * Automatically determine an acceleration mode from left to right from the list of [HEXAGON_DSP, GPU, NONE].
     */
    AUTO(R.string.acceleration_type_auto_description),
    /**
     * DSP acceleration - uses Hexagon DSP to accelerate inference times.
     */
    HEXAGON_DSP(R.string.acceleration_type_hexagon_dsp_description),
    /**
     * GPU acceleration - uses GPU Delegate to accelerate inference times.
     */
    GPU(R.string.acceleration_type_gpu_description),
    /**
     * No acceleration, executes on CPU.
     */
    NONE(R.string.acceleration_type_none_description),
    /**
     * NNAPI acceleration - uses NNAPI to execute on the most optimal hardware available.
     */
    NNAPI(R.string.acceleration_type_nnapi_description, true);

    private static final AccelerationType[] VALUES = values();

    private static JsonArray sValuesAsJSON;

    private final String mDescription;
    private final boolean mIsModernAPI;

    AccelerationType(final int descriptionResId) {
        this(descriptionResId, false);
    }

    AccelerationType(final int descriptionResId, final boolean isModernAPI) {
        mDescription = TfLiteDetectorApplication.getAppContext().getString(descriptionResId);
        mIsModernAPI = isModernAPI;
    }

    /**
     * Override the {@code toString()} method to set user-friendly display names for certain
     * {@code AccelerationType}s.
     *
     * @return A user-friendly name for the {@code AccelerationType}.
     */
    @Override
    public String toString() {
        switch(this) {
        case AUTO:
            return TfLiteDetectorApplication.getAppContext().getString(R.string.acceleration_type_auto_name);
        case HEXAGON_DSP:
            return TfLiteDetectorApplication.getAppContext().getString(R.string.acceleration_type_hexagon_dsp_name);
        case NONE:
            return TfLiteDetectorApplication.getAppContext().getString(R.string.acceleration_type_none_name);
        default:
        }
        return super.toString();
    }

    /**
     * Gets the {@code AccelerationType} when given an ordinal value.
     *
     * @param ordinal The ordinal value of an {@code AccelerationType}.
     * @return An {@code AccelerationType} that corresponds to the given ordinal value.
     */
    public static AccelerationType fromOrdinal(final int ordinal) {
        return VALUES[ordinal];
    }

    /**
     * Gets the {@code AccelerationType} corresponding to the given {@code String} value, if one exists.
     *
     * @param string A {@code String} for which the corresponding {@code AccelerationType} must be obtained.
     * @return The {@code AccelerationType} corresponding to the given {@code String}, if one exists. Otherwise, null.
     */
    public static AccelerationType fromString(final String string) {
        for (AccelerationType accelerationType : VALUES) {
            if (accelerationType.toString().equals(string)) {
                return accelerationType;
            }
        }
        return null;
    }

    /**
     * Gets a {@code JsonArray} containing all available {@link AccelerationType}s along with their metadata.
     *
     * @return A {@code JsonArray} containing all available {@code AccelerationType}s with their metadata.
     */
    public static JsonArray getValuesAsJSON() {
        if (sValuesAsJSON == null) {
            sValuesAsJSON = new JsonArray();
            for (AccelerationType accelerationType : VALUES) {
                // Create JSON Object for this AccelerationType.
                final JsonObject jsonObject = new JsonObject();
                // Use toString() to obtain a user-friendly display name.
                jsonObject.addProperty("name", accelerationType.toString());
                jsonObject.addProperty("description", accelerationType.mDescription);
                jsonObject.addProperty("isModernAPI", accelerationType.mIsModernAPI);
                sValuesAsJSON.add(jsonObject);
            }
        }
        return sValuesAsJSON;
    }
}