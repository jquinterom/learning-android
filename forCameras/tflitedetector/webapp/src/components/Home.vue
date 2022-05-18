<!--
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
-->

<!-- The template element contains the HTML code -->
<template>
  <!-- mx-auto and my-11 are Vuetify's spacing helper classes. https://vuetifyjs.com/en/styles/spacing/#how-it-works
  mx-auto means margin-left and margin-right are set to auto, my-12 means margin-top and margin-bottom are set to 12*4px = 48px -->
  <div style="width: 100%; max-width: 1440px" class="mx-auto my-12">
    <!-- text-h1 is a Vuetify Typography helper class that sets the size and style of text according to Material Design.
    https://vuetifyjs.com/en/styles/text-and-typography/#typography -->
    <div class="text-h1 mb-6">Objective</div>
    <div style="max-width: 790px" class="text-body-1 mb-16">
      Retrieve an image from the VideoPipeline, process it using a TensorFlow Lite detector and display the image on the
      Web UI.
    </div>
    <div class="text-h1 mb-6">Live stream</div>
    <template v-for="(error, index) in [liveViewError, inferenceStatisticsError, userPreferencesStatusError]">
      <v-alert v-if="!!error" :key="index" class="mb-6" type="error" dense text icon="$cancel">
        {{ error }}
      </v-alert>
    </template>
    <template v-if="!!userPreferencesStatus">
      <v-alert class="mb-6" type="error" dense text icon="$cancel" :value="!userPreferencesStatus.isSuccessful">
        <span v-html="userPreferencesStatus.message"></span>
      </v-alert>
    </template>
    <img width="100%" :src="liveViewUrl"/>
    <Info
      v-if="!!inferenceStatistics"
      :accelerationType="inferenceStatistics.accelerationType"
      :fps="inferenceStatistics.framesProcessedPerSecond"
      :requestedFps="inferenceStatistics.requestedFramesPerSecond"
      :inferenceTime="inferenceStatistics.inferenceTime"
      :name-bird="infoImage.imageName"
    />
  </div>
</template>

<!-- The script element contains the Typescript code -->
<script lang="ts">
import {Component, Vue} from "vue-property-decorator";
import Info from "./Info.vue";
import {getInferenceStatistics, getUserPreferencesStatus, getInfoImage} from "@/services/home.api";
import {InferenceDTO, UserPreferencesStatusDTO, InfoImageDTO} from "@/interfaces";

/**
 * Maps a new vue component called home to this file
 */
@Component({
  name: "home",
  components: {Info},
})
export default class Home extends Vue {
  private inferenceStatistics: InferenceDTO | null = null;
  private infoImage: InfoImageDTO | null = null;
  /**
   * Points to the rest/example/live endpoint
   */
  private liveViewUrl = "";
  /**
   * If the rest/example/live endpoint fails to obtain an image, this error message will be shown in a v-alert.
   */
  private liveViewError = "";

  private inferenceStatisticsError = "";

  private userPreferencesStatusError = "";

  private infoImageStatusError = "";

  private userPreferencesStatus: UserPreferencesStatusDTO | null = null;

  /**
   * Rendering loop. Continously calls itself to display live images from the backend while the home vue component is active.
   */
  private retrieveImage() {
    getInferenceStatistics()
      .then((res) => {
        this.inferenceStatistics = res.data;
        this.inferenceStatisticsError = "";
      })
      .catch((error) => {
        this.inferenceStatistics = null;
        if (error?.toJSON().message === "Network Error") {
          this.inferenceStatisticsError = "A network error occurred while attempting to get inference statistics.";
        } else {
          this.inferenceStatisticsError = "Something went wrong while attempting to get inference statistics.";
        }
      });
    getUserPreferencesStatus()
      .then((res) => {
        this.userPreferencesStatus = res.data;
        this.userPreferencesStatusError = "";
      })
      .catch((error) => {
        this.userPreferencesStatus = null;
        if (error?.toJSON().message === "Network Error") {
          this.userPreferencesStatusError = "A network error occurred while attempting to get user preferences status.";
        } else {
          this.userPreferencesStatusError = "Something went wrong while attempting to get user preferences status.";
        }
      });
    getInfoImage()
      .then((res) => {
        this.infoImage = res.data;
        this.infoImageStatusError = ""
      })
      .catch((error) => {
        this.infoImage = null;
        if (error?.toJSON().message === "Network Error") {
          this.infoImageStatusError = "A network error occurred while attempting to get user preferences status.";
        } else {
          this.infoImageStatusError = "Something went wrong while attempting to get info from image.";
        }
      });
    const url = "rest/example/live?time=" + Date.now();
    const img = new Image();
    img.onload = () => {
      this.liveViewUrl = url;
      this.liveViewError = "";

      /**
       * If the route is no longer at the home component then the rendering loop should be interupted.
       */
      if (this.$route.name == "home") {
        window.requestAnimationFrame(this.retrieveImage);
      }
    };
    img.onerror = () => {
      this.liveViewError = "Unable to acquire video stream!";
      /**
       * If we encounter error screen, retry in 500 ms
       */
      setTimeout(() => this.retrieveImage(), 500);
    };
    img.src = url;
  }

  /**
   * Vue lifecycle hook for when this component has been mounted to the DOM. Used to start the rendering loop.
   */
  mounted(): void {
    /**
     * Calls the retrieveImage() function, which obtains an image from the rest/example/live endpoint, and continuously calls itself to update the image being displayed
     * in the <img> tag
     */
    this.retrieveImage();
  }
}
</script>

<!-- The style element contains the SCSS styles -->
<style scoped lang="scss"></style>
