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

package com.example.curity.iproov.descriptor;


import com.example.curity.iproov.IProovAuthenticationAction;
import com.example.curity.iproov.config.IProovAuthenticationActionConfig;
import com.example.curity.iproov.handlers.IProovAuthenticationActionFailedRequestHandler;
import com.example.curity.iproov.handlers.IProovAuthenticationActionRequestHandler;
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction;

import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticationActionPluginDescriptor;

import java.util.HashMap;
import java.util.Map;

import static com.example.curity.iproov.IProovAuthenticationActionConstants.Endpoints.*;

public final class IProovAuthenticationActionDescriptor implements AuthenticationActionPluginDescriptor<IProovAuthenticationActionConfig>
{
    @Override
    public Class<? extends AuthenticationAction> getAuthenticationAction()
    {
        return IProovAuthenticationAction.class;
    }

    @Override
    public String getPluginImplementationType()
    {
        return "iproov";
    }

    @Override
    public Class<? extends IProovAuthenticationActionConfig> getConfigurationType()
    {
        return IProovAuthenticationActionConfig.class;
    }

    @Override
    public Map<String, Class<? extends ActionCompletionRequestHandler<?>>> getAuthenticationActionRequestHandlerTypes()
    {
        Map<String, Class<? extends ActionCompletionRequestHandler<?>>> endpoints = new HashMap<>();
        endpoints.put(INDEX, IProovAuthenticationActionRequestHandler.class);
        endpoints.put(CANCEL, IProovAuthenticationActionFailedRequestHandler.class);
        endpoints.put(FAILED, IProovAuthenticationActionFailedRequestHandler.class);
        return endpoints;
    }
}
