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

package com.securityandsafetythings.examples.tflitedetector.services;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;

import com.securityandsafetythings.Build;
import com.securityandsafetythings.app.VideoService;
import com.securityandsafetythings.examples.tflitedetector.BuildConfig;
import com.securityandsafetythings.examples.tflitedetector.R;
import com.securityandsafetythings.examples.tflitedetector.detector.InferenceHandler;
import com.securityandsafetythings.examples.tflitedetector.enums.AccelerationType;
import com.securityandsafetythings.examples.tflitedetector.events.*;
import com.securityandsafetythings.examples.tflitedetector.rest.RestEndPoint;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.InferenceDTO;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.InfoImageDTO;
import com.securityandsafetythings.examples.tflitedetector.rest.dtos.UserPreferencesStatusDTO;
import com.securityandsafetythings.examples.tflitedetector.utilities.EasySharedPreference;
import com.securityandsafetythings.jumpsuite.commonhelpers.BitmapUtils;
import com.securityandsafetythings.video.RefreshRate;
import com.securityandsafetythings.video.VideoCapture;
import com.securityandsafetythings.video.VideoManager;
import com.securityandsafetythings.video.VideoSession;
import com.securityandsafetythings.web_components.webserver.RestHandler;
import com.securityandsafetythings.web_components.webserver.WebServerConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * This class responds to {@link #onCreate()} and {@link #onDestroy()} methods of the application
 * lifecycle. In order to receive images from the VideoPipeline, {@link MainService} extends
 * {@link VideoService} and implements its callbacks.
 *
 * The three callbacks are:
 *
 * 1. {@link #onVideoAvailable(VideoManager)} - Triggered when the {@link VideoManager} is available to request video sessions.
 * 2. {@link #onImageAvailable(ImageReader)} - Triggered when a new {@link Image} is available.
 * 3. {@link #onVideoClosed(VideoSession.CloseReason)} - Triggered when the video session is closed.
 */
public class MainService extends VideoService {

    private static final String LOGTAG = MainService.class.getSimpleName();
    private static final String INFERENCE_THREAD_NAME = String.format("%s%s",
        MainService.class.getSimpleName(), "InferenceThread");
    private WebServerConnector mWebServerConnector;
    private RestEndPoint mRestEndPoint;
    private VideoCapture mCapture;
    private Size mCaptureSize;
    private InferenceHandler mInferenceHandler;
    private HandlerThread mInferenceHandlerThread;
    private AccelerationType mInitializedAccelerationType;

    /**
     * {@link #onCreate()} initializes our {@link WebServerConnector}, {@link RestEndPoint}, and
     * {@link RestHandler}.
     *
     * The {@link WebServerConnector} acts as the bridge between our application and the webserver
     * which is contained in the Azena SDK.
     *
     * The {@link RestEndPoint} is a class annotated with JaxRs endpoint annotations. This is the class
     * that we interact with via HTTP on the front end.
     *
     * The {@link RestHandler} acts as a wrapper class for our {@link RestEndPoint}. The Handler registers our
     * {@link RestEndPoint}, and connects it to the WebServer.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInitializedAccelerationType = null;
        // Creates a RestHandler with a base path of 'app/getPackageName()'.
        final RestHandler restHandler = new RestHandler(this, BuildConfig.WEBSITE_ASSET_PATH);
        /*
         * Registers the RestEndPoint with the server via the RestHandler class. The RestHandler
         * is just a wrapper for the RestEndPoint's JaxRs annotated functions.
         */
        mRestEndPoint = new RestEndPoint();
        restHandler.register(mRestEndPoint);
        // Connects the RestHandler with the WebServerConnector.
        mWebServerConnector = new WebServerConnector(this);
        mWebServerConnector.connect(restHandler);
        EventBus.getDefault().register(this);
    }

    /**
     * Subscribe to OnPreferenceStoreUpdatedEvent to re-configure the detector when user preferences are updated.
     *
     * @param onPreferencesStoreUpdatedEvent An {@code OnPreferenceStoreUpdatedEvent} object.
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(final OnPreferencesStoreUpdatedEvent onPreferencesStoreUpdatedEvent) {
        // Whenever user preferences are changed, the detector must be re-configured.
        configureDetector();
    }

    /**
     * Subscribe to StartVideoSessionActionEvent to start the video session upon receiving event.
     *
     * @param startVideoSessionActionEvent A {@code StartVideoSessionActionEvent} object.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final StartVideoSessionActionEvent startVideoSessionActionEvent) {
        // Request the VideoPipeline for images with HD resolution (1920x1080) at a LIVE RefreshRate i.e. 30 FPS
        openVideo(mCapture, mCaptureSize.getWidth(), mCaptureSize.getHeight(), RefreshRate.LIVE, false);
        Log.d(LOGTAG, "startVideoSession(): openVideo() is called and video session is started");
    }

    /**
     * Subscribe to {@code OnInferenceCompletedEvent} and handle the inference results upon receiving event.
     * Sends data to the web front-end through {@link RestEndPoint}.
     *
     * @param onInferenceCompletedEvent An {@code OnInferenceCompletedEvent} object.
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(final OnInferenceCompletedEvent onInferenceCompletedEvent) {
        final AccelerationType requestedAccelerationType = EasySharedPreference.getInstance().getRequestedAccelerationType();
        // If the user requested Auto and if the inference was run using GPU, then this will be formatted as "Auto (GPU)".
        final String displayAccelerationType;
        if (requestedAccelerationType == AccelerationType.AUTO) {
            final String actualAccelerationType = mInitializedAccelerationType != null ? mInitializedAccelerationType.toString() : null;
            displayAccelerationType = getString(R.string.acceleration_type_auto_display_format, actualAccelerationType);
        } else {
            displayAccelerationType = requestedAccelerationType.toString();
        }
        final InferenceDTO inferenceDTO = new InferenceDTO(onInferenceCompletedEvent.getInferenceTime(),
            onInferenceCompletedEvent.getFramesProcessedPerSecond(),
            mCapture.getFramerate(),
            displayAccelerationType);

        /*
         * Store the image on which inference was run (containing bounding boxes, if any were detected) in the RestEndPoint class,
         * so that the frontend can retrieve it via a GET call to rest/example/live.
         * Also, store the InferenceDTO, which contains statistics from the inference operation.
         */
        mRestEndPoint.setImageAndStatistics(onInferenceCompletedEvent.getImageAsBytes(), inferenceDTO);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(final OnInferenceCompletedEventBird onInferenceCompletedEventBird) {
        final InfoImageDTO infoImageDTO = new InfoImageDTO("Jhon Test2");
        mRestEndPoint.setInfoForImage(onInferenceCompletedEventBird.getImageAsBytes(), infoImageDTO);
    }

    /**
     * Subscribe to OnObjectDetectorInitializedEvent
     * @param onObjectDetectorInitializedEvent {@code OnObjectDetectorInitializedEvent} object
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(final OnObjectDetectorInitializedEvent onObjectDetectorInitializedEvent) {
        /*
         * Store the {@code AccelerationType} that the ObjectDetector was initialized with. When {@link AccelerationType#AUTO}
         * is requested, this value equals the optimal {@code AccelerationType} that the ObjectDetector was initialized with.
         */
        mInitializedAccelerationType = onObjectDetectorInitializedEvent.getAccelerationType();
        mRestEndPoint.setUserPreferencesStatusDTO(
            new UserPreferencesStatusDTO(getString(R.string.object_detector_initialization_success,
            onObjectDetectorInitializedEvent.getAccelerationType().toString()), true)
        );
    }

    /**
     * Subscribe to OnObjectDetectorInitializationFailedEvent
     * @param onObjectDetectorInitializationFailedEvent {@code OnObjectDetectorInitializationFailedEvent} object
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(final OnObjectDetectorInitializationFailedEvent onObjectDetectorInitializationFailedEvent) {
        mInitializedAccelerationType = null;
        mRestEndPoint.setUserPreferencesStatusDTO(
            new UserPreferencesStatusDTO(getString(R.string.object_detector_initialization_error,
            onObjectDetectorInitializationFailedEvent.getAccelerationType().toString(),
            onObjectDetectorInitializationFailedEvent.getErrorMessage()), false)
        );
    }

    /**
     * Configures the TFLite detector with the most recently chosen preferences.
     */
    private void configureDetector() {
        /* Send a message to the InferenceThread to configure the detector.
         * It is redundant to send multiple messages to configure the detector.
         */
        if (!mInferenceHandler.hasMessages(InferenceHandler.Message.CONFIGURE_DETECTOR.ordinal())) {
            mInferenceHandler.obtainMessage(InferenceHandler.Message.CONFIGURE_DETECTOR.ordinal()).sendToTarget();
        }
    }

    /**
     * This callback is triggered when the {@code VideoManager} is available to request video sessions.
     *
     * @param manager The {@code VideoManager} object that can be used to request video sessions from the VideoPipeline.
     */
    @Override
    @SuppressWarnings("MagicNumber")
    protected void onVideoAvailable(final VideoManager manager) {
        // Gets a default VideoCapture instance which does not scale, rotate, or modify the images received from the VideoPipeline.
        mCapture = manager.getDefaultVideoCapture();
        Log.d(LOGTAG, String.format("getDefaultVideoCapture() with width %d and height %d",
            mCapture.getWidth(), mCapture.getHeight()));
        /*
         * Request from the VideoPipeline only images of HD resolution (1920 * 1080).
         * With lower resolutions, image manipulations and rendering will be much more performant than dealing with UHD images.
         */
        mCaptureSize = new Size(mCapture.getWidth() / 2, mCapture.getHeight() / 2);
        // Starts the InferenceThread
        startInferenceThread();
        // Configures the detector
        configureDetector(); // It is not necessary for other model right now ****
        // Send an event to start the video session.
        new StartVideoSessionActionEvent().broadcastEvent();
    }

    /**
     * Starts the InferenceThread and assigns a handler to the thread.
     * This thread is primarily responsible for configuring the detector and running inference on the images from the VideoPipeline.
     */
    private void startInferenceThread() {
        mInferenceHandlerThread = new HandlerThread(INFERENCE_THREAD_NAME);
        mInferenceHandlerThread.start();
        mInferenceHandler = new InferenceHandler(mInferenceHandlerThread.getLooper(), mCaptureSize);
    }

    /**
     * Stops (waits until all the pending messages are processed) the InferenceThread.
     */
    private void stopInferenceThread() {
        mInferenceHandler = null;
        if (mInferenceHandlerThread != null) {
            mInferenceHandlerThread.quitSafely();
            mInferenceHandlerThread = null;
        }
    }

    /**
     * Callback is triggered when a new {@link Image} is available.
     *
     * @param reader The {@link ImageReader} object that can be used to obtain still frames from the VideoPipeline.
     */
    @Override
    protected void onImageAvailable(final ImageReader reader) {
        /*
         * Gets the latest image from the VideoPipeline
         */
        try (Image image = reader.acquireLatestImage()) {
            // ImageReader may sometimes return a null image.
            if (image == null) {
                Log.e("onImageAvailable()", "ImageReader returned null image.");
                return;
            }
            if (mInitializedAccelerationType == null) {
                /*
                 * Since the detector was not initialized, inference can't be run.
                 * Store the image (without running inference) in the RestEndPoint class,
                 * so that the frontend can retrieve it via a GET call to rest/example/live.
                 * Also, send null for the InferenceDTO since inference was not run on the image.
                 */
                mRestEndPoint.setImageAndStatistics(BitmapUtils.compressBitmap(BitmapUtils.imageToBitmap(image)), null);
                mRestEndPoint.setInfoForImage(BitmapUtils.compressBitmap(BitmapUtils.imageToBitmap(image)), null);
                return;
            }
            /*
             * Without this check, the queue will be filled with Bitmaps at a rate that will be much higher
             * than the rate in which the queue will be emptied (as inference takes longer to complete). This ultimately will lead to OOM.
             * That's why it is important to insert frames into the queue, only when the queue does not contain any other frames.
             */
            if (!mInferenceHandler.hasMessages(InferenceHandler.Message.RUN_INFERENCE.ordinal())) {
                final Bitmap imageBmp = BitmapUtils.imageToBitmap(image);
                // Sends message to InferenceThread for running inference
                mInferenceHandler.obtainMessage(InferenceHandler.Message.RUN_INFERENCE.ordinal(), imageBmp).sendToTarget();
            }
        }
    }

    /**
     * This callback would handle all tear-down logic, and is called when the {@link VideoSession} is stopped.
     * Five possible ways for the video session to be stopped are:
     *
     * 1. {@link VideoSession.CloseReason#SESSION_CLOSED}
     * 2. {@link VideoSession.CloseReason#VIRTUAL_CAMERA_CONFIGURATION_CHANGED}
     * 3. {@link VideoSession.CloseReason#VIRTUAL_CAMERA_CONFIGURATION_REMOVED}
     * 4. {@link VideoSession.CloseReason#RENDERING_FAILED}
     * 5. {@link VideoSession.CloseReason#BASE_CAMERA_CONFIGURATION_CHANGED}
     *
     * @param reason The reason that the video session has been stopped.
     */
    @Override
    @SuppressWarnings("MagicNumber")
    protected void onVideoClosed(final VideoSession.CloseReason reason) {
        Log.i(LOGTAG, "onVideoClosed(): reason " + reason.name());
        /*
         * In API level v5 and above, a new {@link VideoSession.CloseReason.BASE_CAMERA_CONFIGURATION_CHANGED} was
         * introduced to indicate that the VideoPipeline configuration (for example, camera is rotated) has been changed.
         * In these situations, it is recommended to re-start the video session to provide seamless user experience.
         */
        if (Build.VERSION.MAX_API >= 5) {
            if (reason == VideoSession.CloseReason.BASE_CAMERA_CONFIGURATION_CHANGED) {
                Log.i(LOGTAG, "onVideoClosed(): Triggering the restart of the video session that got closed due to " + reason.name());
                /*
                 * The video session is restarted due to base camera configuration changed
                 * in the main thread for proper rendering
                 */
                new StartVideoSessionActionEvent().broadcastEvent();
            }
        }
    }

    /**
     * This callback is triggered when the application is stopped.
     * This can be used to clean up resources, shut down services, etc.
     */
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopInferenceThread();
        mWebServerConnector.disconnect();
        super.onDestroy();
    }
}

