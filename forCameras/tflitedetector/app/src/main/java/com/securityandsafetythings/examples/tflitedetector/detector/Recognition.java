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

import android.graphics.RectF;
import java.util.Locale;

/**
 * A result returned by a Classifier describing what was recognized.
 */
public class Recognition {
    // A unique identifier for what has been recognized. Specific to the image
    private final String mId;

    // Display name for the recognition.
    private final String mLabel;

    // A sortable score for how good the recognition is relative to others. Higher should be better.
    private final Float mConfidence;

    // Optional location within the source image for the location of the recognized object.
    private RectF mLocation;

    /**
     * A single recognized object
     * @param id Identifier for the object
     * @param label The name of the object recognized
     * @param confidence Value from 0-1 how strong the confidence is for the detection
     * @param location Bounding box for the object
     */
    Recognition(
        final String id, final String label, final Float confidence, final RectF location) {
        mId = id;
        mLabel = label;
        mConfidence = confidence;
        mLocation = location;
    }

    /**
     * Gets recognition id
     * @return String id of the object
     */
    public String getId() {
        return mId;
    }

    /**
     * Gets the label for the object
     * @return Object class
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Gets object confidence score
     * @return 0-1 value indicating confidence
     */
    public Float getConfidence() {
        return mConfidence;
    }

    /**
     * Gets a bounding box specifying the detection location
     * @return Bounding box
     */
    public RectF getLocation() {
        return new RectF(mLocation);
    }

    /**
     * Sets the location for a detection
     * @param location Rectangle specifying the location
     */
    public void setLocation(final RectF location) {
        mLocation = location;
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public String toString() {
        final char space = ' ';
        final StringBuilder resultString = new StringBuilder();
        if (mId != null) {
            resultString.append("[").append(mId).append("] ");
        }

        if (mLabel != null) {
            resultString.append(mLabel).append(space);
        }

        if (mConfidence != null) {
            resultString.append(String.format(Locale.US, "(%.1f%%) ", mConfidence * 100.0f));
        }

        if (mLocation != null) {
            resultString.append(mLocation).append(space);
        }

        return resultString.toString().trim();
    }
}