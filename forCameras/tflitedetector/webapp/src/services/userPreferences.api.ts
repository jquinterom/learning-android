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

import { AccelerationType, UserPreferencesDTO, UserPreferencesStatusDTO } from "@/interfaces";
import { AxiosRequestConfig, AxiosResponse } from "axios";
import httpClient from "./httpClient";

const getUserPreferences = (): Promise<AxiosResponse<UserPreferencesDTO>> =>
  httpClient.get<UserPreferencesDTO>("user-preferences");
const postUserPreferences = (
  userPreferences: UserPreferencesDTO,
  config?: AxiosRequestConfig
): Promise<AxiosResponse<UserPreferencesStatusDTO>> =>
  httpClient.post<UserPreferencesStatusDTO>("user-preferences", userPreferences, {
    ...config,
    timeout: 30000,
    /*
     * This endpoint needs a timeout value longer than the usual (1000) as initializing the detector with certain acceleration types
     * (e.g. GPU) can take longer than one second.
     */
  });
const getAccelerationTypes = (): Promise<AxiosResponse<AccelerationType[]>> =>
  httpClient.get<AccelerationType[]>("acceleration-types");

export { getUserPreferences, postUserPreferences, getAccelerationTypes };
