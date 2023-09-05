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
package com.example.curity.iproov;

import se.curity.identityserver.sdk.service.SessionManager;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SCANNED_DOCUMENT;

public final class Utils
{
    public static void cleanup(SessionManager sessionManager)
    {
        sessionManager.remove(SCANNED_DOCUMENT).getOptionalValueOfType(String.class);
    }

    public static String stripLastPathPart(String fullPath) throws MalformedURLException
    {
        String urlPath = new URL(fullPath).getPath();
        return urlPath.substring(0,urlPath.lastIndexOf('/'));
    }
}
