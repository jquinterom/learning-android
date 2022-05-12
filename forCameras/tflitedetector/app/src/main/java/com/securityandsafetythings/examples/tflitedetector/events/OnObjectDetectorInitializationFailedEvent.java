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

import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;

/**
 * An event indicating that the {@code ObjectDetector} failed to initialize.
 */
public class OnObjectDetectorInitializationFailedEvent extends BaseEvent {
    private final String mErrorMessage;
    private final AccelerationType mAccelerationType;

    /**
     * Constructs an {@code OnObjectDetectorInitializationFailedEvent}.
     *
     * @param accelerationType The {@code AccelerationType} that was used to configure the {@code ObjectDetector}.
     * @param errorMessage A message that indicates why the {@code ObjectDetector} failed to initialize.
     */
    public OnObjectDetectorInitializationFailedEvent(final AccelerationType accelerationType, final String errorMessage) {
        mAccelerationType = accelerationType;
        mErrorMessage = errorMessage;
    }

    /**
     * Gets the {@code AccelerationType} that was used to configure the {@code ObjectDetector}.
     *
     * @return The {@code AccelerationType} that was used to configure the {@code ObjectDetector}.
     */
    public AccelerationType getAccelerationType() {
        return mAccelerationType;
    }

    /**
     * Gets the message that indicates why the {@code ObjectDetector} failed to initialize.
     *
     * @return The message that indicates why the {@code ObjectDetector} failed to initialize.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }
}
