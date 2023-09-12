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

import com.example.curity.iproov.config.IProovAuthenticationActionConfig;
import com.google.gson.Gson;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.*;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionContext;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult;
import se.curity.identityserver.sdk.service.*;

import java.util.Map;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SCANNED_DOCUMENT;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SessionKeys.SESSION_KEY;
import static com.example.curity.iproov.IProovAuthenticationActionConstants.SubjectAttributes.SCAN_ATTRIBUTES;
import static com.example.curity.iproov.Utils.cleanup;
import static se.curity.identityserver.sdk.authenticationaction.completions.RequiredActionCompletion.PromptUser.prompt;

public final class IProovAuthenticationAction implements AuthenticationAction
{
    private final SessionManager _sessionManager;
    private static final Gson gson = new Gson();

    public IProovAuthenticationAction(IProovAuthenticationActionConfig configuration)
    {
        _sessionManager = configuration.getSessionManager();
    }

    @Override
    public AuthenticationActionResult apply(AuthenticationActionContext context)
    {
        @Nullable Attribute attributeView = _sessionManager.get(SESSION_KEY);

        if (attributeView != null)
        {
            Map<String, Object> frontendAttributes = gson.fromJson(_sessionManager.get(SCANNED_DOCUMENT).getValueOfType(String.class), Map.class);
            Map<String, Object> backendAttributes = gson.fromJson(_sessionManager.get(SCAN_ATTRIBUTES).getValueOfType(String.class), Map.class);
            backendAttributes.remove("frame"); //remove the captured frame from the attributes
            frontendAttributes.putAll(backendAttributes);

            cleanup(_sessionManager);
            _sessionManager.remove(SESSION_KEY);

            ContextAttributes contextAttributes = context.getAuthenticationAttributes().getContextAttributes();

            SubjectAttributes subjectAttributes = SubjectAttributes.of(context.getAuthenticationAttributes().getSubjectAttributes());

            return AuthenticationActionResult.successfulResult(AuthenticationAttributes.of(subjectAttributes.append(Attributes.fromMap(frontendAttributes)),contextAttributes));
        }

        return AuthenticationActionResult.pendingResult(prompt());
    }
}
