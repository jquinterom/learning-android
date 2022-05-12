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

package com.securityandsafetythings.examples.tflitedetector.rest;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.google.gson.JsonArray;

import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.TfLiteDetectorApplication;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.InferenceDTO;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.UserPreferencesDTO;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.UserPreferencesStatusDTO;
import com.securityandsafetythings.examples.tflitedetector.utilities.EasySharedPreference;
import com.securityandsafetythings.web_components.webserver.utilities.ProducesHeader;

import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CountDownLatch;

/**
 * Class responsible to receive API calls from the front end, process it, and return the result.
 */
@Path("example")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestEndPoint {
    private static final String LOGTAG = RestEndPoint.class.getSimpleName();
    private byte[] mBitmapBytes;
    private InferenceDTO mInferenceDTO;
    private UserPreferencesStatusDTO mUserPreferencesStatusDTO;
    private CountDownLatch mDetectorInitializationSignal;

    /**
     * Gets the statistics from running inference on a frame.
     *
     * @return An instance of {@code InferenceDTO}.
     */
    @GET
    @Path("inference-statistics")
    public InferenceDTO getInferenceDTO() {
        return mInferenceDTO;
    }

    /**
     * Sets the most recently received {@link Image} annotated with bounding boxes and the statistics from running
     * inference on this image.
     *
     * @param compressedImageBytes A byte array containing the compressed {@link Bitmap} image annotated with bounding
     *                             boxes from running inference.
     * @param inferenceDTO         The {@code InferenceDTO} object containing the statistics from running inference
     *                             on the given {@code Bitmap}.
     */
    public synchronized void setImageAndStatistics(final byte[] compressedImageBytes, final InferenceDTO inferenceDTO) {
        mBitmapBytes = compressedImageBytes;
        mInferenceDTO = inferenceDTO;
    }

    /**
     * Gets the most recent {@link Image} annotated with bounding boxes from running inference as a byte[].
     *
     * @return A {@code byte[]} representing the {@link Bitmap}.
     */
    @SuppressWarnings("MagicNumber")
    @GET
    @Path("live")
    @Produces("image/jpeg")
    @ProducesHeader("Cache-Control: max-age=5")
    public synchronized byte[] getImage() {
        if (mBitmapBytes == null) {
            throw new NotFoundException();
        }
        return mBitmapBytes;
    }

    /**
     * Gets the most recently selected user preferences.
     *
     * @return The most recently selected user preferences.
     */
    @GET
    @Path("user-preferences")
    public UserPreferencesDTO getUserPreferences() {
        return EasySharedPreference.getInstance().getUserPreferences();
    }

    /**
     * Updates the backend with the most recently selected user preferences.
     * Waits for {@link #setUserPreferencesStatusDTO(UserPreferencesStatusDTO)}
     * to return the status that gets set.
     *
     * @param userPreferencesDTO A DTO that contains the preferences selected by the user.
     * @return The {@code UserPreferencesStatusDTO} containing the result of the attempted update.
     */
    @POST
    @Path("user-preferences")
    public UserPreferencesStatusDTO updateUserPreferences(final UserPreferencesDTO userPreferencesDTO) {
        mDetectorInitializationSignal = new CountDownLatch(1);
        // Change settings.
        EasySharedPreference.getInstance().storeUserPreferences(userPreferencesDTO);
        try {
            Log.i(LOGTAG, String.format("Await called in updateUserPreferences for acceleration type %s",
                    userPreferencesDTO.getAccelerationType()));
            // Wait until latch has counted down to 0.
            mDetectorInitializationSignal.await();
            Log.i(LOGTAG, String.format( "Await unblocked for acceleration type %s",
                    userPreferencesDTO.getAccelerationType()));
        } catch (InterruptedException e) {
            Log.e(LOGTAG, "Interrupted exception");
            mUserPreferencesStatusDTO = new UserPreferencesStatusDTO(
                    TfLiteDetectorApplication.getAppContext().getString(R.string.object_detector_initialization_thread_interruption), false);
        }
        Log.i(LOGTAG, String.format("Return value is %s for acceleration type %s",
                mUserPreferencesStatusDTO.getMessage(), userPreferencesDTO.getAccelerationType()));
        return mUserPreferencesStatusDTO;
    }

    /**
     * Sets the {@code UserPreferencesStatusDTO} containing the status of the last user preferences update.
     *
     * @param userPreferencesStatusDTO A DTO that contains the status of the last user preferences update.
     */
    public void setUserPreferencesStatusDTO(final UserPreferencesStatusDTO userPreferencesStatusDTO) {
        mUserPreferencesStatusDTO = userPreferencesStatusDTO;
        if (mDetectorInitializationSignal != null) {
            // Countdown will cause count to become 0, which will unblock the updateUserPreferences method waiting on this signal.
            mDetectorInitializationSignal.countDown();
        }
    }

    /**
     * Gets the status of the last user preferences update.
     *
     * @return The status of the last user preferences update.
     */
    @GET
    @Path("user-preferences-status")
    public UserPreferencesStatusDTO getUserPreferencesStatus() {
        return mUserPreferencesStatusDTO;
    }

    /**
     * Gets a {@code JsonArray} of the supported acceleration types along with their metadata.
     *
     * @return A {@code JsonArray} containing the supported acceleration types along with their metadata.
     */
    @GET
    @Path("acceleration-types")
    public JsonArray getAccelerationTypes() {
        return AccelerationType.getValuesAsJSON();
    }

    @GET
    @Path("hello-jhon")
    public String getGreet() {
        return "Hello Jhon!";
    }

}
