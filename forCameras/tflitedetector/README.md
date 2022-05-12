# TFLiteDetector

This app shows how to

1.  Use an Image from the VideoPipeline and process it using a [TensorFlow lite](https://www.tensorflow.org/lite/guide) detector.
2.  Create a web user interface that
    - displays the images processed by the detector, and 
    - allows the user to configure the confidence threshold and the accelerator type.
3. Add and configure accelerators to improve performance of TFLite inference. Adding the accelerators requires extra steps as outlined [here](#delegates).
4. Offload time consuming tasks (like configuring the detector or running inference on an image) to a background thread, and pass the results back to the UI thread.

This document assumes that [OpenJDK 8 is installed](https://openjdk.java.net/install/).

## Build the app
### Pre-requisites
1. #### Tell gradle to use the Azena SDK
   Set the following environment variables:
    ```
    export ANDROID_SDK_ROOT=<path_to_azena_sdk>
    export ANDROID_NDK_HOME=<path_to_azena_sdk>/ndk-bundle

2. #### Set up credentials for Azena's maven repository
   Create a file called `gradle.properties` and populate it with your credentials for [Azena's developer documentation](https://docs.azena.com/) as follows:
   ```
   sstUsername=John.Doe@example.com
   sstPassword=MySecurePassword
   ```
   Place this file in your home directory `.gradle` folder.
   - On Linux, put this file under `~/.gradle/gradle.properties`
   - On Windows, put this file under `%USERPROFILE%/.gradle/gradle.properties`.

3. #### Setup npm registry for Azena's npm packages
   In your home directory, create a file called `.npmrc` and populate it with your credentials for [Azena's developer documentation](https://docs.azena.com/) as follows:

   ```
   @azena:registry=https://artifacts.azena.com/repository/npm/
   //artifacts.azena.com/repository/npm/:username=John.Doe@example.com
   //artifacts.azena.com/repository/npm/:email=John.Doe@example.com
   # Password "MySecurePassword" must be base64-encoded before adding to this file.
   //artifacts.azena.com/repository/npm/:_password=TXlTZWN1cmVQYXNzd29yZA==
   ```
   ##### How to base64-encode your password
   * On Linux/Mac, run the following command:
     ```
     echo -n "MySecurePassword" | openssl base64
     ```
   * On Windows, run the following commands:
     ```
     echo|set/p="MySecurePassword" >temp.txt
     certutil -encodehex temp.txt out.txt 0x40000001
     type out.txt
     del temp.txt out.txt
     ```
           
To build the app, run:

    ./gradlew assembleDebug
    

### Install and run on Device

1. Obtain the generated APK file from: 

       ./app/build/outputs/apk/debug/app-debug.apk

2. Install the apk files on an Azena Device using ADB
      
        adb install -r -g ./app/build/outputs/apk/debug/app-debug.apk
        
3. From a web browser, visit `https://<ip_address_of_camera>:8443`.
4. From the menu on the left pane, select `Applications -> Overview`. 
5. From the apps listed on the right pane, find the app `TFLiteDetector` and select `App interface and configurations`.
6. Verify the video is being streamed.
7. Verify that objects are being detected and that bounding boxes are drawn.

### Generating a release APK

In order to generate the release APK, be sure to have your signing configuration setup as follows:

1. The following environment variables must be defined, for example:

        export SIGNING_KEY_ALIAS=keyalias # Choose any name for the signing key
        export SIGNING_KEY_PASSWORD=signingkeypassword # Choose a password for the signing key
        export SIGNING_KEYSTORE_PATH=~/key_name.keystore # Choose a path to store your keystore
        export SIGNING_KEYSTORE_PASSWORD=keystorepassword # Choose a password for the keystore

2. Run the following command to generate the keystore:

        keytool -genkey -v -keystore $SIGNING_KEYSTORE_PATH \
        -alias $SIGNING_KEY_ALIAS -keyalg RSA \-keysize 2048 \
        -validity 10000 -storepass $SIGNING_KEYSTORE_PASSWORD \
        -keypass $SIGNING_KEY_PASSWORD

    For more information on `keytool`, please refer to `man keytool` or [Oracle's keytool documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html)

3. From the terminal run:

        ./gradlew assembleRelease

### Model Selection
Identify the model that will be used in your app. For this example we used the [ssd_mobilenet_v1_quantized](https://tfhub.dev/tensorflow/lite-model/ssd_mobilenet_v1/1/default/1) model that comes from the TensorFlow Lite [Object Detection](https://www.tensorflow.org/lite/models/object_detection/overview) examples. The model needs two files:
* [ssd_mobilenet_v1_1_default_1.tflite](https://tfhub.dev/tensorflow/lite-model/ssd_mobilenet_v1/1/default/1?lite-format=tflite)
  * Download `ssd_mobilenet_v1_1_default_1.tflite` and rename the file to `detect.tflite`
  * This is the model file we will use to run *inference*
    * Inference is the process through which a model creates output for an input. In our case the input is an image and the output is a set of bounding boxes, labels, and confidence values.
* [lite-model_ssd_mobilenet_v1_1_metadata_2.tflite](https://tfhub.dev/tensorflow/lite-model/ssd_mobilenet_v1/1/metadata/2?lite-format=tflite)
  * Download and extract the archive to get the `labelmap.txt` file.
  * This provides the mapping from the numeric output of the model file to human readable categories (i.e. person, car, chair, etc.). For this model, if `n` is the output class prediction, then the label in row `n` of `labelmap.txt` is the human readable label.

Be sure to move `detect.tflite` to `./app/src/main/assets/`. Also, move `labelmap.txt` to `./app/src/main/res/raw/`. Create these directories if they do not already exist.

### MainService
The UI Thread/Main Thread is primarily responsible for the following taks:

1. **Starting the video session:** It starts the video session that requests HD (1920 * 1080) frames from the VideoPipeline at a rate of LIVE (~30 FPS).
2. **Displaying the results of the inference:** It sends the image alongside inference statistics and stores them in the RestEndPoint class so that the frontend can retrieve it via a GET call to rest/example/live, and rest/example/inference-statistics, respectively.

### InferenceThread
This thread is responsible for performing the time-consuming tasks related to running inference on an image:

1. **Configuring the detector:** Whenever the accelerator is changed, the detector must be re-configured. Re-configuring the detector also requires the image manipulation parameters to be re-calculated.
2. **Initializing the Renderer:** When the detector is changed, the image manipulation parameters also change requiring the Renderer needs to be (re-)initialized with the new values.
3. **Running the inference:** As soon as we receive images from the VideoPipeline, we pass those images to the InferenceThread, for running [inference](#inference).
4. **Rendering the results of the inference:**: It filters the detections based on the confidence level and shows the [processed](#image-postprocessing) result on the image with the help of [Renderer](#renderer). 

### Configuring the detector
The detector is configured using an [ObjectDetectorBuilder](./app/src/main/java/com/securityandsafetythings/examples/tflitedetector/detector/ObjectDetectorBuilder.java). The `ObjectDetectorBuilder` provides setters to configure the detector as per the choice of model and builds an `ObjectDetector` initialized with those values:
```java
ObjectDetector mDetector = new ObjectDetectorBuilder()
      // filename of the model stored in the assets folder of the app
      .setModelFileName("detect.tflite")
      // Resource id of the label file that the model uses. The labels file is kept in the resources folder of the app
      .setLabelFileResourceId(R.raw.labelmap)
      // Input size of the detector
      .setInputSize(300)
      // The type of acceleration you would like the detector to use
      .setAccelerationType(EasySharedPreference.getInstance().getRequestedAccelerationType())
      // true/false denoting whether the model is a quantized or floating point model
      .setIsQuantized(true)
      // Configures the detector to use 16 bit precision for 32 bit values to save on space
      .allowFp16PrecisionForFp32()
      .build();
```
#### ObjectDetector
[ObjectDetector](./app/src/main/java/com/securityandsafetythings/examples/tflitedetector/detector/ObjectDetector.java) is the class responsible for [preparing](#resourcehelper) and accessing a model. It also allows to [configure delegates](#delegates), to take advantage of different acceleration modes like GPU, Hexagon DSP and NNAPI. It is based on the [TFLiteObjectDetectionAPIModel](https://github.com/tensorflow/examples/blob/32b67f3f42aaa1e11e9daf0a8a32e67cc8fb7579/lite/examples/object_detection/android/app/src/main/java/org/tensorflow/lite/examples/detection/tflite/TFLiteObjectDetectionAPIModel.java) provided by the TensorFlow team.

It provides the following API:
1. To run inference on a `Bitmap` retrieved from the VideoPipeline, use
   ```java
   public List<Recognition> recognizeImage(final Bitmap bitmap)
   ```
2. To get the size of the image as required by the detector, use
   ```java
   public Size getRequiredImageSize()
   ```
   This API is useful to determine how the raw images will be processed. More information on that is available [here](#image-preprocessing).

#### Delegates
Delegates enable acceleration of TensorFlow Lite models by utilizing on-device accelerators like the GPU and DSP. The accelerator performance will likely vary depending on the specific hardware available on device.

##### Hexagon Delegate
The steps for downloading and extracting the required libraries for Hexagon Delegate is as follows:
1. Download [hexagon_nn_skel.run](https://storage.cloud.google.com/download.tensorflow.org/tflite/hexagon_nn_skel_v1.20.0.1.run).
2. Open the terminal and move to the folder in which you've saved your RUN file.
3. Use the following command to make your RUN file executable:

        chmod +x yourfilename.run

4. Use the following command to execute your RUN file:

        ./yourfilename.run

3. When prompted, type "I ACCEPT", to agree to the terms of the license.
4. A new folder will be created in the same location. It should provide 3 different shared libraries “libhexagon_nn_skel.so”, “libhexagon_nn_skel_v65.so”, “libhexagon_nn_skel_v66.so”
5. [Include](./app/src/main/jniLibs/arm64-v8a) all 3 shared libraries in the app.

#### ResourceHelper
[ResourceHelper](./app/src/main/java/com/securityandsafetythings/examples/tflitedetector/utilities/ResourceHelper.java) is a helper class responsible for loading the model and its labels from the app's resources.

It provides two methods:
1. `loadModelFile`
2. `loadLabels`

### Running inference on the image
#### Image preprocessing
The model used in this app operates on 300x300 pixel images. The images we obtain from the pipeline are of HD (1920x1080) resolution. We would not want to use just a small 300x300 crop of our preview image. Instead what we can do is crop the full resolution image to the same aspect ratio as our model (1:1) and then rescale the cropped image to the size our detector accepts. This way the resize operation does not warp the input, though this warping is tolerable for some models, we won't use it now. Refer to `prepareForInference` and `handleRunningInference` right up until `recognizeImage` is called.

Be sure to reference the [Bitmap](https://developer.android.com/reference/android/graphics/Bitmap) documentation as well.

#### Running inference
For every bit as complex as image preprocessing is, inference is simple:
```java
/*
 * Perform classification using the detector
 */
final List<Recognition> detectionResults = mDetector.recognizeImage(croppedBitmap);
```

### Displaying bounding boxes on the image
After we have generated a list of `Recognition` objects, we want to display them over our image. To do this, we utilize the [Renderer](#renderer) helper class. It performs all necessary rescaling and drawing on the image at the original HD resolution.
```java
/*
 * Utility class renders the detection bounding boxes on imageBmp. We pass the base image size along with the
 * filtered detection results based on the confidence level.
 */
mRenderer.render(new Canvas(imageBmp), detectionResults);
```
Finally, send the processed image to the rest endpoint just like in `Helloworld`

#### Renderer
[Renderer](./app/src/main/java/com/securityandsafetythings/examples/tflitedetector/utilities/Renderer.java) is a utility class that performs drawing and manages paints. It even shades areas that were not used by our detector.

A rectangle is drawn on each detected object. The color of said rectangle is determined by the label of the object so that each class has a distinct color. The text label and confidence are rendered at the top of each rectangle as well.

## References
1. `Helloworld` app
2. [ObjectDetector](#objectdetector) is based on the TensorFlow lite example 
[TFLiteDetectionAPIModel](https://github.com/tensorflow/examples/blob/32b67f3f42aaa1e11e9daf0a8a32e67cc8fb7579/lite/examples/object_detection/android/app/src/main/java/org/tensorflow/lite/examples/detection/tflite/TFLiteObjectDetectionAPIModel.java)
3. [DetectorActivity](https://github.com/tensorflow/examples/blob/master/lite/examples/object_detection/android/app/src/main/java/org/tensorflow/lite/examples/detection/DetectorActivity.java)
was used as reference for parts of the image preprocessing and rendering
4. More info on TFLite Object detection can be found at the [Object Detection Overview](https://www.tensorflow.org/lite/models/object_detection/overview)
