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
  <div class="wrapper">
    <div class="info-item">
      <!-- font-weight-bold is a Vuetify Typography helper class.
      https://vuetifyjs.com/en/styles/text-and-typography/#font-emphasis -->
      <span class="font-weight-bold">Acceleration Type:</span> {{ accelerationType }}
    </div>
    <div class="info-item">
      <span class="font-weight-bold">FPS:</span>
      {{ fps >= 0 ? fps : "N/A" }}/{{ requestedFps }}
      <v-tooltip bottom content-class="tooltip-content">
        <template v-slot:activator="{ on, attrs }">
          <v-icon small v-on="on" v-bind="attrs">$informationOutline</v-icon>
        </template>
        <span
          >Rendered FPS({{ fps }}): The average FPS rendered ( image conversion, inferencing and post processing ) by
          the app.<br />Requested FPS({{ requestedFps }}): The FPS requested by the app from the Azena
          VideoPipeline.</span
        >
      </v-tooltip>
    </div>
    <div class="info-item">
      <span class="font-weight-bold">Inference Time:</span>
      {{ inferenceTime >= 0 ? `${inferenceTime} ms` : "N/A" }}
      <v-tooltip bottom content-class="tooltip-content">
        <template v-slot:activator="{ on, attrs }">
          <v-icon small v-on="on" v-bind="attrs">$informationOutline</v-icon>
        </template>
        <span>The time in ms taken by the model to run inference on an image frame.</span>
      </v-tooltip>
    </div>
  </div>
</template>

<!-- The script element contains the Typescript code -->
<script lang="ts">
import Vue from "vue";
const Info = Vue.extend({
  name: "Info",
  props: {
    accelerationType: {
      type: String,
      required: true,
    },
    fps: {
      type: Number,
      required: true,
    },
    requestedFps: {
      type: Number,
      required: true,
    },
    inferenceTime: {
      type: Number,
      required: true,
    },
  },
});
export default Info;
</script>

<!-- The style element contains the SCSS styles -->
<style scoped lang="scss">
.wrapper {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-content: center;
  justify-content: center;
}

.info-item {
  display: inline-block;
  margin: 12px;
}

/* Tooltip styling */
.tooltip-content {
  padding: 10px;
  border: 1px solid #dfdfdf;
  border-radius: 0px 6px 6px 6px;
  background: #ffffff;
  color: #4b4c5b;
  box-shadow: 0px 2px 6px rgba(115, 125, 155, 0.16), 1px 8px 16px rgba(176, 192, 237, 0.16);
  opacity: 1 !important;
}
</style>
