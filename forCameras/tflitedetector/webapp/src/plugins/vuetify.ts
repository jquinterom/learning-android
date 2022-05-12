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
import Vue from "vue";
import Vuetify from "vuetify/lib";
/**
 * Needed for any text that is put on any of the web pages
 */
import "roboto-fontface/css/roboto/roboto-fontface.css";
// Need to import each icon we're using inside the app which is not a default icon in the mdiSvg iconfont
import { mdiPlayBoxOutline, mdiCogOutline, mdiInformationOutline } from "@mdi/js";
import { COLOR } from "@/services/color";
Vue.use(Vuetify);
/**
 * Sets up Vue to use Vuetify
 */
export default new Vuetify({
  theme: {
    themes: {
      light: {
        primary: COLOR.primary,
        secondary: COLOR.accent,
        accent: COLOR.accent,
        error: COLOR.error,
        info: COLOR.info,
        success: COLOR.success,
        warning: COLOR.warning,
        grey: COLOR.grey,
        black: COLOR.black,
      },
    },
  },
  icons: {
    iconfont: "mdiSvg",
    // Use these values in a v-icon with a dollar sign in the front (shortcut to $vuetify.icons), like so: <v-icon>$playBoxOutline</v-icon>
    values: {
      playBoxOutline: mdiPlayBoxOutline,
      cogOutline: mdiCogOutline,
      informationOutline: mdiInformationOutline,
    },
  },
  options: { customProperties: true }, // makes it possible to use the colors above in CSS like: "color: var(--v-primary-base);"
});
