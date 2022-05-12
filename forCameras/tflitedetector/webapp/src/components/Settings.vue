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
  <div style="width: 100%; max-width: 1440px" class="mx-auto my-12">
    <div style="max-width: 790px">
      <div class="text-h1 mb-12">Settings</div>
      <template v-if="!!userPreferencesStatus">
        <v-alert
          class="mb-6"
          :type="userPreferencesStatus.isSuccessful ? 'success' : 'error'"
          :icon="userPreferencesStatus.isSuccessful ? '$success' : '$cancel'"
          dense
          text
        >
          <span class="dynamic-text" v-html="userPreferencesStatus.message"></span>
        </v-alert>
      </template>
      <div class="text-h2 font-weight-medium mb-2">Confidence threshold</div>
      <div class="mb-12">
        The confidence of the detection must be equal or above this value for the bounding boxes to be displayed.
      </div>
      <v-slider
        v-model="slider"
        :min="min"
        :max="max"
        :step="this.step"
        hide-details
        :value="slider"
        thumb-label="always"
        class="confidence-slider mt-8 mb-12"
      >
      </v-slider>
      <div class="text-h2 font-weight-medium mb-2">Acceleration type</div>
      <div class="mb-6">Select one of these types to accelerate your inference operations.</div>
      <template v-if="!!accelerationTypesResult">
        <v-alert class="mb-6" type="error" dense text icon="$cancel" :value="!accelerationTypesResult.isSuccessful">
          {{ accelerationTypesResult.message }}
        </v-alert>
      </template>
      <v-radio-group v-model="selectedAccelerationType" dense class="accelerationDivRadioGroup mb-8">
        <template v-for="accelerationType in accelerationTypes">
          <v-divider v-if="accelerationType.isModernAPI" class="mb-4" :key="accelerationType.name"></v-divider>
          <v-radio class="mb-2" :key="accelerationType.name" :value="accelerationType.name">
            <template v-slot:label>
              <div class="text--primary font-weight-bold">
                {{ accelerationType.name }}
              </div>
            </template>
          </v-radio>
          <div
            class="grey--text text--darken-3 ml-8 mb-4 dynamic-text"
            v-html="accelerationType.description"
            :key="accelerationType.name"
          ></div>
        </template>
      </v-radio-group>
      <!-- Creates a button that allows the user to post the current selected user preferences to the backend -->
      <v-btn @click="postUserPreferences()" ripple color="primary" width="65px">Save</v-btn>
    </div>
  </div>
</template>

<!-- The script element contains the Typescript code -->
<script lang="ts">
import { AccelerationType, AccelerationTypesResult, UserPreferencesDTO, UserPreferencesStatusDTO } from "@/interfaces";
import { getAccelerationTypes, getUserPreferences, postUserPreferences } from "@/services/userPreferences.api";
import { Component, Vue } from "vue-property-decorator";

@Component({
  name: "Settings",
})
export default class Settings extends Vue {
  private selectedAccelerationType = "None";
  private accelerationTypes: AccelerationType[] = [];
  /**
   * Minimum value for the slider
   */
  private min = 0.0;
  /**
   * Maximum value for the slider
   */

  private max = 1.0;
  /**
   * The current float value of the slider
   */

  private slider = 0;
  /**
   * The step amount between slider values
   */
  private step = 0.01;

  /**
   * The status of the selected user preferences, which will be shown in an alert.
   */
  private userPreferencesStatus: UserPreferencesStatusDTO | null = null;

  /**
   * The result of getAccelerationTypes, which will be shown in an alert.
   */
  private accelerationTypesResult: AccelerationTypesResult | null = null;

  /**
   * Vue lifecycle hook for when component has been mounted to the DOM. Used to get the
   * settings before the UI is rendered so when the user sees the slider, its value
   * will reflect the state of the backend
   */
  mounted(): void {
    this.getUserPreferences();
    this.getAccelerationTypes();
  }

  /**
   * Posts the preferences made by the user to the backend.
   */
  private postUserPreferences() {
    // Reset alert
    this.userPreferencesStatus = null;
    /**
     * Declares the appropriate header for the post request
     */
    let config = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    /**
     * Create and populate the UserPreferencesDTO object that will hold the chosen user preferences
     */
    let userPreferences: UserPreferencesDTO = {
      confidence: this.slider,
      accelerationType: this.selectedAccelerationType,
    };
    /**
     * Post the current selected user preferences to the backend.
     * On success inform the user of success. On failure, inform the
     * user of failure.
     */
    postUserPreferences(userPreferences, config)
      .then((response) => {
        this.userPreferencesStatus = response.data;
      })
      .catch((error) => {
        if (error?.toJSON().message === "Network Error") {
          this.userPreferencesStatus = {
            message: "A network error occurred while attempting to update the settings.",
            isSuccessful: false,
          };
        } else {
          this.userPreferencesStatus = {
            message: "Something went wrong while attempting to update the settings.",
            isSuccessful: false,
          };
        }
      })
      .finally(() => {
        window.scrollTo(0, 0);
      });
  }

  /**
   * Gets the currently selected user preferences from the backend to populate the settings page with.
   */
  private getUserPreferences() {
    getUserPreferences()
      .then((response) => {
        /**
         * If the get is successful. Apply the settings to the UI components
         */
        if (response.data) {
          this.userPreferencesStatus = null;
          let userPreferences: UserPreferencesDTO = response.data;
          this.slider = userPreferences.confidence;
          this.selectedAccelerationType = userPreferences.accelerationType;
        }
      })
      .catch((error) => {
        if (error?.toJSON().message === "Network Error") {
          this.userPreferencesStatus = {
            message: "A network error occurred while attempting to retrieve the settings.",
            isSuccessful: false,
          };
        } else {
          this.userPreferencesStatus = {
            message: "Something went wrong while attempting to retrieve the settings.",
            isSuccessful: false,
          };
        }
      });
  }

  /**
   * Gets the selectable acceleration types to display from the backend.
   */
  private getAccelerationTypes() {
    getAccelerationTypes()
      .then((response) => {
        this.accelerationTypesResult = { isSuccessful: true };
        this.accelerationTypes = response.data;
      })
      .catch((error) => {
        if (error?.toJSON().message === "Network Error") {
          this.accelerationTypesResult = {
            message: "A network error occurred while attempting to get the acceleration types.",
            isSuccessful: false,
          };
        } else {
          this.accelerationTypesResult = {
            message: "Something went wrong while attempting to get the acceleration types.",
            isSuccessful: false,
          };
        }
      });
  }
}
</script>

<!-- The style element contains the SCSS styles -->
<style scoped lang="scss">
.confidence-slider {
  width: 335px;
}

// https://vue-loader.vuejs.org/guide/scoped-css.html#dynamically-generated-content
.dynamic-text::v-deep a {
  color: #5844ee;
  text-decoration: none;
}
</style>
