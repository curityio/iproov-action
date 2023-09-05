/*
 *  Copyright 2023 Curity AB
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
 *
 */

package com.example.curity.iproov.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultString;
import se.curity.identityserver.sdk.config.annotation.Description;

import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.SessionManager;
import se.curity.identityserver.sdk.web.ContainsSensitiveData;

public interface IProovAuthenticationActionConfig extends Configuration {
    SessionManager getSessionManager();

    Json getJson();

    ExceptionFactory getExceptionFactory();

    @Description("The iProov API Key")
    @ContainsSensitiveData
    String getIproovApiKey();

    @Description("The iProov API Secret")
    @ContainsSensitiveData
    String getIproovApiSecret();

    @Description("The iProov Tenant")
    @DefaultString("us.rp.secure.iproov.me")
    String getIproovTenant();

    @Description("The iProov Base URL")
    @DefaultString("/api/v2")
    String getIproovBaseURL();
    HttpClient getHttpClient();
}