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
  <v-app>
    <!-- The colon is shorthand for the v-bind directive. This directive is used for property binding. 
    https://vuejs.org/v2/api/#v-bind 
    Here, the title prop of Toolbar is bound to the title property inside the App class below. -->
    <Toolbar :title="title" :drawer.sync="drawer" />
    <!-- The Toolbar and NavDrawer need two-way binding (rather than one-way property binding) with the drawer state, so the .sync modifier is added. 
    This means that if the NavDrawer internally sets drawerState to some other value, it will be updated here as well. 
    https://vuejs.org/v2/guide/components-custom-events.html#sync-Modifier -->
    <NavDrawer :drawerState.sync="drawer" :drawerItems="drawerItems" />
    <!-- Register a ref of this v-main to the App component class for its ResizeObserver. -->
    <v-main ref="mainContent">
      <!-- Use a style object to set CSS properties. -->
      <div :style="contentStyle">
        <!-- The router-view is populated with the Home.Vue component when the app starts. -->
        <router-view />
      </div>
    </v-main>
    <Footer />
  </v-app>
</template>

<!-- The script element contains the Typescript code -->
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Toolbar, NavDrawer, Footer } from "@azena/jumpsuite";

const SMALL_PADDING_BREAKPOINT = 720;

@Component({
  components: { Toolbar, NavDrawer, Footer },
})
export default class App extends Vue {
  // Set drawer state to null initially, the NavDrawer itself will set it to true or false depending on viewport width. True will display, false will hide the NavDrawer from view.
  private drawer = null;
  // Title of app.
  private title = "";
  // Style object for main content, https://vuejs.org/v2/guide/class-and-style.html#Object-Syntax-1
  private contentStyle = {
    width: "100%",
    // Top and bottom padding is 0, right and left is 48px. https://developer.mozilla.org/en-US/docs/Web/CSS/padding
    padding: "0 48px",
  };
  // Navigation items for the NavDrawer to display.
  private drawerItems = [
    {
      title: "Live Stream",
      icon: "$playBoxOutline",
      to: "/",
    },
    {
      title: "Settings",
      icon: "$cogOutline",
      to: "/settings",
    },
  ];
  /**
   * Vue lifecycle hook for when this component has been mounted to the DOM. Used to start the rendering loop.
   */
  mounted(): void {
    this.title = process.env.VUE_APP_WEB_PAGE_TITLE;
    /* If the width of the content is smaller than a certain value, use a smaller padding.
       This observation will fire when the element is inserted into / removed from DOM, resized etc.
       NOTE: This is a container query, based on the size of an element, rather than the entire screen (i.e. media query). */
    const ro = new ResizeObserver((entries) => {
      for (let entry of entries) {
        if (entry.contentRect.width < SMALL_PADDING_BREAKPOINT) {
          this.contentStyle.padding = "0 24px";
        } else {
          this.contentStyle.padding = "0 48px";
        }
      }
    });
    // Observe size changes to the mainContent's element.
    ro.observe((this.$refs.mainContent as Vue).$el);
  }
}
</script>

<!-- The style element contains the SCSS styles -->
<style scoped lang="scss"></style>
