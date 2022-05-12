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

package com.securityandsafetythings.examples.tflitedetector.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.securityandsafetythings.examples.tflitedetector.TfLiteDetectorApplication;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;
import com.securityandsafetythings.examples.tflitedetector.events.OnPreferencesStoreUpdatedEvent;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.UserPreferencesDTO;

/**
 * Utility class for storing preferences.
 */
public final class EasySharedPreference {

    // Singleton instance for this class
    private static EasySharedPreference sInstance = null;

    // String key for accessing confidence in EasySharedPreference
    private static final String PREF_KEY_MIN_CONFIDENCE = "pref_key_min_confidence";
    private static final String PREF_KEY_ACCELERATIONTYPE = "pref_key_accelerationtype";

    // Default value for AccelerationType
    private static final AccelerationType DEFAULT = AccelerationType.AUTO;

    // Default confidence threshold
    @SuppressWarnings("MagicNumber")
    private static final float DEFAULT_CONFIDENCE = 0.5f;

    // Key value store for persisting applications preferences
    private final SharedPreferences mSharedPrefs;

    /**
     * Private constructor for Singleton. Uses the application context to
     * retrieve a SharedPreferences object for this class.
     */
    private EasySharedPreference() {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(TfLiteDetectorApplication.getAppContext());
    }

    /**
     * Gets an instance of this class.
     *
     * @return The Singleton instance of this class.
     */
    public static synchronized EasySharedPreference getInstance() {

        // If no instance exists create one. Else return existing instance
        if (sInstance == null) {
            sInstance = new EasySharedPreference();
        }
        return sInstance;
    }

    /**
     * Gets the current user preferences.
     *
     * @return A {@code UserPreferencesDTO} object containing the currently active user preferences.
     */
    public UserPreferencesDTO getUserPreferences() {
        return new UserPreferencesDTO(getMinConfidenceLevel(), getRequestedAccelerationType().toString());
    }

    /**
     * Stores user preferences from a given {@code UserPreferencesDTO}.
     *
     * @param userPreferencesDto A {@code UserPreferencesDTO} object containing new user preferences to be set.
     */
    public void storeUserPreferences(final UserPreferencesDTO userPreferencesDto) {
        setMinConfidenceLevel(userPreferencesDto.getConfidence());
        setRequestedAccelerationType(AccelerationType.fromString(userPreferencesDto.getAccelerationType()).ordinal());
        new OnPreferencesStoreUpdatedEvent().broadcastEvent();
    }

    /**
     * Gets the minimum confidence level that detections must meet so as to be rendered.
     *
     * @return A float representing the confidence threshold.
     */
    public float getMinConfidenceLevel() {
        return mSharedPrefs.getFloat(PREF_KEY_MIN_CONFIDENCE, DEFAULT_CONFIDENCE);
    }

    /**
     * Sets the minimum confidence level that detections must meet so as to be rendered.
     *
     * @param confidence A float representing the confidence threshold.
     */
    private void setMinConfidenceLevel(final float confidence) {
        mSharedPrefs.edit().putFloat(PREF_KEY_MIN_CONFIDENCE, confidence).apply();
    }

    /**
     * Gets the {@code AccelerationType} that was requested to be used for inference.
     *
     * @return The {@code AccelerationType} that was requested to be used for inference.
     */
    public AccelerationType getRequestedAccelerationType() {
        return AccelerationType.fromOrdinal(mSharedPrefs.getInt(PREF_KEY_ACCELERATIONTYPE, DEFAULT.ordinal()));
    }

    /**
     * Stores the {@link AccelerationType} that was requested by the user to use for inference.
     *
     * @param accelerationTypeOrdinal The ordinal value of the {@code AccelerationType} that was requested by the user to use for inference.
     */
    private void setRequestedAccelerationType(final int accelerationTypeOrdinal) {
        mSharedPrefs.edit().putInt(PREF_KEY_ACCELERATIONTYPE, accelerationTypeOrdinal).apply();
    }
}
